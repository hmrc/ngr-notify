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

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.NDRRPublicInterface
import uk.gov.hmrc.ngrnotify.model.bridge.{ForeignDatum, JobData, JobMessage}
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue

import scala.concurrent.{ExecutionContext, Future}

class AboutRald {
  def process(bridgeTemplate: JobMessage, raldChanges: JsObject, assessmentId: String)(implicit ec: ExecutionContext): BridgeResult[JobMessage] = {
    val result: Either[String, JobMessage] =
      try {
        val productsCompartment = bridgeTemplate.job.compartments.products

        if (productsCompartment.isEmpty) {
          Left("job.compartments.products is empty")
        } else {
          val foreignIds: List[ForeignDatum] =
            bridgeTemplate.job.data.foreignIds :+ ForeignDatum(Some(NDRRPublicInterface), None, Some(assessmentId))
          val updatedData: JobData = bridgeTemplate.job.data.copy(foreignIds = foreignIds)
          val jobItemOpt = bridgeTemplate.job.compartments.products.find(_.category.code == "LTX-DOM-PRP") // CODE :LTX-DOM-PRP
            .map { item =>
              val existingDesc = item.description.value
              val newDescJson = mergeDescription(existingDesc, raldChanges)
              item.copy(description = NullableValue(Some(newDescJson)))
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
            case None =>
              Left("No job item found to update description")
          }
        }
      } catch {
        case e: Throwable => Left(e.getMessage)
      }
    Future.successful(result)
  }

  private def mergeDescription(existing: Option[String], raldChanges: JsObject): String = {
    val baseJson: JsObject = existing.flatMap { s =>
      try {
        Json.parse(s).asOpt[JsObject]
      } catch {
        case _: Throwable => None
      }
    }.getOrElse(Json.obj())

    val merged = if (baseJson.fields.isEmpty) Json.obj("rald" -> raldChanges)
    else baseJson ++ Json.obj("rald" -> raldChanges)

    Json.stringify(merged)
  }

}