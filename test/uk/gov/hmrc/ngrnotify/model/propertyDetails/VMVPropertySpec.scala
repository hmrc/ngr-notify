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
import org.scalatest.matchers.must.Matchers.mustBe

class VMVPropertySpec extends AnyFreeSpec with JobMessageTestData {
  "VMVPropertySpec" - {
    "VMVProperty toString should redact sensitive information" in {
      val toStringOutput = vmvProperty.toString
      toStringOutput mustBe "uarn: 100, addressFull: property-id, localAuthorityCode: address, localAuthorityReference: LA123, valuations: [assessmentRef: 12345, assessmentStatus: CURRENT, rateableValue: None, scatCode: Some(Details about valuation), descriptionText: description, effectiveDate: 2026-01-01, currentFromDate: 2026-01-01, listYear: 2023, primaryDescription: primary, allowedActions: [], listType: type, propertyLinkEarliestStartDate: None]"
    }
  }

}
