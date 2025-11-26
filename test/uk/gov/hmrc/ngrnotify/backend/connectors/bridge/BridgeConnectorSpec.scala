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

//  Note that if we keep writing controller's unit test similar to the RatepayerControllerSpec,
//  we may not need an additional unit test for our BridgeConnector component.
//
//  In the RatepayerControllerSpec unit test, a real BridgeConnector instance receives a mock
//  HttpClient instance by the Guice injector. For each test case the mock is instructed to
//  behave like the Bridge API service is "supposed to behave".
//
//  Be aware that "is supposed to behave" means that at the moment (today is November 2025)
//  there's NO guarantee on the stability of the Bridge API service design. It could change
//  with short (or no) notice.
//
//  Therefore, for the time being, we'll be "playing with" a BridgeConnectorIntegrationSpec
//  instead of this unit test.
//
//  The integration test is written to connect to the real Bridge API (which we assume to
//  be always available over the development network). This approach provides more realistic
//  insights into the actual behaviors of Bridge, and detects any eventual change of its
//  protocol.
//

class BridgeConnectorSpec
