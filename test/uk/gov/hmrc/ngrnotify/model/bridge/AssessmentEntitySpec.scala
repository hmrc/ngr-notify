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
import play.api.libs.json.{JsValue, Json}

class AssessmentEntitySpec extends AnyFreeSpec {

  val json: JsValue = Json.parse("""
                          |{
                          |    "id": 9,
                          |    "idx": "null",
                          |    "name": "null",
                          |    "label": "null",
                          |    "description": "null",
                          |    "origination": "null",
                          |    "termination": "null",
                          |    "category": {
                          |      "code": "INF",
                          |      "meaning": "Assisting information"
                          |    },
                          |     "type": {
                          |      "code": "INF",
                          |      "meaning": "Assisting information"
                          |    },
                          |    "class": {
                          |    "code": "null",
                          |    "meaning": "null"
                          |  },
                          |    "data": {
                          |    "foreign_ids": [ {
                          |    "system": "Government_Gateway",
                          |    "location": "hmrc/voa/cdb/ndr_assessments",
                          |    "value": "27399688000"
                          |  }
                          |    ],
                          |    "foreign_names": [],
                          |    "foreign_labels": [],
                          |    "property": {
                          |    "id": 1111,
                          |    "cdb_id": "Government_Gateway"
                          |  },
                          |    "use": {
                          |    "is_composite": "null",
                          |    "is_part_exempt": "null",
                          |    "description": "null"
                          |  },
                          |    "valuation_surveys": [],
                          |    "valuation": {
                          |    "system": "null",
                          |    "id": "null",
                          |    "method_code": "null",
                          |    "rateable_value": 123,
                          |    "effective_date": "null"
                          |  },
                          |    "list": {
                          |    "category": "null",
                          |    "function": "null",
                          |    "year": "null",
                          |    "authority_code": "null"
                          |  },
                          |    "workflow": {
                          |    "cdb_job_id": "null"
                          |  }
                          |  },
                          |    "protodata": [ {
                          |    "node": null,
                          |    "mime_type": "",
                          |    "label": "",
                          |    "is_pointer": null,
                          |    "pointer": "",
                          |    "data": ""
                          |  }
                          |    ],
                          |    "metadata": {
                          |    "sending": {
                          |    "extracting": {
                          |    "selecting": {}
                          |  },
                          |    "transforming": {
                          |    "filtering": {},
                          |    "supplementing": {},
                          |    "recontextualising": {}
                          |  },
                          |    "loading": {
                          |    "readying": {},
                          |    "assuring": {},
                          |    "signing": {},
                          |    "encrypting": {},
                          |    "sending": {}
                          |  }
                          |  },
                          |    "receiving": {
                          |    "unloading": {
                          |    "receiving": {},
                          |    "decrypting": {},
                          |    "verifying": {},
                          |    "assuring": {},
                          |    "readying": {}
                          |  },
                          |    "transforming": {
                          |    "recontextualising": {},
                          |    "dropping": {},
                          |    "restoring": {}
                          |  },
                          |    "storing": {
                          |    "inserting": {}
                          |  }
                          |  }
                          |  },
                          |    "compartments": {},
                          |    "items": []
                          |  }
                          |   """.stripMargin)


  "AssessmentEntity" - {
    "should deserialize from JSON" in {
      val assessmentEntity = json.as[AssessmentEntity]
      assert(assessmentEntity.isInstanceOf[AssessmentEntity])
    }

  }
}
