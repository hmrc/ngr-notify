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

package uk.gov.hmrc.ngrnotify.exporter

import com.mongodb.client.result.DeleteResult
import org.bson.types.ObjectId
import org.scalatest.FutureOutcome
import org.scalatest.flatspec.FixtureAsyncFlatSpec
import play.api.Configuration
import play.api.http.Status.{BAD_REQUEST, OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrnotify.backend.base.MockitoExtendedSugar
import uk.gov.hmrc.ngrnotify.config.{AppConfig, NGRAudit}
import uk.gov.hmrc.ngrnotify.connectors.{CallbackConnector, EmailConnector}
import uk.gov.hmrc.ngrnotify.model.EmailTemplate
import uk.gov.hmrc.ngrnotify.model.EmailTemplate.{ngr_add_property_request_sent, ngr_registration_successful}
import uk.gov.hmrc.ngrnotify.model.ErrorCode.{BAD_REQUEST_BODY, WRONG_RESPONSE_STATUS}
import uk.gov.hmrc.ngrnotify.model.db.EmailNotification
import uk.gov.hmrc.ngrnotify.repository.EmailNotificationRepo
import uk.gov.hmrc.play.audit.http.config.AuditingConfig

import java.time.temporal.ChronoUnit.{DAYS, HOURS}
import java.time.{Clock, Instant, ZoneId}
import java.util.UUID
import scala.concurrent.Future.{failed, successful}

class ExportEmailNotificationSpec extends FixtureAsyncFlatSpec with MockitoExtendedSugar:

  it should "delete when notification was kept in the queue for too long" in { fixture =>
    import fixture.*

    // Setup the queue with a notification that was created 2 days ago
    val notification = newNotificationExampleWithCreateAt(whenItAllBegins.minus(2, DAYS))
    enqueueNotifications(notification)

    // Exercise the method under test
    subjectUnderTest.exportNow(size = 100).map { _ =>

      // Verify that the notification was simply deleted (and never sent)
      verify(notificationRepo).getNotificationsBatch(100)
      verify(notificationRepo).delete(notification._id)
      verify(emailConnector, never).sendEmailNotification(notification)

      succeed
    }
  }

  it should "send a notification email and handle the 'good' scenario" in { fixture =>
    import fixture.*

    // Setup the queue with a good  notification example
    val notification = newNotificationExampleWithTemplate(ngr_add_property_request_sent)
    enqueueNotifications(notification)

    // Instruct the emailConnector mock to successfully reply with a "good" response
    when(emailConnector.sendEmailNotification(notification)).thenReturn(
      successful(
        HttpResponse(
          status = OK,
          body = ""
        )
      )
    )

    // Exercise the method under test
    subjectUnderTest.exportNow(size = 100).map { _ =>

      // Verify that the notification was actually sent (and thereafter deleted from the queue)
      verify(emailConnector).sendEmailNotification(notification)
      verify(notificationRepo).delete(notification._id)
      succeed
    }
  }

  it should "send a notification email and handle the 'bad request' scenario" in { fixture =>
    import fixture.*

    // Setup the queue with a new notification example
    val notification = newNotificationExample
    enqueueNotifications(notification)

    // Instruct the emailConnector mock to successfully reply with a "bad request" response
    when(emailConnector.sendEmailNotification(notification)).thenReturn(
      successful(
        HttpResponse(
          status = BAD_REQUEST,
          body = """ {"message":"bad request", "reason":"the notification was missing something important"}  """
        )
      )
    )

    // Exercise the method under test
    subjectUnderTest.exportNow(size = 100).map { _ =>

      // Verify that the notification was actually sent (but never deleted from the queue)
      verify(callbackConnector).callbackOnFailure(
        notification,
        BAD_REQUEST,
        BAD_REQUEST_BODY,
        "bad request. the notification was missing something important"
      )
      verify(notificationRepo, never).delete(notification._id)
      succeed
    }
  }

  it should "send a notification email and handle the 'service unavailable' scenario" in { fixture =>
    import fixture.*

    // Setup the queue with a new notification example
    val notification = newNotificationExample
    enqueueNotifications(notification)

    // Instruct the emailConnector mock to successfully reply with a "service unavailable" response
    when(emailConnector.sendEmailNotification(notification)).thenReturn(
      successful(
        HttpResponse(
          status = SERVICE_UNAVAILABLE,
          body = ""
        )
      )
    )

    // Exercise the method under test
    subjectUnderTest.exportNow(size = 100).map { _ =>

      // Verify that the notification was actually sent (but never deleted from the queue)
      verify(callbackConnector).callbackOnFailure(
        notification,
        SERVICE_UNAVAILABLE,
        WRONG_RESPONSE_STATUS,
        s"Send email to user FAILED: 503 "
      )
      verify(notificationRepo, never).delete(notification._id)
      succeed
    }
  }

  it should "send a notification email and handle the 'networking issues' scenario" in { fixture =>
    import fixture.*

    // Setup the queue with a new notification example
    val notification = newNotificationExample
    enqueueNotifications(notification)

    val networkingIssuesException = new Exception("networking issues")

    // Instruct the emailConnector mock to fail with the 'networking issues' exception
    when(emailConnector.sendEmailNotification(notification)).thenReturn(
      failed(
        networkingIssuesException
      )
    )

    // Exercise the method under test
    subjectUnderTest.exportNow(size = 100).map { _ =>
      // Verify that the notification was actually sent (but never deleted from the queue)
      verify(callbackConnector).callbackOnFailure(notification, networkingIssuesException)
      verify(notificationRepo, never).delete(notification._id)
      succeed
    }
  }

  val whenItAllBegins = Instant.EPOCH

  case class EmailNotificationFixture(
    notificationRepo: EmailNotificationRepo,
    emailConnector: EmailConnector,
    callbackConnector: CallbackConnector,
    subjectUnderTest: ExportEmailNotification
  ) {

    private val notification = EmailNotification(
      emailTemplateId = ngr_registration_successful,
      trackerId = UUID.randomUUID(),
      sendToEmails = Seq("somebody@somewhere.com"),
      templateParams = Json.obj("prop1" -> "value1"),
      callbackUrl = Some("https://someapp.net/callback"),
      client = None,
      _id = new ObjectId(),
      createdAt = whenItAllBegins
    )

    def enqueueNotifications(emailNotifications: EmailNotification*) =
      when(notificationRepo.getNotificationsBatch(any)).thenReturn(successful(emailNotifications.toList))

    def newNotificationExample =
      notification.copy(_id = new ObjectId())

    def newNotificationExampleWithTemplate(template: EmailTemplate) =
      newNotificationExample.copy(emailTemplateId = template)

    def newNotificationExampleWithCreateAt(instant: Instant) =
      newNotificationExample.copy(createdAt = instant)
  }

  type FixtureParam = EmailNotificationFixture

  override def withFixture(testCase: OneArgAsyncTest): FutureOutcome =
    val configuration = Configuration.from(
      Map(
        "appName"                         -> "ngr-notify",
        "sendSubmission.enabled"          -> "false",
        "sendSubmission.retryWindowHours" -> "0",
        "sendSubmission.frequencySeconds" -> "0",
        "sendSubmission.batchSize"        -> "0",
        "validationImport.hourToRunAt"    -> "0",
        "validationImport.minuteToRunAt"  -> "0",
        "auditing.enabled "               -> "false",
        // ------
        "sendSubmission.retryWindowHours" -> "24" // 1 day
      )
    )

    // Instruct the notificationRepo mock
    val notificationRepo = mock[EmailNotificationRepo]
    when(notificationRepo.delete(any)).thenReturn(successful(DeleteResult.unacknowledged()))

    val callbackConnector = mock[CallbackConnector]
    when(callbackConnector.callbackOnFailure(any, any, any, any)).thenReturn(successful(()))

    val emailConnector = mock[EmailConnector]

    val theFixture = EmailNotificationFixture(
      notificationRepo,
      emailConnector,
      callbackConnector,
      subjectUnderTest = new ExportEmailNotificationImpl(
        notificationRepo,
        clock = {
          val twelveHoursAfter = whenItAllBegins.plus(12, HOURS)
          Clock.fixed(twelveHoursAfter, ZoneId.of("UTC"))
        },
        audit = NGRAudit(
          auditingConfig = AuditingConfig.fromConfig(configuration),
          auditChannel = null,
          datastreamMetrics = null
        ),
        emailConnector,
        callbackConnector,
        forConfig = AppConfig(configuration)
      )
    )
    testCase.apply(theFixture)
