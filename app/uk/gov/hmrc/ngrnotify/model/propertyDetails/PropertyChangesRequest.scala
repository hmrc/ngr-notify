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

import play.api.libs.json.{Json, OFormat}
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

  def process(bridgeTemplate: JobFormMessage, propertyChanges: PropertyChangesRequest)(implicit ec: ExecutionContext): BridgeResult[JobFormMessage] = {
    val result: Either[String, JobFormMessage] =
      try {
        val productsCompartment = bridgeTemplate.jobForm.job.compartments.products

        if (productsCompartment.isEmpty) {
          Left("job.compartments.products is empty")
        } else {
          val foreignIds: List[ForeignDatum] =
            bridgeTemplate.jobForm.job.data.foreignIds :+ ForeignDatum(Some(NDRRPublicInterface), None, propertyChanges.declarationRef)
          val updatedData: JobData           = bridgeTemplate.jobForm.job.data.copy(foreignIds = foreignIds)
          val jobItemOpt                     = bridgeTemplate.jobForm.job.compartments.products.find(_.category.code == "LTX-DOM-PRP") // CODE :LTX-DOM-PRP
            .map(_.copy(description = NullableValue(Some(propertyChanges.toString))))

          jobItemOpt match {
            case Some(jobItem) =>
              Right(
                JobFormMessage(bridgeTemplate.jobForm.copy(
                  job = bridgeTemplate.jobForm.job.copy(
                    data = updatedData,
                    compartments = bridgeTemplate.jobForm.job.compartments.copy(
                      products = List(jobItem)
                    )
                  )
                ))
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
}
