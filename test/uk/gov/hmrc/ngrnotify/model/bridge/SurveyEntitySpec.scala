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
import uk.gov.hmrc.ngrnotify.model.bridge.SurveyEntity.{LevelSummary, PhysicalDetails}
import uk.gov.hmrc.ngrnotify.model.propertyDetails.JobMessageTestData

class SurveyEntitySpec extends AnyFreeSpec with JobMessageTestData{

  "ProductEntitySpec" - {
    "serialization and deserialization of ProductEntity" in {
      val json = Json.parse(testResourceContent("survey.json"))

      val surveyEntity = json.as[SurveyEntity]
      val serialized = Json.toJson(surveyEntity)

      Json.prettyPrint(serialized) mustBe Json.prettyPrint(json)
    }

    "extractFloorAndParkingData to return empty lists when no items are present" in {
      val surveyEntity = sampleSurveyEntity

      val reviewDetails: SurveyEntity.ReviewDetails = SurveyEntity.extractFloorAndParkingData(surveyEntity, None)

      reviewDetails.floorsInfo mustBe Nil
      reviewDetails.parkingInfo mustBe Nil
      reviewDetails.totalArea mustBe 0
    }

    "extractFloorAndParkingData to return ReviewDetails" in {
      val json = Json.parse(testResourceContent("min_survey.json"))

      val surveyEntity = json.as[SurveyEntity]

      val reviewDetails: SurveyEntity.ReviewDetails = SurveyEntity.extractFloorAndParkingData(surveyEntity, None)

      reviewDetails.floorsInfo mustBe List(LevelSummary("Floor Level GF to GF", List(PhysicalDetails("retail zone a", 100.11, "m2"), PhysicalDetails("retail zone b", 200.22, "m2"), PhysicalDetails("retail zone c", 300.33, "m2")), 600.66))
      reviewDetails.parkingInfo mustBe List(LevelSummary("All Levels", List(PhysicalDetails("Surfaced open", 2, "car spaces")), 2))
      reviewDetails.otherAdditionInfo mustBe List(LevelSummary("All Levels", List(PhysicalDetails("Air conditioning system", 142.52, "m2"), PhysicalDetails("Land used for storage", 10.23, "m2")), 152.75))
      reviewDetails.totalArea mustBe 600.66
    }
  }
}
