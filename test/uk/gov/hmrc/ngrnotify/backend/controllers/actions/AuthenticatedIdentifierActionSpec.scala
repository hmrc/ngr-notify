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

package uk.gov.hmrc.ngrnotify.controllers.actions
import org.apache.pekko.util.Timeout
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.api.{Application, inject}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class AuthenticatedIdentifierActionSpec extends AnyFreeSpec {

  class Harness(authAction: IdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
  }

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  implicit val timeout: Timeout = 5.seconds
  val application: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[AuthConnector].toInstance(mockAuthConnector)
    )
    .build()

  "AuthenticatedIdentifierAction" - {

    "when the user is logged in" - {
      "should return Ok when credentials are present" in {
        type AuthRetrievals = Option[Credentials]
        running(application) {
          when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(using any(), any()))
            .thenReturn(Future.successful(Some(Credentials("id", "provider"))))

          val action = application.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(action)
          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe OK
        }
      }

      "should throw NoSuchElementException when credentials are missing" in {
        type AuthRetrievals = Option[Credentials]
        running(application) {
          when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(using any(), any()))
            .thenReturn(Future.successful(None))

          val action = application.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(action)

          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe UNAUTHORIZED
        }
      }

      "should propagate other exceptions thrown by the auth connector" in {
        type AuthRetrievals = Option[Credentials]
        running(application) {
          when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(using any(), any()))
            .thenReturn(Future.failed(new RuntimeException("Some other auth error")))

          val action = application.injector.instanceOf[AuthenticatedIdentifierAction]
          val controller = new Harness(action)
          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe UNAUTHORIZED
        }
      }
    }

    "when the user hasn't logged in" - {
      "should fail with MissingBearerToken" in {
        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe UNAUTHORIZED
        }
      }
    }

    "when the user's session has expired" - {
      "should fail with BearerTokenExpired" in {
        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe UNAUTHORIZED
        }
      }
    }

    "when the user doesn't have sufficient enrolments" - {
      "should fail with InsufficientEnrolments" in {
        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientEnrolments), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe UNAUTHORIZED
        }
      }
    }

    "when the user doesn't have sufficient confidence level" - {
      "should fail with InsufficientConfidenceLevel" in {
        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientConfidenceLevel), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe UNAUTHORIZED
        }
      }
    }

    "when the user used an unaccepted auth provider" - {
      "should fail with UnsupportedAuthProvider" in {
        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAuthProvider), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe UNAUTHORIZED
        }
      }
    }

    "when the user has an unsupported credential role" - {
      "should fail with UnsupportedCredentialRole" in {
        running(application) {
          val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
          val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedCredentialRole), bodyParsers)
          val controller = new Harness(authAction)
          val result = controller.onPageLoad()(FakeRequest("", ""))
          status(result) mustBe UNAUTHORIZED
        }
      }
    }
  }

  class FakeFailingAuthConnector @Inject()(exceptionToReturn: Throwable) extends AuthConnector {
    val serviceUrl: String = ""
    override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
      Future.failed(exceptionToReturn)
  }
}
