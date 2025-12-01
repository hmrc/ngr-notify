/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngrnotify.connectors.bridge

import play.api.http.Status as HttpStatus
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.mvc.Request
import uk.gov.hmrc.http.HttpErrorFunctions.is2xx
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrnotify.config.AppConfig
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeFailure.unknown
import uk.gov.hmrc.ngrnotify.model.bridge.{Compartments, HodMessage, JobMessage}
import uk.gov.hmrc.ngrnotify.model.propertyDetails.{AssessmentId, CredId, PropertyChangesRequest}
import uk.gov.hmrc.ngrnotify.model.ratepayer.{RatepayerPropertyLinksResponse, RegisterRatepayerRequest}

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class BridgeConnector @Inject() (
  appConfig: AppConfig,
  httpClient: HttpClientV2,
  val aboutRatepayers: AboutRatepayers
  // ... inject more conversation utilities here if needed ...
)(using ec: ExecutionContext
) extends HipHeaderCarrier(appConfig):

  /*
   *  DESIGN NOTES
   *  ------------
   *
   *      1. Make this connector expose methods which take the incoming Ngr request as input
   *         and return the outgoing Ngr response as output (conveniently wrapped in the
   *         BridgeResult monadic type). This helps to keep the NGR controller layer
   *         decoupled from the complexity of the Bridge API design.
   *
   *      2. Encapsulate the conversions to/from the JobMessage requests/responses inside
   *         this connector and do not leak them out to the controller layer.
   *
   *      3. Get inspired by the existing registerRatepayer() method below, which shows how to
   *         use the "conversation pattern" to convert between NgrNotifyMessage and JobMessage
   *         requests/responses.
   *
   *               A typical conversation sequence (which, thanks to the new BridgeResult
   *               type, you can implement using Scala for-comprehension) would be:
   *
   *                 - GET the job template
   *
   *                 - process the job template
   *                   (by replacing values in the relevant parts of it)
   *
   *                 - POST the processed job template
   *
   *              The monadic nature of BridgeResult makes it easy to chain these steps
   *              together, and it yields the wrapped NgrNotifyMessage (or it fails fast).
   *
   *      4. Do not get distracted by the implicit use of PlayFramework HTTP Request
   *         as that's needed only for the handling of the CorrelationId header
   */

  // TODO It is still unclear why the CorrelationId header serves any useful purpose for "conversating with the BridgeAPI"

  def registerRatepayer(ngrRequest: RegisterRatepayerRequest)(using request: Request[?]): BridgeResult[NoContent] = {
    val url = appConfig.getRatepayerUrl(ngrRequest.ratepayerCredId)
    for {
      template    <- getJobTemplate(appConfig.getRatepayerUrl(ngrRequest.ratepayerCredId))
      processed   <- aboutRatepayers.process(template, ngrRequest)
      ngrResponse <- postJobTemplate(processed, appConfig.postJobUrl())(using request)
    } yield ngrResponse
  }

  def getRatepayerPropertyLinks(ratepayerCredId: String)(using request: Request[?]): BridgeResult[NoContent] =
    for {
      response <- getJobTemplate(appConfig.getRatepayerUrl(ratepayerCredId))
    } yield {
      val addresses = Compartments.properties(response.job.compartments).map(_.data.addresses.propertyFullAddress.getOrElse(""))
      RatepayerPropertyLinksResponse(addresses.nonEmpty, addresses)
    }

  def submitPropertyChanges(credId: CredId, assessmentId: AssessmentId, propertyChangesRequest: PropertyChangesRequest)(using request: Request[?]): BridgeResult[NoContent] = {
    for {
      template <- getJobTemplate(appConfig.getPropertiesUrl(credId, assessmentId))
      processed <- PropertyChangesRequest.process(template, propertyChangesRequest)
      ngrResponse <- postJobTemplate(processed, appConfig.postJobUrl())(using request)
    } yield {
      ngrResponse
    }
  }


  /**
    * Get the Bridge API job template for the given URL.
    *
    * @param url the URL of the Bridge API job template to retrieve
    * @param request the incoming HTTP request (needed for the CorrelationId header)
    * @return the JobMessage representation of the job template, wrapped in a BridgeResult
    */
  private def getJobTemplate(url: URL)(using request: Request[?]): BridgeResult[JobMessage] =
    httpClient
      .get(url)(using hipHeaderCarrier)
      .execute[HttpResponse]
      .map {
        case r if r.status == HttpStatus.OK =>
          r.json.validate[JobMessage] match {
            case JsSuccess(m, _) =>
              Right(m)
            case JsError(errors) =>
              Left("Bridge replied with status code: 200, but body validation errors occurred: " + errors.mkString(", "))
          }

        case r if r.status == HttpStatus.BAD_REQUEST =>
          // Deserialize the response as HodMessage
          // TODO It's not clear if the Hod (and the HIP) are intermediate systems operating in both development, test or production environments,
          //      or if we should expect to receive the BridgeFailure message directly from the Bridge API depending on the environment.
          val m      = r.json.as[HodMessage]
          // Attempt to extract the first failure reason (if it exists) from the HodMessage
          val reason = m.response.response.failures.headOption.map(_.reason).getOrElse(unknown().reason)
          Left(reason)

        case r =>
          Left("Bridge replied with unexpected status code: " + r.status + ", and body: " + r.body.take(100) + " ...")
      }

  private def postJobTemplate(processed: JobMessage, url: URL)(using request: Request[?]): BridgeResult[NoContent] = {
    val processedJson = Json.toJson[JobMessage](processed)
    httpClient
      .post(url)(using hipHeaderCarrier)
      .withBody(processedJson)
      .execute[HttpResponse]
      .map {
        case r if is2xx(r.status) =>
          Right(())
        case r                    =>
          val bodyAsText = r.body.mkString
          Left(r.status.toString + " " + bodyAsText)
      }
  }
