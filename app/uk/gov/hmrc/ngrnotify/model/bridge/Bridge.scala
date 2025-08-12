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

import play.api.libs.json.*


object Bridge:

  type Id = String

  given Reads[Id] = new Reads[Id] {
    def reads(jsValue: JsValue): JsResult[Id] =
      jsValue match
        case JsNumber(num) => JsSuccess(num.toString)
        case JsString(str) => JsSuccess(str)
        case _             => JsError("Expected a JSON number or string")
  }


  //
  //   NOTE
  //   -----
  //
  //   Note that the Bridge API engineers wanted to model the JSON `id` property as having
  //   either JSON null or JSON number (such as 123) or JSON string (such as "123") values.
  //
  //   Instead, we believe that the `id` JSON values should have been modeled as having either
  //   JSON null or JSON string value (such as "123"), simply because that would have included
  //   all practical scenarios of usage. Despite our belief, we might need to mirror the Bridge
  //   API design, and therefore employ Scala 3 union types.
  //
  //   We hope that the following alternative Scala code will not have to replace the above:
  //
  //      ┌──────────────────────────────────────────────────────────────────
  //      │ type Id = Int | String
  //      │
  //      │  given Reads[Id] = new Reads[Id] {
  //      │    def reads(jsValue: JsValue): JsResult[Id] =
  //      │      jsValue match
  //      │        case JsNumber(num) => JsSuccess(num.toInt)
  //      │        case JsString(str) => JsSuccess(str)
  //      │        case  _            => JsError("Expected an Int or String")
  //      │  }
  //      │
  //      │  given Writes[Id] = new Writes[Id] {
  //      │    override def writes(intOrString: Id): JsValue =
  //      │      intOrString match
  //      │        case int: Int => JsNumber(int)
  //      │        case str: String => JsString(str)
  //      │  }
  //      └──────────────────────────────────────────────────────────────────
  //
  //



