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

class MetadataSpec extends AnyFreeSpec {
  "serialisation" - {
    import play.api.libs.json.Json

    "should serialize and deserialize Metadata correctly" in {
      val metadata: Metadata = Metadata(
        Sending(
          Extracting(Map()),
          Transforming(filtering = Map(), supplementing = Map(), recontextualising = Map()),
          Loading(readying = Map(), assuring = Map(), signing = Signing(None), encrypting = Map(), sending = Map())
        ),
        Receiving(
          unloading = Unloading(receiving = Map(), decrypting = Map(), verifying = Map(), assuring = Map(), readying = Map()),
          transforming = TransformingReceiving(recontextualising = Map(), dropping = Map(), restoring = Map()),
          storing = Storing(inserting = Map())
        )
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
      
      Json.toJson(deserializedMetadata) mustBe metadata

    }
  }
}
