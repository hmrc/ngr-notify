/*
 * Copyright 2026 HM Revenue & Customs
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
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.NDRRPublicInterface
import uk.gov.hmrc.ngrnotify.model.bridge.{ForeignDatum, JobData, JobMessage}

import scala.concurrent.{ExecutionContext, Future}

case class ReviewChangesUserAnswers(declarationRef: String)

object ReviewChangesUserAnswers:
  implicit val format: OFormat[ReviewChangesUserAnswers] = Json.format[ReviewChangesUserAnswers]

  def process(bridgeTemplate: JobMessage, reviewChanges: ReviewChangesUserAnswers)(implicit ec: ExecutionContext)
    : BridgeResult[JobMessage] =
    if (bridgeTemplate.job.compartments.products.isEmpty)
      Future.successful(Left("job.compartments.products is empty"))
    else
      Future.successful(Right(
        bridgeTemplate.copy(
          job = bridgeTemplate.job.copy(
            data = bridgeTemplate.job.data.copy(
              foreignIds = bridgeTemplate.job.data.foreignIds :+ ForeignDatum(
                Some(NDRRPublicInterface),
                None,
                Some(reviewChanges.declarationRef)
              )
            )
          )
        )
      ))
