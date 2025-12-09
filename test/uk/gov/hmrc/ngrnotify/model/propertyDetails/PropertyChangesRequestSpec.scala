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
import play.api.libs.json.JsNull
import uk.gov.hmrc.ngrnotify.model.bridge
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.{Government_Gateway, NDRRPublicInterface}
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PropertyChangesRequestSpec extends AnyFreeSpec with Data with ScalaFutures {

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

      val updatedJobModel: JobFormMessage = PropertyChangesRequest.process(sampleJobMessage(), propertyChanges).toFuture.futureValue

      val foreignIds = updatedJobModel.jobForm.job.data.foreignIds
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
    val invalidBridgeModel: JobFormMessage = sampleJobMessage().copy(
      jobForm = sampleJobMessage().jobForm.copy(
        job = sampleJobMessage().jobForm.job.copy(
          compartments = Compartments()
        )
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

      val updatedJobModelOpt: Future[JobFormMessage] = PropertyChangesRequest.process(invalidBridgeModel, propertyChanges).toFuture
      updatedJobModelOpt.failed.futureValue.getMessage mustBe "job.compartments.products is empty"

    }
  }
}

trait Data {

  val metadata: bridge.Metadata = bridge.Metadata(
    Sending(Extracting(JsNull), Transforming(JsNull, JsNull, JsNull), Loading(JsNull, JsNull, JsNull, JsNull, JsNull)),
    Receiving(Unloading(JsNull, JsNull, JsNull, JsNull, JsNull), TransformingReceiving(JsNull, JsNull, JsNull), Storing(JsNull))
  )

  val propertyData                                              = PropertyData(List(ForeignDatum(Some(Government_Gateway), Some("location"), Some("SomeId"))), List.empty, List.empty, PropertyAddresses())

  def sampleProductEntity(categoryCode: String = "LTX-DOM-PRP") = ProductEntity(
    id = NullableValue(Some(StringId("123"))),
    idx = "P001",
    name = NullableValue(Some("Sample Product")),
    label = "Sample Label",
    description = NullableValue(Some("A sample product for testing.")),
    origination = NullableValue(Some("Origin")),
    termination = NullableValue(Some("Termination")),
    protodata = List.empty,
    metadata = metadata,
    category = CodeMeaning(categoryCode, NullableValue(Some("Category 1"))),
    `type` = CodeMeaning("TYPE001", NullableValue(Some("Type 1"))),
    `class` = CodeMeaning("CLASS001", NullableValue(Some("Class 1"))),
    data = propertyData,
    compartments = Compartments(),
    items = List.empty
  )

  def sampleJobEntity(categoryCode: String = "LTX-DOM-PRP") = JobEntity(
    id = NullableValue(Some(StringId("job-123"))),
    idx = "IDX-001",
    name = NullableValue(Some("Sample Job")),
    label = "Sample Label",
    description = NullableValue(Some("This is a sample job entity.")),
    origination = NullableValue(Some("2025-01-01T00:00:00Z")),
    termination = NullableValue(Some("2025-12-31T23:59:59Z")),
    protodata = List(Protodata(Some("proto-1"), "Pdf", "string", Some(true), "string", "")),
    metadata = metadata,
    category = CodeMeaning(categoryCode, NullableValue(Some("Category 1"))),
    `type` = CodeMeaning("TYPE001", NullableValue(Some("Type 1"))),
    `class` = CodeMeaning("CLASS001", NullableValue(Some("Class 1"))),
    data = JobData(List(ForeignDatum(Some(Government_Gateway), Some("location"), Some("SomeId"))), List.empty, List.empty),
    compartments = Compartments(products = List(sampleProductEntity(categoryCode))),
    items = List.empty
  )

  def sampleJobMessage(categoryCode: String = "LTX-DOM-PRP"): JobFormMessage = JobFormMessage(JobForm(
    "",
    sampleJobEntity(categoryCode)
  ))
}
