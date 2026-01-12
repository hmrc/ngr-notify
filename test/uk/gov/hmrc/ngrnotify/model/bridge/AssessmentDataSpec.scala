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

class AssessmentDataSpec extends AnyFreeSpec {

  "serialization and deserialization of AssessmentData" in {
    val json = Json.parse(
      """
        |{
        |"foreign_ids": [
        |  {
        |    "system": "HMRC-VOA_CDB",
        |    "location": "hmrc/voa/cdb/ndr_assessments",
        |    "value": "27399677000"
        |  }
        |],
        |"foreign_names": [],
        |"foreign_labels": [],
        |"property": {
        |  "property_id": 2,
        |  "cdb_property_id": 55821184
        |},
        |"use": {
        |  "is_composite": "N",
        |  "is_part_exempt": "N",
        |  "use_description": "RESTAURANT AND PREMISES"
        |},
        |"valuation_surveys": [],
        |"valuations": [],
        |"valuation": {
        |  "valuation_method_code": "234",
        |  "valuation_rateable": 76500,
        |  "valuation_effective_date": "20230529T000000Z"
        |},
        |"list": {
        |  "list_category": "LTX-DOM-LST",
        |  "list_function": "Charging",
        |  "list_year": "2023",
        |  "list_authority_code": "1160"
        |},
        |"workflow": {
        |  "cdb_job_id": 39115380283
        |}
        |        }
        |""".stripMargin
    )

    val model      = json.as[AssessmentData]
    val serialized = Json.toJson(model)
    serialized mustBe json

  }
}
