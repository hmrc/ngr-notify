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
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.NDRRPublicInterface
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

case class PropertyChangesRequest(
  dateOfChange: LocalDate,
  useOfSpace: Option[ChangeToUseOfSpace] = None,
  internalFeatures: Seq[(String, String)],
  externalFeatures: Seq[(String, String)],
  additionalInfo: Option[AnythingElseData] = None,
  uploadedDocuments: Seq[String],
  declarationRef: Option[String] = None
) {
  private val useOfSpaceData: String     = useOfSpace.map(_.toString).getOrElse("No change to use of space")
  private val additionalInfoData: String = additionalInfo.map(_.toString).getOrElse("No additional information provided")

  override def toString: String = s"dateOfChange: $dateOfChange - useOfSpace: $useOfSpaceData - " +
    s"internalFeatures: ${internalFeatures.map { case (k, v) => s"($k, $v)" }.mkString("[", ", ", "]")}, " +
    s"externalFeatures: ${externalFeatures.map { case (k, v) => s"($k, $v)" }.mkString("[", ", ", "]")}, " +
    s"additionalInfo: $additionalInfoData - uploadedDocuments: ${uploadedDocuments.map(x => s"$x").mkString("[", ", ", "]")}"
}

object PropertyChangesRequest {
  implicit val format: OFormat[PropertyChangesRequest] = Json.format

  def process(bridgeTemplate: JobMessage, propertyChanges: PropertyChangesRequest)(implicit ec: ExecutionContext): BridgeResult[JobMessage] = {
    val result: Either[String, JobMessage] =
      try {
        val productsCompartment = bridgeTemplate.job.compartments.products

        if (productsCompartment.isEmpty) {
          Left("job.compartments.products is empty")
        } else {
          val foreignIds: List[ForeignDatum] =
            bridgeTemplate.job.data.foreignIds :+ ForeignDatum(Some(NDRRPublicInterface), None, propertyChanges.declarationRef)
          val updatedData: JobData           = bridgeTemplate.job.data.copy(foreignIds = foreignIds)
          val jobItemOpt                     = bridgeTemplate.job.compartments.products.find(_.category.code == "LTX-DOM-PRP") // CODE :LTX-DOM-PRP
            .map { item =>
              val existingDesc = item.description.value
              val propertyChangesJsonString = Json.stringify(Json.toJson(propertyChanges))
              val merged = mergeDescription(existingDesc, propertyChangesJsonString)
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
    val existingJson: JsObject = existing.flatMap(s => Json.parse(s).asOpt[JsObject]).getOrElse(Json.obj())
    val parsedValue: JsValue = Json.parse(valueJsonString)
    val merged = existingJson ++ Json.obj("physical" -> parsedValue)
    Json.stringify(merged)
  }

}
