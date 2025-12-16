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
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.{Government_Gateway, NDRRPublicInterface}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PropertyChangesRequestSpec extends AnyFreeSpec with JobMessageTestData with ScalaFutures {

  "PropertyChangesRequest" - {
    "toString should redact uploadedDocuments" in {
      val request        = PropertyChangesRequest(
        dateOfChange = java.time.LocalDate.of(2023, 1, 1),
        useOfSpace = Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
        internalFeatures = Seq(("airConditioning", "none"), ("securityCamera", "23")),
        externalFeatures = Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
        additionalInfo = Some(AnythingElseData(true, Some("additional text"))),
        uploadedDocuments = Seq("uploadId1", "uploadId2")
      )
      val toStringOutput = request.toString
      toStringOutput mustBe "dateOfChange: 2023-01-01 - useOfSpace: selectUseOfSpace: [rearrangedTheUseOfSpace] - hasPlanningPermission: true - permissionReference: REFzR42536T - internalFeatures: [(airConditioning, none), (securityCamera, 23)], externalFeatures: [(loadingBays, added), (lockupGarages, removedSome)], additionalInfo: value: true - text: additional text - uploadedDocuments: [uploadId1, uploadId2]"
    }

    "toString should handle None values correctly" in {
      val request        = PropertyChangesRequest(
        dateOfChange = java.time.LocalDate.of(2023, 1, 1),
        useOfSpace = None,
        internalFeatures = Seq.empty,
        externalFeatures = Seq.empty,
        additionalInfo = None,
        uploadedDocuments = Seq.empty
      )
      val toStringOutput = request.toString
      toStringOutput mustBe "dateOfChange: 2023-01-01 - useOfSpace: No change to use of space - internalFeatures: [], externalFeatures: [], additionalInfo: No additional information provided - uploadedDocuments: []"
    }

    "process should update JobMessage with correct data for the category code 'LTX-DOM-PRP'" in {

      val propertyChanges = PropertyChangesRequest(
        dateOfChange = java.time.LocalDate.of(2023, 1, 1),
        useOfSpace = None,
        internalFeatures = Seq.empty,
        externalFeatures = Seq.empty,
        additionalInfo = None,
        uploadedDocuments = Seq.empty,
        declarationRef = Some("declRef123")
      )

      val updatedJobModel: JobMessage = PropertyChangesRequest.process(sampleJobMessage(), propertyChanges).toFuture.futureValue

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
          value = Some("declRef123")
        )
      )
    }

    "process should throw an exception when the products category code not 'LTX-DOM-PRP'" in {

      val propertyChanges = PropertyChangesRequest(
        dateOfChange = java.time.LocalDate.of(2023, 1, 1),
        useOfSpace = None,
        internalFeatures = Seq.empty,
        externalFeatures = Seq.empty,
        additionalInfo = None,
        uploadedDocuments = Seq.empty,
        declarationRef = Some("declRef123")
      )

      val updatedJobModel = PropertyChangesRequest.process(sampleJobMessage("random"), propertyChanges).toFuture
      updatedJobModel.failed.futureValue.getMessage mustBe "No job item found to update description"

    }

    "process should throw an exception if job data has no Compartments" in {
      val invalidBridgeModel = sampleJobMessage().copy(
        job = sampleJobMessage().job.copy(
          compartments = Compartments()
        )
      )

      val propertyChanges = PropertyChangesRequest(
        dateOfChange = java.time.LocalDate.of(2023, 1, 1),
        useOfSpace = None,
        internalFeatures = Seq.empty,
        externalFeatures = Seq.empty,
        additionalInfo = None,
        uploadedDocuments = Seq.empty,
        declarationRef = Some("declRef123")
      )

      val updatedJobModelOpt: Future[JobMessage] = PropertyChangesRequest.process(invalidBridgeModel, propertyChanges).toFuture
      updatedJobModelOpt.failed.futureValue.getMessage mustBe "job.compartments.products is empty"
    }
  }
}
