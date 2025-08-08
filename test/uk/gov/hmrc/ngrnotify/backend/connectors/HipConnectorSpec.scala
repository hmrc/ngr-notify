//package uk.gov.hmrc.ngrnotify.backend.connectors
//
///*
// * Copyright 2025 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.matchers.should.Matchers.shouldBe
//import play.api.libs.json.{JsString, JsValue}
//import uk.gov.hmrc.ngrnotify.backend.testUtils.HipTestData.testHipHeaders
//import uk.gov.hmrc.ngrnotify.backend.testUtils.MockHttpV2
//import uk.gov.hmrc.ngrnotify.connectors.HipConnector
//
//
//class HipConnectorSpec extends MockHttpV2 {
//  val connector = new HipConnector(mockHttpClientV2)
//
////  "callHelloWorld()" should {
////    "return a successful JsValue response" in {
////      val expectedResponse: JsValue = JsString("Hello World")
////
////      setupMockHttpV2Get("https://hip.ws.ibt.hmrc.gov.uk/demo/hello-world")(expectedResponse)
////
////      val result = connector.callHelloWorld(testHipHeaders)
////      result.futureValue shouldBe expectedResponse
////    }
////  }
////
////  "callPersonDetails()" should {
////    "return a successful JsValue response" in {
////      val expectedResponse: JsValue = JsString("Person Details")
////
////      setupMockHttpV2Get("https://hip.ws.ibt.hmrc.gov.uk/voa-prototype/api/persondetails")(expectedResponse)
////
////      val result = connector.callPersonDetails(testHipHeaders)
////      result.futureValue shouldBe expectedResponse
////    }
////  }
////
////  "callItems()" should {
////    "return a successful JsValue response" in {
////      val expectedResponse: JsValue = JsString("Item Details")
////
////      setupMockHttpV2Get("https://hip.ws.ibt.hmrc.gov.uk/voa-prototype/api/item")(expectedResponse)
////
////      val result = connector.callItems(testHipHeaders)
////      result.futureValue shouldBe expectedResponse
////    }
//  //}
//}
//
