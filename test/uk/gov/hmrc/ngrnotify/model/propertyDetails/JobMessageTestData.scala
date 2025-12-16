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

import play.api.libs.json.{JsNull, JsValue, Json}
import uk.gov.hmrc.ngrnotify.model.bridge
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.Government_Gateway
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue

trait JobMessageTestData {

  val credId = CredId("test-cred-id")
  val valuations = List(
    Valuation(
      assessmentRef = 12345L,
      assessmentStatus = "CURRENT",
      rateableValue = None,
      scatCode = Some("Details about valuation"),
      descriptionText = "description",
      effectiveDate = java.time.LocalDate.of(2026, 1, 1),
      currentFromDate = java.time.LocalDate.of(2026, 1, 1),
      listYear = "2023",
      primaryDescription = "primary",
      allowedActions = List.empty,
      listType = "type",
      propertyLinkEarliestStartDate = None
    )
  )
  
  val vmvProperty = VMVProperty(100L, "property-id", "address", "LA123", valuations)
  val VMVPropertyWithNoValuations = VMVProperty(101L, "property-id-2", "address-2", "LA124", List.empty)
  val currentRatepayer = Some(CurrentRatepayer(true, Some("John Doe")))
   val propertyLinkingRequest              = PropertyLinkingRequest(
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
  val propertyLinkingRequestJson: JsValue = Json.toJson(propertyLinkingRequest)
  val propertyLinkingRequestNoValuationJson: JsValue = Json.toJson(propertyLinkingRequest.copy(vmvProperty = VMVPropertyWithNoValuations))

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

  def sampleJobMessage(categoryCode: String = "LTX-DOM-PRP"): JobMessage = JobMessage(
    "",
    sampleJobEntity(categoryCode)
  )
}
