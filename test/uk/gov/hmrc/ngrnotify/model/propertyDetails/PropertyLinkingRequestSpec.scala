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

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe
import play.api.libs.json.Json
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.{Government_Gateway, NDRRPublicInterface}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PropertyLinkingRequestSpec extends AnyFreeSpec with JobMessageTestData with ScalaFutures {
  "PropertyLinkingRequest" - {
    "serialization and deserialization" - {
      "should work correctly" in {
        val json     = Json.toJson(propertyLinkingRequest)
        val fromJson = json.as[PropertyLinkingRequest]

        fromJson mustBe propertyLinkingRequest
      }
    }

    "propertyLinkingRequest toString" - {
      "should return correct string representation" in {
        val expectedString =
          "vmvProperty: uarn: 100, addressFull: property-id, localAuthorityCode: address, localAuthorityReference: LA123, valuations: [], currentRatepayer: isBeforeApril: true - becomeRatepayerDate: John Doe," +
            " businessRatesBill: bill.pdf, connectionToProperty: Owner, requestSentReference: ref-123, evidenceDocument: evidence.pdf, evidenceDocumentUrl: http://example.com/evidence.pdf, evidenceDocumentUploadId: upload-123, uploadEvidence: yes"

        propertyLinkingRequest.copy(vmvProperty = vmvProperty.copy(valuations = List.empty)).toString mustBe expectedString
      }
    }

    "process should update JobMessage with correct data for the category code 'LTX-DOM-PRP'" in {

      val propertyChanges = PropertyLinkingRequest(
        vmvProperty = vmvProperty,
        currentRatepayer = currentRatepayer,
        businessRatesBill = None,
        connectionToProperty = None,
        requestSentReference = Some("SentReference1234"),
        evidenceDocument = None,
        evidenceDocumentUrl = None,
        evidenceDocumentUploadId = None,
        uploadEvidence = None)

      val updatedJobModel: JobMessage = PropertyLinkingRequest.process(sampleJobMessage(), propertyChanges).toFuture.futureValue

      val foreignIds = updatedJobModel.job.data.foreignIds
      foreignIds mustBe List(
        ForeignDatum(
          system = Some(Government_Gateway),
          location = Some("location"),
          value = Some("SomeId")
        ),
        ForeignDatum(
          system = Some(NDRRPublicInterface),
          location = None,
          value = Some("SentReference1234")
        )
      )
    }

    "process should throw an exception when the products category code not 'LTX-DOM-PRP'" in {

      val propertyChanges = PropertyLinkingRequest(
        vmvProperty = vmvProperty,
        currentRatepayer = currentRatepayer,
        businessRatesBill = None,
        connectionToProperty = None,
        requestSentReference = Some("SentReference1234"),
        evidenceDocument = None,
        evidenceDocumentUrl = None,
        evidenceDocumentUploadId = None,
        uploadEvidence = None)

      val updatedJobModel = PropertyLinkingRequest.process(sampleJobMessage("random"), propertyChanges).toFuture
      updatedJobModel.failed.futureValue.getMessage mustBe "No job item found to update description"

    }

    "process should throw an exception if job data has no Compartments" in {
      val invalidBridgeModel = sampleJobMessage().copy(
        job = sampleJobMessage().job.copy(
          compartments = Compartments()
        )
      )

      val propertyChanges = PropertyLinkingRequest(
        vmvProperty = vmvProperty,
        currentRatepayer = currentRatepayer,
        businessRatesBill = None,
        connectionToProperty = None,
        requestSentReference = Some("declRef123"),
        evidenceDocument = None,
        evidenceDocumentUrl = None,
        evidenceDocumentUploadId = None,
        uploadEvidence = None)

      val updatedJobModelOpt: Future[JobMessage] = PropertyLinkingRequest.process(invalidBridgeModel, propertyChanges).toFuture
      updatedJobModelOpt.failed.futureValue.getMessage mustBe "job.compartments.products is empty"

    }

    "process should update only description field in the product entity" in {

      val propertyChanges = PropertyLinkingRequest(
        vmvProperty = vmvProperty,
        currentRatepayer = currentRatepayer,
        businessRatesBill = None,
        connectionToProperty = None,
        requestSentReference = Some("SentReference1234"),
        evidenceDocument = None,
        evidenceDocumentUrl = None,
        evidenceDocumentUploadId = None,
        uploadEvidence = None)

      val updatedJobModel: JobMessage = PropertyLinkingRequest.process(sampleJobMessage(), propertyChanges).toFuture.futureValue

      val updatedProduct = updatedJobModel.job.compartments.products.head
      val expectedDescription = Json.stringify(Json.obj("PropertyLinking" -> Json.toJson(propertyChanges)))

      updatedProduct.description.value mustBe Some(expectedDescription)
    }
  }
}
