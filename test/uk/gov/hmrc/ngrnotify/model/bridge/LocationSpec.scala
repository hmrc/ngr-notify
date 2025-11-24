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
  "Location JSON format" - {
    import play.api.libs.json.Json

    "should serialize and deserialize correctly" in {
      val location = Location(
        localAuthorityPseudoAreaCode = Some("51.5074"),
        ordanaceSurveyCoordinates = Some("0.1278"),
        googleMapsCoordinates = Some("15.0")
      )

      val json                = Json.toJson(location)
      val deserializedLocation = json.as[Location]
      deserializedLocation mustBe location
    }

    "serialize and deserialize with missing fields" in {
      val location = Location(
        localAuthorityPseudoAreaCode = None,
        ordanaceSurveyCoordinates = Some("0.1278"),
        googleMapsCoordinates = None
      )

      val expectedJson = Json.parse("""
                                      |{
                                      |  "local_authority_pseudo_area_code": null,
                                      |  "ordanace_survey_coordinates": "0.1278",
                                      |  "google_maps_coordinates": null
                                      |}
                                      |""".stripMargin)

      val json = Json.toJson(location)
      json mustBe expectedJson
    }
  }
}
