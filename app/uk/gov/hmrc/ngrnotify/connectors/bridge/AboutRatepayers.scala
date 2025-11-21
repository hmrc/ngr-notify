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

import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.mvc.Request
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrnotify.config.AppConfig
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeFailure.unknown
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.Government_Gateway
import uk.gov.hmrc.ngrnotify.model.ratepayer.{RegisterRatepayerRequest, RegisterRatepayerResponse}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

/*
 * This class provides convenient methods for composing HTTP conversations between our NGR Notify service
 * and the Bridge API service. A conversation is a sequence of HTTP request/response pairs that are sent
 * back and forth to carry out a specific task.
 *
 * Typical conversations go through 3 steps:
 *
 *    1. >> NGR Notify sends a GET request to the Bridge API to retrieve a JSON template for a Bridge job
 *    2. << Bridge API returns a JSON template for a Bridge job.
 *    3. >> NGR Notify modifies such a template by replacing values into the relevant points of the JSON structure.
 *    4. >> NGR Notify sends a POST request to the Bridge API to submit the modified JSON template as a Bridge job.
 *    5. << Bridge API returns a response indicating whether the job was successfully submitted.
 *
 */
class AboutRatepayers @Inject(
    appConfig: AppConfig,
    httpClient: HttpClientV2
  )(implicit ec: ExecutionContext) extends CorrelationHandling(appConfig) {


  def getJobTemplate(id: String)(using request: Request[?]): BridgeResult[BridgeMessage] = {
    httpClient
      .get(appConfig.getRatepayerUrl(id))(using hipHeaderCarrier)
      .execute[HttpResponse]
      .map {
        case r if r.status == 200 =>
          Right(r.json.as[BridgeMessage])

        case r if r.status == 400 =>
          // Deserialize the response as HodMessage
          // TODO It's not clear if the Hod (and the HIP) are intermediate systems operating in both development, test or production environments,
          //      or if we should expect to receive the BridgeFailure message directly from the Bridge API depending on the environment.
          val m = r.json.as[HodMessage]
          // Attempt to extract the first failure reason (if it exists) from the HodMessage
          val reason = m.response.response.failures.headOption.map(_.reason).getOrElse(unknown().reason)
          Left(reason)

        case r =>
          Left(r.status.toString + " " + r.body.take(100))
      }
  }


  def processJobTemplate(template: BridgeMessage, ngr: RegisterRatepayerRequest): BridgeMessage = {
    BridgeMessage(
      job = template.job.copy(
        compartments = template.job.compartments.copy(
          // TODO How should we modify the template in order to inject the data conveyed by the NGR RegisterRatepayerRequest
          persons = template.job.compartments.persons
        )
      )
    )
  }


  def postJob(modified: BridgeMessage)(using request: Request[?]): BridgeResult[RegisterRatepayerResponse] = {
    httpClient
      .post(appConfig.registerRatepayerUrl)(using hipHeaderCarrier)
      .withBody(Json.toJson(modified))
      .execute[HttpResponse]
      .map {
        case r if r.status == 200 =>
          val ngrResponse = r.json.as[RegisterRatepayerResponse]
          Right(ngrResponse)

        case _ =>
          // TODO It's not clear which kind of HTTP response we should expect to receive from Hod / HIP / Bridge in negative scenarios.
          Left(unknown().reason)
      }
  }


  private def toBridgeMessage(ratepayer: RegisterRatepayerRequest): BridgeMessage =
    BridgeMessage(
      Job(
        id = None,
        idx = "1",
        name = "Register Ratepayer",
        compartments = Compartments(
          products = List(
            Person(
              id = None,
              idx = "1.4.1",
              name = "Government Gateway User",
              data = PersonData(
                foreignIds = List(
                  ForeignId(
                    system = Some(Government_Gateway),
                    value = Some(ratepayer.ratepayerCredId)
                  ),
                  ForeignId(
                    location = Some("NINO"),
                    value = Some(ratepayer.nino.map(_.value).getOrElse(""))
                  ),
                  ForeignId(
                    location = Some("secondary_telephone_number"),
                    value = Some(ratepayer.secondaryNumber.map(_.value).getOrElse(""))
                  )
                ),
                foreignLabels = List(
                  ForeignId(
                    location = Some("RatepayerType"),
                    value = ratepayer.userType.map(_.toString)
                  ),
                  ForeignId(
                    location = Some("AgentStatus"),
                    value = ratepayer.agentStatus.map(_.toString)
                  ),
                  ForeignId(
                    location = Some("RecoveryId"),
                    value = ratepayer.recoveryId
                  )
                ),
                names = extractNames(ratepayer),
                communications = extractCommunications(ratepayer)
              ),
              // TODO label = "label",
              // TODO description = ""
            )
          )
        )
      )
    )


  private def extractNames(ratepayer: RegisterRatepayerRequest): Option[Names] =
    val (forenamesOpt, surnameOpt) = extractForenamesAndSurname(ratepayer.name.map(_.value).getOrElse(""))

    Some(
      Names(
        forenames = forenamesOpt,
        surname = surnameOpt,
        corporateName = Some(ratepayer.tradingName.map(_.value).getOrElse(""))
      )
    )


  private def extractForenamesAndSurname(fullName: String): (Option[String], Option[String]) =
    val trimmedFullName = fullName.trim
    val index = trimmedFullName.lastIndexOf(" ")
    val (forenames, surname) =
      if index == -1 then ("", trimmedFullName) else trimmedFullName.splitAt(index)
    (Option.when(forenames.trim.nonEmpty)(forenames.trim), Some(surname.trim))


  private def extractCommunications(ratepayer: RegisterRatepayerRequest): Option[Communications] =
    Some(
      Communications(
        postalAddress = Some(ratepayer.address.map(_.singleLine).getOrElse("")),
        telephoneNumber = Some(ratepayer.contactNumber.map(_.value).getOrElse("")),
        email = Some(ratepayer.email.map(_.toString).getOrElse(""))
      )
    )

}