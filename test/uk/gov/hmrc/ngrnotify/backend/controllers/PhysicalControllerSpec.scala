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

import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.testkit.NoMaterializer
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordAppSpec
import uk.gov.hmrc.ngrnotify.backend.testUtils.RequestBuilderStub
import uk.gov.hmrc.ngrnotify.controllers.PhysicalController
import uk.gov.hmrc.ngrnotify.model.propertyDetails.{AnythingElseData, ChangeToUseOfSpace, CredId, PropertyChangesRequest}

import java.time.LocalDate
import scala.collection.immutable.Seq

class PhysicalControllerSpec extends AnyWordAppSpec:

  given Materializer = NoMaterializer

  private def appWithResponse(status: Either[Throwable, Int], body: String): Application = {
    val httpClientV2Mock = mock[HttpClientV2]

    when(
      httpClientV2Mock.post(eqTo(url"http://localhost:1501/ngr-stub/hip/job/physical/"))(using any[HeaderCarrier])
    ).thenReturn(RequestBuilderStub(status, body))

    new GuiceApplicationBuilder()
      .overrides(bind[HttpClientV2].to(httpClientV2Mock))
      .build()
  }

  "PhysicalController" should {
    ".updatePropertyChanges return 202" in {

      val app = appWithResponse(Right(OK), """"OK"""")
      val controller = app.injector.instanceOf[PhysicalController]

      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody(Json.toJson(
          PropertyChangesRequest(
            CredId("credId"),
            LocalDate.of(2023, 1, 1),
            Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
            Seq(("airConditioning", "none"), ("securityCamera", "23")),
            Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
            Some(AnythingElseData(true, Some("addtional text"))),
            Seq("uploadId1", "uploadId2")
          )
        ))

      val result = controller.updatePropertyChanges()(fakeRequest)
      status(result)          shouldBe ACCEPTED
    }

    ".updatePropertyChanges return 400" in {
      val app = appWithResponse(Right(OK), """"OK"""")
      val controller = app.injector.instanceOf[PhysicalController]

      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")

      val result = controller.updatePropertyChanges()(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }

    ".updatePropertyChanges returns a buildValidationErrorsResponse" in {
      val app = appWithResponse(Right(OK), """"OK"""")
      val controller = app.injector.instanceOf[PhysicalController]

      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody(Json.obj())

      val result = controller.updatePropertyChanges()(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }

    ".updatePropertyChanges returns a BadRequest due to Receiving a BadRequest from hip" in {
      val app = appWithResponse(Right(BAD_REQUEST), """"BAD_REQUEST"""")
      val controller = app.injector.instanceOf[PhysicalController]

      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody((Json.toJson(
          PropertyChangesRequest(
            CredId("credId"),
            LocalDate.of(2023, 1, 1),
            Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
            Seq(("airConditioning", "none"), ("securityCamera", "23")),
            Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
            Some(AnythingElseData(true, Some("additional text"))),
            Seq("uploadId1", "uploadId2")
          )
        )))

      val result = controller.updatePropertyChanges()(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }

    ".updatePropertyChanges returns a InternalServerError due to Receiving a InternalServerError from hip" in {
      val app = appWithResponse(Right(INTERNAL_SERVER_ERROR), """"INTERNAL_SERVER_ERROR"""")
      val controller = app.injector.instanceOf[PhysicalController]

      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody((Json.toJson(
          PropertyChangesRequest(
            CredId("credId"),
            LocalDate.of(2023, 1, 1),
            Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
            Seq(("airConditioning", "none"), ("securityCamera", "23")),
            Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
            Some(AnythingElseData(true, Some("additional text"))),
            Seq("uploadId1", "uploadId2")
          )
        )))

      val result = controller.updatePropertyChanges()(fakeRequest)
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

    ".updatePropertyChanges returns a InternalServerError due to Receiving a error from hip" in {
      val app = appWithResponse(Left(Exception("something went wrong")), """"INTERNAL_SERVER_ERROR"""")
      val controller = app.injector.instanceOf[PhysicalController]

      val fakeRequest = FakeRequest("POST", "/")
        .withHeaders("Content-type" -> "application/json;charset=UTF-8")
        .withBody((Json.toJson(
          PropertyChangesRequest(
            CredId("credId"),
            LocalDate.of(2023, 1, 1),
            Some(ChangeToUseOfSpace(Seq("rearrangedTheUseOfSpace"), true, Some("REFzR42536T"))),
            Seq(("airConditioning", "none"), ("securityCamera", "23")),
            Seq(("loadingBays", "added"), ("lockupGarages", "removedSome")),
            Some(AnythingElseData(true, Some("additional text"))),
            Seq("uploadId1", "uploadId2")
          )
        )))

      val result = controller.updatePropertyChanges()(fakeRequest)
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }


  }
