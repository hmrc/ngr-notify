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
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers.shouldBe
import play.api.libs.json.Json

class ProductEntitySpec extends AnyFreeSpec {
  "be serialized back to JSON matching the original get-properties-response example" in {
    val text =
      """
        |  {
        |          "id": 1,
        |          "idx": "1",
        |          "name": "string",
        |          "label": "string",
        |          "description": "string",
        |          "origination": "string",
        |          "termination": "string",
        |          "category": {
        |            "code": "LTX-DOM-PSN",
        |            "meaning": "string"
        |          },
        |          "type": {
        |            "code": null,
        |            "meaning": "string"
        |          },
        |          "class": {
        |            "code": null,
        |            "meaning": "string"
        |          },
        |          "data": {
        |            "foreign_ids": [
        |              {
        |                "system": "Government_Gateway",
        |                "location": "string",
        |                "value": "string"
        |              }
        |            ],
        |            "foreign_names": [
        |              {
        |                "system": "Government_Gateway",
        |                "location": "string",
        |                "value": "string"
        |              }
        |            ],
        |            "foreign_labels": [
        |              {
        |                "system": "Government_Gateway",
        |                "location": "string",
        |                "value": "string"
        |              }
        |            ],
        |            "names": {
        |              "title_common": "Miss",
        |              "title_uncommon": "Dr",
        |              "forenames": "Oliver James",
        |              "surname": "Harrison",
        |              "post_nominals": "BSc",
        |              "corporate_name": "Royal Society of Arts (RSC)",
        |              "crown_name": "Lord Chancellor",
        |              "known_as": "Ollie"
        |            },
        |            "communications": {
        |              "postal_address": "",
        |              "telephone_number": "",
        |              "email": ""
        |            }
        |          },
        |          "protodata": [
        |            {
        |              "node": "string",
        |              "mime_type": "image/jpeg",
        |              "label": "string",
        |              "is_pointer": true,
        |              "pointer": "string",
        |              "data": ""
        |            }
        |          ],
        |          "metadata": {
        |            "sending": {
        |              "extracting": {
        |                "selecting": null
        |              },
        |              "transforming": {
        |                "filtering": null,
        |                "supplementing": null,
        |                "recontextualising": null
        |              },
        |              "loading": {
        |                "readying": null,
        |                "assuring": null,
        |                "signing": {
        |                  "inputs": {}
        |                },
        |                "encrypting": null,
        |                "sending": null
        |              }
        |            },
        |            "receiving": {
        |              "unloading": {
        |                "receiving": null,
        |                "decrypting": null,
        |                "verifying": null,
        |                "assuring": null,
        |                "readying": null
        |              },
        |              "transforming": {
        |                "recontextualising": null,
        |                "dropping": null,
        |                "restoring": null
        |              },
        |              "storing": {
        |                "inserting": null
        |              }
        |            }
        |          },
        |          "compartments": null,
        |          "items": [
        |            {
        |              "id": 1,
        |              "idx": "1",
        |              "name": "string",
        |              "label": "string",
        |              "description": "string",
        |              "origination": "string",
        |              "termination": "string",
        |              "category": {
        |                "code": "LTX-DOM-PSA",
        |                "meaning": "string"
        |              },
        |              "type": {
        |                "code": null,
        |                "meaning": "string"
        |              },
        |              "class": {
        |                "code": null,
        |                "meaning": "string"
        |              },
        |              "data": {
        |                "foreign_ids": [
        |                  {}
        |                ],
        |                "foreign_names": [
        |                  {}
        |                ],
        |                "foreign_labels": [
        |                  {}
        |                ],
        |                "names": {},
        |                "communications": {}
        |              },
        |              "protodata": [
        |                {}
        |              ],
        |              "metadata": {
        |                "sending": {
        |                  "extracting": {},
        |                  "transforming": {},
        |                  "loading": {}
        |                },
        |                "receiving": {
        |                  "unloading": {},
        |                  "transforming": {},
        |                  "storing": {}
        |                }
        |              },
        |              "compartments": null,
        |              "items": []
        |            }
        |          ]
        |        }
        |""".stripMargin
    val json = Json.parse(text)
    val jobMessage = json.as[ProductEntity]
    val serialized = Json.toJson(jobMessage)
    serialized shouldBe json
  }
}
