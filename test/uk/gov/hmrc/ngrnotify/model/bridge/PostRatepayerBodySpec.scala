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

class PostRatepayerBodySpec extends AnyWordAppSpec {

  "Model PostRatepayer" should {

    "be serialised from JSON example file" in {
      val postRatepayerJson = Json.parse(testResourceContent("post-ratepayer-example.json"))
      val postRatepayer = postRatepayerJson.as[PostRatepayer]

      // round-trip should be idempotent
      Json.toJson(postRatepayer).as[PostRatepayer] shouldBe postRatepayer
    }

    "serialise and deserialise minimal object with defaults" in {
      val minimalJob = PostRatepayer.Job(
        id = None,
        idx = "IDX-001",
        name = "Minimal Job",
        label = "Label",
        description = "A minimal job description",
        origination = None,
        termination = None,
        category = PostRatepayer.CodeMeaning("CAT1", "Category"),
        `type` = PostRatepayer.CodeMeaning("TYPE1", "Type"),
        `class` = PostRatepayer.CodeMeaning("CLASS1", "Class"),
        data = PostRatepayer.Data(
          foreign_ids = Seq.empty,
          foreign_names = Seq.empty,
          foreign_labels = Seq.empty
        ),
        protodata = Seq.empty,
        metadata = PostRatepayer.Metadata(
          sending = PostRatepayer.MetadataStage(),
          receiving = PostRatepayer.MetadataStage()
        ),
        compartments = PostRatepayer.Compartments(),
        items = Seq.empty
      )

      val postRatepayer = PostRatepayer(
        $schema = "schema-v1",
        job = minimalJob
      )

      val json = Json.toJson(postRatepayer)
      val fromJson = json.as[PostRatepayer]

      fromJson shouldBe postRatepayer
    }

    "handle nested optional names and communications correctly" in {
      val data = PostRatepayer.Data(
        foreign_ids = Seq("fid1"),
        foreign_names = Seq("fName1"),
        foreign_labels = Seq("fLabel1"),
        names = Some(
          PostRatepayer.Names(
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
          PostRatepayer.Communications(
            postal_address = Some("123 Test Street"),
            telephone_number = Some("01234 567890"),
            email = Some("john@example.com")
          )
        )
      )

      val json = Json.toJson(data)
      val fromJson = json.as[PostRatepayer.Data]

      fromJson shouldBe data
    }

    "support metadata with empty maps and nested stages" in {
      val metadata = PostRatepayer.Metadata(
        sending = PostRatepayer.MetadataStage(
          extracting = PostRatepayer.MetadataAction(),
          transforming = PostRatepayer.MetadataTransform(),
          loading = Some(PostRatepayer.MetadataLoading()),
          unloading = Some(PostRatepayer.MetadataUnloading()),
          storing = Some(PostRatepayer.MetadataStoring())
        ),
        receiving = PostRatepayer.MetadataStage()
      )

      val json = Json.toJson(metadata)
      val fromJson = json.as[PostRatepayer.Metadata]

      fromJson shouldBe metadata
    }
  }
}