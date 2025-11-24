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
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeJobModel.{Extracting, Loading, Receiving, Sending, Storing, TransformingReceiving, TransformingSending, Unloading}

class DataSpec extends AnyFreeSpec {
  "Data model" - {
    "serialization and deserialization" in {
      val data = PropertyEntityData(
        foreign_ids = Seq(ForeignId(Some(System.GovernmentGateway), Some("ID1"), Some("Value1"))),
        foreign_names = Seq(ForeignId(Some(System.GovernmentGateway), Some("ID1"), Some("Value1"))),
        foreign_labels = Seq(ForeignId(Some(System.GovernmentGateway), Some("ID1"), Some("Value1"))),
        addresses = Some(PropertyAddresses(Some("line1"), Some("line2"), Some("line3"), Some("line4"))),
        location = Some(Location(
          localAuthorityPseudoAreaCode = Some("51.5074"),
          ordnanceSurveyCoordinates = Some("0.1278"),
          googleMapsCoordinates = Some("15.0")
        )),
        assessments = Some(Seq(
          BridgeJobModel.JobItem(
            id = Some(1),
            idx = Some("A001"),
            name = Some("Assessment Name"),
            label = Some("Assessment Label"),
            description = Some("Description of assessment"),
            category = BridgeJobModel.CodeMeaning(Some("CAT"), Some("Category")),
            `type` = BridgeJobModel.CodeMeaning(Some("TYPE"), Some("Type")),
            `class` = BridgeJobModel.CodeMeaning(Some("CLASS"), Some("Class")),
            data = PropertyEntityData(),
            protodata = Seq.empty,
            metadata = Metadata(Sending(Extracting(), TransformingSending(), Loading()), Receiving(Unloading(), TransformingReceiving(), Storing())),
            compartments = BridgeJobModel.Compartments()
          )
        ))
      )

      val json = uk.gov.hmrc.ngrnotify.model.bridge.Data.format.writes(data)
      val parsedData = uk.gov.hmrc.ngrnotify.model.bridge.Data.format.reads(json).get

      parsedData mustBe data
    }

    "handle different Data subtypes correctly" in {
      val personData = PersonEntityData(
        foreign_ids = Seq(ForeignId(Some(System.GovernmentGateway), Some("ID2"), Some("Value2"))),
        foreign_names = Seq(ForeignId(Some(System.GovernmentGateway), Some("ID2"), Some("Value2"))),
        foreign_labels = Seq(ForeignId(Some(System.GovernmentGateway), Some("ID2"), Some("Value2"))),
        names = Some(BridgeJobModel.Names(
          title_common = Some("Mr"),
          title_uncommon = Some("Sir"),
          forenames = Some("John"),
          surname = Some("Doe"),
          None, None, None, None
        )),
        communications = Some(Communications(
          email = Some("email@ example.com"),
          telephoneNumber = Some("0123456789"),
          postalAddress = Some("123 Example Street")
        ))
      )

      val json = uk.gov.hmrc.ngrnotify.model.bridge.Data.format.writes(personData)
      val parsedData = uk.gov.hmrc.ngrnotify.model.bridge.Data.format.reads(json).get
      parsedData mustBe personData
    }
  }
}