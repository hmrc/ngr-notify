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

package uk.gov.hmrc.ngrnotify.model.bridge

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe
import play.api.libs.json.{JsNull, JsObject}

class MetadataSpec extends AnyFreeSpec {
  "serialisation" - {
    import play.api.libs.json.Json

    "should serialize and deserialize Metadata correctly when the JsValue is empty object" in {
      val empty = JsObject.empty
      val metadata: Metadata = Metadata(
        Sending(Extracting(empty), Transforming(empty, empty, empty), Loading(empty, empty, Signing(), empty, empty)),
        Receiving(Unloading(empty, empty, empty, empty, empty), TransformingReceiving(empty, empty, empty), Storing(empty))
      )

      val json = Json.parse(
        """
          |{
          |    "sending": {
          |      "extracting": {
          |        "selecting": {}
          |      },
          |      "transforming": {
          |        "filtering": {},
          |        "supplementing": {},
          |        "recontextualising": {}
          |      },
          |      "loading": {
          |        "readying": {},
          |        "assuring": {},
          |        "signing": {},
          |        "encrypting": {},
          |        "sending": {}
          |      }
          |    },
          |    "receiving": {
          |      "unloading": {
          |        "receiving": {},
          |        "decrypting": {},
          |        "verifying": {},
          |        "assuring": {},
          |        "readying": {}
          |      },
          |      "transforming": {
          |        "recontextualising": {},
          |        "dropping": {},
          |        "restoring": {}
          |      },
          |      "storing": {
          |        "inserting": {}
          |      }
          |    }
          |  }
          |""".stripMargin
      )

      val deserializedMetadata = json.as[Metadata]
      deserializedMetadata mustBe metadata
      Json.toJson(deserializedMetadata) mustBe json

    }

    "should correctly serialize and deserialize Metadata when the JsValue is null" in {
      val empty = JsNull
      val metadata: Metadata = Metadata(
        Sending(Extracting(empty), Transforming(empty, empty, empty), Loading(empty, empty, Signing(), empty, empty)),
        Receiving(Unloading(empty, empty, empty, empty, empty), TransformingReceiving(empty, empty, empty), Storing(empty))
      )

      val json = Json.parse(
        """
          |{
          |    "sending": {
          |      "extracting": {
          |        "selecting": null
          |      },
          |      "transforming": {
          |        "filtering": null,
          |        "supplementing": null,
          |        "recontextualising": null
          |      },
          |      "loading": {
          |        "readying": null,
          |        "assuring": null,
          |        "signing": {},
          |        "encrypting": null,
          |        "sending": null
          |      }
          |    },
          |    "receiving": {
          |      "unloading": {
          |        "receiving": null,
          |        "decrypting": null,
          |        "verifying": null,
          |        "assuring": null,
          |        "readying": null
          |      },
          |      "transforming": {
          |        "recontextualising": null,
          |        "dropping": null,
          |        "restoring": null
          |      },
          |      "storing": {
          |        "inserting": null
          |      }
          |    }
          |  }
          |""".stripMargin
      )

      val deserializedMetadata = json.as[Metadata]
      deserializedMetadata mustBe metadata
      Json.toJson(deserializedMetadata) mustBe json

    }


  }
}
