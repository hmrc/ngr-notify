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

package uk.gov.hmrc.ngrnotify.backend.connectors.bridge

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsNull, JsObject, Json}
import uk.gov.hmrc.ngrnotify.connectors.bridge.AboutRald
import uk.gov.hmrc.ngrnotify.model.bridge
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.{Government_Gateway, NDRRPublicInterface}
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue

import scala.concurrent.ExecutionContext.Implicits.global

class AboutRaldSpec extends AnyFreeSpec with Data with ScalaFutures with Matchers {

  "AboutRald" - {

    "process should update JobMessage with correct data for the category code 'LTX-DOM-PRP'" in {
      val raldChanges: JsObject = Json.obj("newField" -> "newValue")
      val assessmentId = "assessment-123"

      val updatedJob: JobMessage = new AboutRald()
        .process(sampleJobMessage(), raldChanges, assessmentId)
        .toFuture
        .futureValue

      val foreignIds = updatedJob.job.data.foreignIds
      foreignIds must contain(ForeignDatum(Some(NDRRPublicInterface), None, Some(assessmentId)))

      val updatedDesc = updatedJob.job.compartments.products.head.description.value.get
      updatedDesc must include("\"rald\":{\"newField\":\"newValue\"}")
    }

    "process should throw an exception when the products category code is not 'LTX-DOM-PRP'" in {
      val raldChanges: JsObject = Json.obj("newField" -> "newValue")
      val assessmentId = "assessment-123"

      val future = new AboutRald()
        .process(sampleJobMessage("OTHER-CODE"), raldChanges, assessmentId)
        .toFuture

      val ex = future.failed.futureValue
      ex.getMessage mustBe "No job item found to update description"
    }

    "process should throw an exception if job data has empty products compartments" in {
      val raldChanges: JsObject = Json.obj("newField" -> "newValue")
      val assessmentId = "assessment-123"

      val invalidJob = sampleJobMessage().copy(
        job = sampleJobEntity().copy(compartments = Compartments())
      )

      val future = new AboutRald()
        .process(invalidJob, raldChanges, assessmentId)
        .toFuture

      val ex = future.failed.futureValue
      ex.getMessage mustBe "job.compartments.products is empty"
    }

    "process should merge with existing JSON description" in {
      val productWithJson = sampleProductEntity().copy(
        description = NullableValue(Some("""{"existing":"desc"}"""))
      )
      val jobWithProduct = sampleJobMessage().copy(
        job = sampleJobEntity().copy(compartments = Compartments(products = List(productWithJson)))
      )

      val raldChanges: JsObject = Json.obj("newField" -> "newValue")
      val assessmentId = "assessment-123"

      val updatedJob: JobMessage = new AboutRald()
        .process(jobWithProduct, raldChanges, assessmentId)
        .toFuture
        .futureValue

      val updatedDesc = updatedJob.job.compartments.products.head.description.value.get
      updatedDesc must include("\"existing\":\"desc\"")
      updatedDesc must include("\"rald\":{\"newField\":\"newValue\"}")
    }

    "process should handle malformed existing JSON safely" in {
      val productWithMalformedJson = sampleProductEntity().copy(
        description = NullableValue(Some("""{malformed json}"""))
      )
      val jobWithProduct = sampleJobMessage().copy(
        job = sampleJobEntity().copy(compartments = Compartments(products = List(productWithMalformedJson)))
      )

      val raldChanges: JsObject = Json.obj("newField" -> "newValue")
      val assessmentId = "assessment-123"

      val updatedJob: JobMessage = new AboutRald()
        .process(jobWithProduct, raldChanges, assessmentId)
        .toFuture
        .futureValue

      val updatedDesc = updatedJob.job.compartments.products.head.description.value.get
      updatedDesc must include("\"rald\":{\"newField\":\"newValue\"}")
    }
  }
}

trait Data {

  val metadata: bridge.Metadata = bridge.Metadata(
    Sending(Extracting(JsNull), Transforming(JsNull, JsNull, JsNull), Loading(JsNull, JsNull, JsNull, JsNull, JsNull)),
    Receiving(Unloading(JsNull, JsNull, JsNull, JsNull, JsNull), TransformingReceiving(JsNull, JsNull, JsNull), Storing(JsNull))
  )

  val propertyData = PropertyData(
    foreignIds = List.empty,
    foreignNames = List.empty,
    foreignLabels = List.empty,
    addresses = PropertyAddresses()
  )

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

  def sampleJobMessage(categoryCode: String = "LTX-DOM-PRP"): JobMessage =
    JobMessage("", sampleJobEntity(categoryCode))
}
