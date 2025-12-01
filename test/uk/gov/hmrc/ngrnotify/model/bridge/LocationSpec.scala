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

class LocationSpec extends AnyFreeSpec {

  "LocationSpec" - {
    "serialization and deserialization of Location" in {
      val location = Location(
        localAuthorityPseudoAreaCode = Some("LAPAC123"),
        ordanaceSurveyCoordinates = Some("OS123456"),
        googleMapsCoordinates = Some("GM123456")
      )

      val jsonString =
        """
          |{
          |  "local_authority_pseudo_area_code": "LAPAC123",
          |  "ordanace_survey_coordinates": "OS123456",
          |  "google_maps_coordinates": "GM123456"
          |}
          |""".stripMargin

      val json = play.api.libs.json.Json.parse(jsonString)

      // Test serialization
      val serializedJson = play.api.libs.json.Json.toJson(location)
      serializedJson mustBe json

      // Test deserialization
      val deserializedLocation = json.as[Location]
      deserializedLocation mustBe location
    }
  }

}
