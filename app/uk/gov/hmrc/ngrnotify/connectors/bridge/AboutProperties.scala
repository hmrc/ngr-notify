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

import uk.gov.hmrc.ngrnotify.model.bridge.{ForeignDatum, ForeignIdSystem, JobMessage}
import uk.gov.hmrc.ngrnotify.model.propertyDetails.PropertyChangesRequest

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AboutProperties @Inject ()(implicit ec: ExecutionContext):

  def process(bridgeTemplate: JobMessage, ngRequest: PropertyChangesRequest): BridgeResult[JobMessage] = Future.successful {
    // This is where we fill in the template with the ngRequest data
    // You may need to implement additional processing logic here ...

    val filled =
      bridgeTemplate.copy(
        job = bridgeTemplate.job.copy(
          data = bridgeTemplate.job.data.copy(
            foreignIds = List(ForeignDatum(
              system = Some(ForeignIdSystem.NDRR_Public_Interface),
              value = ngRequest.declarationRef))
          ),
          compartments = bridgeTemplate.job.compartments.copy(
            products = List(
              bridgeTemplate.job.compartments.products(0).copy(
                description = Some(ngRequest.toString)
              )
            )
          )
        )
      )

    Right(filled)
  }

