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

package uk.gov.hmrc.ngrnotify.backend.testUtils

import uk.gov.hmrc.ngrnotify.model.response.bridge.*
import uk.gov.hmrc.ngrnotify.model.response.bridge.metadata.*

object BridgeModelTestData {
  val testCategory: Category                           = Category("TestCategoryCode", "TestCategoryMeaning")
  val testTypeX: TypeX                                 = TypeX("TestTypeXCode", "TestTypeXMeaning")
  val testClassX: ClassX                               = ClassX("TestClassXCode", "TestClassXMeaning")
  val testData: Data                                   = Data(List("1", "2", "3"), List("Bob", "Brian", "Bill"), List("Label1", "Label2", "Label3"))
  val testSelecting: Selecting                         = Selecting()
  val testExtracting: Extracting                       = Extracting(testSelecting)
  val testRecontextualising: Recontextualising         = Recontextualising()
  val testSendingTransforming: SendingTransforming     = SendingTransforming(testRecontextualising, Filtering(), Supplementing())
  val testLoading: Loading                             = Loading(Assuring(), Readying(), Signing(), Encrypting(), LoadingSending())
  val testSending: Sending                             = Sending(testExtracting, testSendingTransforming, testLoading)
  val testReceivingTransforming: ReceivingTransforming = ReceivingTransforming(testRecontextualising, Dropping(), Restoring())
  val testInserting: Inserting                         = Inserting()
  val testStoring: Storing                             = Storing(testInserting)
  val testUnloading: Unloading                         = Unloading(Assuring(), Readying(), Verifying(), Decrypting(), UnloadingReceiving())
  val testReceiving: Receiving                         = Receiving(testReceivingTransforming, testStoring, testUnloading)
  val testMetaData: MetaData                           = MetaData(testSending, testReceiving)
  val testItems: List[Item]                            = List(Item(), Item(), Item())
  val testCompartmentsEmpty: Compartments              = Compartments(List.empty, List.empty, List.empty, List.empty, List.empty)

  val testSubCompartment1: JobCompartment     = JobCompartment(
    id = Some("ID1"),
    idx = "IDX1",
    name = "TestJobName1",
    label = "TestJobLabel1",
    description = "TestJobDescription1",
    origination = "TestJobOrigination1",
    termination = Some("TestJobTermination1"),
    category = testCategory,
    typeX = testTypeX,
    classX = testClassX,
    data = testData,
    protodata = List("A1", "B1", "C1"),
    metadata = testMetaData,
    compartments = testCompartmentsEmpty,
    items = testItems
  )
  val testCompartmentsPersonJob: Compartments = Compartments(List.empty, List(testSubCompartment1), List.empty, List.empty, List(testSubCompartment1))

  val testJobCompartment: JobCompartment = JobCompartment(
    id = Some("ID"),
    idx = "IDX",
    name = "TestJobName",
    label = "TestJobLabel",
    description = "TestJobDescription",
    origination = "TestJobOrigination",
    termination = Some("TestJobTermination"),
    category = testCategory,
    typeX = testTypeX,
    classX = testClassX,
    data = testData,
    protodata = List("A", "B", "C"),
    metadata = testMetaData,
    compartments = testCompartmentsPersonJob,
    items = testItems
  )

  val testJobCompartmentEmpty: JobCompartment = JobCompartment(
    id = Some("ID"),
    idx = "IDX",
    name = "TestJobName",
    label = "TestJobLabel",
    description = "TestJobDescription",
    origination = "TestJobOrigination",
    termination = Some("TestJobTermination"),
    category = testCategory,
    typeX = testTypeX,
    classX = testClassX,
    data = testData,
    protodata = List("A", "B", "C"),
    metadata = testMetaData,
    compartments = testCompartmentsEmpty,
    items = testItems
  )
  val testBridgeResponse: BridgeResponse      = BridgeResponse(testJobCompartment)
}
