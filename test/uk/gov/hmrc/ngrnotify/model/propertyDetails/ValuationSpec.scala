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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

import java.time.LocalDate

class ValuationSpec extends AnyFreeSpec with Matchers {

    "Valuation" - {

      "Valuation JSON format" - {

        "serialise and deserialise Valuation correctly" in {
          val valuation = Valuation(
            assessmentRef = 123456L,
            assessmentStatus = "active",
            rateableValue = Some(BigDecimal(1000)),
            scatCode = Some("SCAT01"),
            descriptionText = "Test property",
            effectiveDate = LocalDate.of(2024, 1, 1),
            currentFromDate = LocalDate.of(2024, 2, 1),
            listYear = "2023",
            primaryDescription = "Office",
            allowedActions = List("view", "edit"),
            listType = "typeA",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2023, 12, 1))
          )

          val json = Json.toJson(valuation)
          val fromJson = json.as[Valuation]

          fromJson mustBe valuation
        }
      }

      "handle optional fields correctly" in {
        val valuation = Valuation(
          assessmentRef = 654321L,
          assessmentStatus = "inactive",
          rateableValue = None,
          scatCode = None,
          descriptionText = "Another test property",
          effectiveDate = LocalDate.of(2023, 5, 15),
          currentFromDate = LocalDate.of(2023, 6, 15),
          listYear = "2022",
          primaryDescription = "Retail",
          allowedActions = List.empty,
          listType = "typeB",
          propertyLinkEarliestStartDate = None
        )

        val json = Json.toJson(valuation)
        val fromJson = json.as[Valuation]

        fromJson mustBe valuation
      }
    }
}
