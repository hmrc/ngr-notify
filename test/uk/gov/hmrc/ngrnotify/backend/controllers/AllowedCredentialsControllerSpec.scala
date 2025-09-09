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

package uk.gov.hmrc.ngrnotify.backend.controllers

import uk.gov.hmrc.ngrnotify.backend.base.AnyWordAppSpec
import play.api.test.Helpers.*
import play.api.test.FakeRequest
import play.api.libs.json.Json
import uk.gov.hmrc.ngrnotify.controllers.AllowedCredentialsController
import uk.gov.hmrc.ngrnotify.repository.AllowedCredentialsRepo
import scala.concurrent.Future
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

class AllowedCredentialsControllerSpec extends AnyWordAppSpec {

  private val mockRepo = mock[AllowedCredentialsRepo]

  private val controller =
    inject[AllowedCredentialsController]

  override def fakeApplication(): Application = {
    when(mockRepo.isAllowed("test-cred-id")).thenReturn(Future.successful(true))
    when(mockRepo.isAllowed("unknown-cred-id")).thenReturn(Future.successful(false))

    new GuiceApplicationBuilder()
      .overrides(bind[AllowedCredentialsRepo].toInstance(mockRepo))
      .build()
  }

  "AllowedCredentialsController" should {

    "return 200" in {
      val result = controller.isAllowedInPrivateBeta("test-cred-id").apply(FakeRequest())

      status(result)        shouldBe OK
      contentAsJson(result) shouldBe Json.obj("allowed" -> true)
    }

    "return 403" in {
      val result = controller.isAllowedInPrivateBeta("unknown-cred-id").apply(FakeRequest())

      status(result)        shouldBe FORBIDDEN
      contentAsJson(result) shouldBe Json.obj("allowed" -> false)
    }
  }
}
