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
import play.api.libs.json.{JsArray, JsValue, Json}

class SurveyEntitySpec extends AnyFreeSpec {
  "ProductEntitySpec" - {
    "serialization and deserialization of ProductEntity" in {
      val json = Json.parse(testResourceContent("survey.json"))

      val surveyEntity = json.as[SurveyEntity]
      val serialized  = Json.toJson(surveyEntity)

      Json.prettyPrint(serialized) mustBe Json.prettyPrint(json)
    }

    // Recursively traverse and display the hierarchy
    def displayHierarchy(node: JsValue, level: Int = 1): Unit = {
      val label = (node \ "label").asOpt[String].getOrElse("No Label")
      println(s"${"  " * (level - 1)}Level $level: $label")
        // Display facilities at each level
      val facilities = (node \ "data" \ "facilities").asOpt[JsArray].getOrElse(Json.arr())
      facilities.value.foreach { facility =>
        val desc = (facility \ "description" \ "value").asOpt[String].getOrElse("No Description")
        val qty = (facility \ "quantity" \ "value").asOpt[Double].getOrElse(0.0)
        val units = (facility \ "units" \ "value").asOpt[String].getOrElse("")
        println(s"${"  " * level}Facility: $desc, Quantity: $qty, Units: $units")
      }


      // At level 5 (room/unit), display dimensional data
      if (level == 5) {

        val uses = (node \ "data" \ "uses").asOpt[JsArray].getOrElse(Json.arr())
        uses.value.foreach { use =>
          val desc = (use \ "description" \ "value").asOpt[String].getOrElse("No Description")
          val qty = (use \ "quantity" \ "value").asOpt[Double].getOrElse(0.0)
          val units = (use \ "units" \ "value").asOpt[String].getOrElse("")
          println(s"${"  " * level}Area: $qty $units ($desc)")
        }
      }

      // Traverse child items
      (node \ "items").asOpt[JsArray].foreach { items =>
        items.value.foreach { child =>
          displayHierarchy(child, level + 1)
        }
      }
    }
    
    "load test resource content" in {
      val json = Json.parse(testResourceContent("survey.json"))

      // Start from the root survey node
      val rootItems = (json \ "items").as[JsArray]
      rootItems.value.foreach { site =>
        displayHierarchy(site)
      }

    }
    
    
  }
}
