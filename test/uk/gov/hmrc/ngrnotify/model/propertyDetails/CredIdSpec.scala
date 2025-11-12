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

import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustEqual
import play.api.libs.json.JsString
import play.api.mvc.PathBindable

class CredIdSpec extends AnyFreeSpec {
  "CredId" - {
    "should create an instance with the correct value" in {
      val credIdValue = "test-cred-123"
      val credId = CredId(credIdValue)
      credId.value mustEqual credIdValue
    }

    "toString should return the correct string representation" in {
      val credIdValue = "test-cred-456"
      val credId = CredId(credIdValue)
      credId.toString mustEqual credIdValue
    }

    "JSON serialization and deserialization" - {
      import play.api.libs.json.Json

      "should serialize CredId to JSON" in {
        val credId = CredId("json-cred-123")
        val json = Json.toJson(credId)
        json mustEqual JsString("json-cred-123")
      }

      "should deserialize JSON to CredId" in {
        val json = JsString("json-cred-456")
        val credId = json.as[CredId]
        credId mustEqual CredId("json-cred-456")
      }
    }
  }
  
  "pathBindable" - {
    "should bind a valid string to CredId" in {
      val pathBindable = implicitly[PathBindable[CredId]]
      val credId = CredId("AB123")

      val bind: Either[String, CredId] = pathBindable.bind("credId", "AB123")
      bind.value mustEqual credId
    }

    "should unbind a CredId to a string" in {
      val key = "credId"
      val credId = CredId("test-cred-789")
      val unbind: String = CredId.pathBindable.unbind(key, credId)
      unbind mustEqual  "test-cred-789"
    }
  }
}
