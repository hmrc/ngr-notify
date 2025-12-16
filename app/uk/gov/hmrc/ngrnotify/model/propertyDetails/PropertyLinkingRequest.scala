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

package uk.gov.hmrc.ngrnotify.model.propertyDetails

import play.api.libs.json.{JsObject, JsValue, Json, OFormat}
import uk.gov.hmrc.ngrnotify.connectors.bridge.{BridgeResult, FutureEither}
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.NDRRPublicInterface
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue
import uk.gov.hmrc.ngrnotify.model.bridge.*

import scala.concurrent.{ExecutionContext, Future}

case class PropertyLinkingRequest(
  vmvProperty: VMVProperty,
  currentRatepayer: Option[CurrentRatepayer] = None,
  businessRatesBill: Option[String] = None,
  connectionToProperty: Option[String] = None,
  requestSentReference: Option[String] = None,
  evidenceDocument: Option[String] = None,
  evidenceDocumentUrl: Option[String] = None,
  evidenceDocumentUploadId: Option[String] = None,
  uploadEvidence: Option[String] = None
) {
  override def toString: String =
    s"vmvProperty: $vmvProperty, " +
      s"currentRatepayer: ${currentRatepayer.getOrElse("current rate payer data not provided")}, businessRatesBill: ${businessRatesBill.getOrElse("businessRatesBill is not provided")}, " +
      s"connectionToProperty: ${connectionToProperty.getOrElse("connectionToProperty data is not provided")}, requestSentReference: ${requestSentReference.getOrElse("requestSentReference is not provided")}, " +
      s"evidenceDocument: ${evidenceDocument.getOrElse("evidenceDocument are not provided")}, evidenceDocumentUrl: ${evidenceDocumentUrl.getOrElse("evidenceDocumentUrl is empty")}, " +
      s"evidenceDocumentUploadId: ${evidenceDocumentUploadId.getOrElse("evidenceDocumentUploadId is empty")}, uploadEvidence: ${uploadEvidence.getOrElse("uploadEvidence is not provided")}"
}

object PropertyLinkingRequest {
  implicit val format: OFormat[PropertyLinkingRequest] = Json.format

  def process(bridgeTemplate: JobMessage, propertyLinkingRequest: PropertyLinkingRequest)(implicit ec: ExecutionContext): BridgeResult[JobMessage] = {
    val result: Either[String, JobMessage] =
      try {
        val productsCompartment = bridgeTemplate.job.compartments.products

        if (productsCompartment.isEmpty) {
          Left("job.compartments.products is empty")
        } else {
          val foreignIds: List[ForeignDatum] =
            bridgeTemplate.job.data.foreignIds :+ ForeignDatum(Some(NDRRPublicInterface), None, propertyLinkingRequest.requestSentReference)
          val updatedData: JobData           = bridgeTemplate.job.data.copy(foreignIds = foreignIds)
          val jobItemOpt                     = bridgeTemplate.job.compartments.products.find(_.category.code == "LTX-DOM-PRP") // CODE :LTX-DOM-PRP
            .map { item =>
              val existingDesc              = item.description.value
              val propertyChangesJsonString = Json.stringify(Json.toJson(propertyLinkingRequest))
              val merged                    = mergeDescription(existingDesc, propertyChangesJsonString)
              item.copy(description = NullableValue(Some(merged)))
            }

          jobItemOpt match {
            case Some(jobItem) =>
              Right(
                bridgeTemplate.copy(
                  job = bridgeTemplate.job.copy(
                    data = updatedData,
                    compartments = bridgeTemplate.job.compartments.copy(
                      products = List(jobItem)
                    )
                  )
                )
              )
            case None          =>
              Left("No job item found to update description")
          }
        }
      } catch {
        case e: Throwable => Left(e.getMessage)
      }
    Future.successful(result)
  }

  private def mergeDescription(existing: Option[String], valueJsonString: String): String = {
    val existingJson: JsObject = existing.flatMap { s =>
      try
        Json.parse(s).asOpt[JsObject]
      catch {
        case _: Throwable => None
      }
    }.getOrElse(Json.obj())

    val parsedValue: JsValue =
      try
        Json.parse(valueJsonString)
      catch {
        case _: Throwable => Json.obj()
      }

    val merged: JsObject = if (existingJson.fields.isEmpty) Json.obj("PropertyLinking" -> parsedValue)
    else existingJson ++ Json.obj("PropertyLinking" -> parsedValue)

    Json.stringify(merged)
  }

}
