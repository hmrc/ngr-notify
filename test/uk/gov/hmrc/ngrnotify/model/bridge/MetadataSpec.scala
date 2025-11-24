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
import play.api.libs.json.Json
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeJobModel.{Extracting, Loading, Receiving, Sending, Storing, TransformingReceiving, TransformingSending, Unloading}

class MetadataSpec extends AnyFreeSpec {
    "Metadata" - {
      "serializes and deserializes correctly" in {
        val metadata = Metadata(
          sending = Sending(
            extracting = Extracting(),
            transforming = TransformingSending(),
            loading = Loading()
          ),
          receiving = Receiving(
            unloading = Unloading(),
            transforming = TransformingReceiving(),
            storing = Storing()
          )
        )

        val json = play.api.libs.json.Json.toJson(metadata)
        val fromJson = json.as[Metadata]

        fromJson mustBe metadata
      }

      "deserializes from JSON example file" in {
        val metadataJson = Json.parse(
          """
            |{
            |  "sending": {
            |    "extracting": {
            |      "selecting": {}
            |    },
            |    "transforming": {
            |      "filtering": {},
            |      "supplementing": {},
            |      "recontextualising": {}
            |    },
            |    "loading": {
            |      "readying": {},
            |      "assuring": {},
            |      "signing": {},
            |      "encrypting": {},
            |      "sending": {}
            |    }
            |  },
            |  "receiving": {
            |    "unloading": {
            |      "receiving": {},
            |      "decrypting": {},
            |      "verifying": {},
            |      "assuring": {},
            |      "readying": {}
            |    },
            |    "transforming": {
            |      "recontextualising": {},
            |      "dropping": {},
            |      "restoring": {}
            |    },
            |    "storing": {
            |      "inserting": {}
            |    }
            |  }
            |}
            |""".stripMargin)

        val metadata = metadataJson.as[Metadata]
        val expectedMetadata = Metadata(Sending(Extracting(Map()),TransformingSending(Map(),Map(),Map()),Loading(Some(Map()),Some(Map()),Some(Signing(None)),Some(Map()),Some(Map()))),Receiving(Unloading(Map(),Map(),Map(),Map(),Map()),TransformingReceiving(Map(),Map(),Map()),Storing(Map())))

        metadata mustBe expectedMetadata
      }
    }
}
