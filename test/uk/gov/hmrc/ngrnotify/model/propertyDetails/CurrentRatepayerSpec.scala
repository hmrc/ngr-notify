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

class CurrentRatepayerSpec extends AnyFreeSpec {
  "CurrentRatepayer toString" - {
    "should return correct string representation when becomeRatepayerDate is defined" in {
      val currentRatepayer = CurrentRatepayer(isBeforeApril = true, Some("2024-05-01"))
      val expectedString = "isBeforeApril: true - becomeRatepayerDate: 2024-05-01"
      currentRatepayer.toString mustBe expectedString
    }

    "should return correct string representation when becomeRatepayerDate is not defined" in {
      val currentRatepayer = CurrentRatepayer(isBeforeApril = false, None)
      val expectedString = "isBeforeApril: false - becomeRatepayerDate: No rate payer date is not provided"
      currentRatepayer.toString mustBe expectedString
    }
  }
}
