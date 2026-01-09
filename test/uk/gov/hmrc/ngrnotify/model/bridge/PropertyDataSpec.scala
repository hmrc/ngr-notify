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

class PropertyDataSpec extends AnyFreeSpec {
  "PropertyData" - {
    
    val json = """
      |{
      |  "property": {
      |    "property_id": 1,
      |    "cdb_property_id": 12345678
      |  },
      |  "address": {
      |    "line_1": "10 Downing Street",
      |    "line_2": "Westminster",
      |    "line_3": null,
      |    "line_4": null,
      |    "line_5": null,
      |    "postcode": "SW1A 2AA"
      |  }
      |}
      |""".stripMargin
      
  }
}
