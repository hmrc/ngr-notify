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

import play.api.libs.json.Json
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordAppSpec

class BridgeJobModelBodySpec extends AnyWordAppSpec {

  "Model PostRatepayer" should {

    "be serialised from JSON example file" in {
      val postRatepayerJson = Json.parse(testResourceContent("post-ratepayer-example.json"))
      val postRatepayer = postRatepayerJson.as[BridgeJobModel]

      // round-trip should be idempotent
      Json.toJson(postRatepayer).as[BridgeJobModel] shouldBe postRatepayer
    }

    "be serialised from get-ratepayer JSON example file" in {
      val postRatepayerJson = Json.parse(testResourceContent("get-ratepayer-response.json"))
      val postRatepayer = postRatepayerJson.as[BridgeJobModel]

      // round-trip should be idempotent
      Json.toJson(postRatepayer).as[BridgeJobModel] shouldBe postRatepayer
    }

    "serialise and deserialise minimal object with defaults" in {
      val minimalJob = BridgeJobModel.Job(
        id = None,
        idx = Some("IDX-001"),
        name = Some("Minimal Job"),
        label = Some("Label"),
        description = Some("A minimal job description"),
        origination = None,
        termination = None,
        category = BridgeJobModel.CodeMeaning(Some("CAT1"), Some("Category")),
        `type` = BridgeJobModel.CodeMeaning(Some("TYPE1"), Some("Type")),
        `class` = BridgeJobModel.CodeMeaning(Some("CLASS1"), Some("Class")),
        data = BridgeJobModel.Data(
          foreign_ids = Seq.empty,
          foreign_names = Seq.empty,
          foreign_labels = Seq.empty
        ),
        protodata = Seq.empty,
        metadata = BridgeJobModel.Metadata(
          sending = BridgeJobModel.MetadataStage(),
          receiving = BridgeJobModel.MetadataStage()
        ),
        compartments = BridgeJobModel.Compartments(),
        items = Some(Seq.empty)
      )

      val postRatepayer = BridgeJobModel(
        $schema = "schema-v1",
        job = minimalJob
      )

      val json = Json.toJson(postRatepayer)
      val fromJson = json.as[BridgeJobModel]

      fromJson shouldBe postRatepayer
    }

    "handle nested optional names and communications correctly" in {
      val data = BridgeJobModel.Data(
        foreign_ids = Seq("fid1"),
        foreign_names = Seq("fName1"),
        foreign_labels = Seq("fLabel1"),
        names = Some(
          BridgeJobModel.Names(
            title_common = Some("Mr"),
            title_uncommon = None,
            forenames = Some("John"),
            surname = Some("Smith"),
            post_nominals = None,
            corporate_name = None,
            crown_name = None,
            known_as = Some("Johnny")
          )
        ),
        communications = Some(
          BridgeJobModel.Communications(
            postal_address = Some("123 Test Street"),
            telephone_number = Some("01234 567890"),
            email = Some("john@example.com")
          )
        )
      )

      val json = Json.toJson(data)
      val fromJson = json.as[BridgeJobModel.Data]

      fromJson shouldBe data
    }

    "support metadata with empty maps and nested stages" in {
      val metadata = BridgeJobModel.Metadata(
        sending = BridgeJobModel.MetadataStage(
          extracting = BridgeJobModel.MetadataAction(),
          transforming = BridgeJobModel.MetadataTransform(),
          loading = Some(BridgeJobModel.MetadataLoading()),
          unloading = Some(BridgeJobModel.MetadataUnloading()),
          storing = Some(BridgeJobModel.MetadataStoring())
        ),
        receiving = BridgeJobModel.MetadataStage()
      )

      val json = Json.toJson(metadata)
      val fromJson = json.as[BridgeJobModel.Metadata]

      fromJson shouldBe metadata
    }
  }
}