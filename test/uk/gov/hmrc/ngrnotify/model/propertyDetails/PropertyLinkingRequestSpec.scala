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

class PropertyLinkingRequestSpec extends AnyFreeSpec {
  "PropertyLinkingRequest" - {
    "serialization and deserialization" - {
      "should work correctly" in {
        import play.api.libs.json.Json

        val credId                 = CredId("test-cred-id")
        val vmvProperty            = VMVProperty(100L, "property-id", "address", "LA123", List())
        val currentRatepayer       = Some(CurrentRatepayer(true, Some("John Doe")))
        val propertyLinkingRequest = PropertyLinkingRequest(
          credId = credId,
          vmvProperty = vmvProperty,
          currentRatepayer = currentRatepayer,
          businessRatesBill = Some("bill.pdf"),
          connectionToProperty = Some("Owner"),
          requestSentReference = Some("ref-123"),
          evidenceDocument = Some("evidence.pdf"),
          evidenceDocumentUrl = Some("http://example.com/evidence.pdf"),
          evidenceDocumentUploadId = Some("upload-123"),
          uploadEvidence = Some("yes")
        )

        val json     = Json.toJson(propertyLinkingRequest)
        val fromJson = json.as[PropertyLinkingRequest]

        fromJson mustBe propertyLinkingRequest
      }
    }
  }
}
