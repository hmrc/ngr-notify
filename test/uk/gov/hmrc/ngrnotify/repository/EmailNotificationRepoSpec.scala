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

package uk.gov.hmrc.ngrnotify.repository

import org.mongodb.scala.bson.ObjectId
import org.scalactic.source.Position
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.ngrnotify.model.db.EmailNotification
import uk.gov.hmrc.ngrnotify.repository.EmailNotificationRepo

class EmailNotificationRepoSpec
  extends AnyFreeSpec
    with Matchers
    with DefaultPlayMongoRepositorySupport[EmailNotification]
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar {

  private val emailNotification = EmailNotification(
    emailTemplateId = uk.gov.hmrc.ngrnotify.model.EmailTemplate.ngr_registration_successful,
    trackerId = java.util.UUID.randomUUID(),
    sendToEmails = Seq("email@test.com"),
    templateParams = Json.obj("key" -> "value"),
    callbackUrl = Some("http://callback.url"),
    client = Some("test-client"))


  protected override val repository: EmailNotificationRepo = new EmailNotificationRepo(
    mongo = mongoComponent,
  )(using scala.concurrent.ExecutionContext.Implicits.global)

  ".save" - {

    "when there is a record for this id" - {

      "must update the lastUpdated time and get the record" in {
        val expectedData = emailNotification.copy(_id = new ObjectId("507f1f77bcf86cd799439011"))
        insert(emailNotification.copy(_id = new ObjectId("507f1f77bcf86cd799439012"))).futureValue
        insert(emailNotification.copy(_id = new ObjectId("507f1f77bcf86cd799439013"))).futureValue
        insert(expectedData).futureValue

        val result = repository.find(expectedData._id).futureValue

        result.value mustEqual expectedData
      }
    }

    "when there is no record for this id" - {

      "must return None" in {

        repository.find(new ObjectId("507f1f77bcf86cd799439013")).futureValue must not be defined
      }
    }

  }

  ".delete" - {

    "must remove a record" in {

      insert(emailNotification).futureValue

      val result = repository.delete(emailNotification._id).futureValue

      repository.find(emailNotification._id).futureValue must not be defined
    }

  }
}

