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

  // TODO We may prefer to define the Id type as a synonym for String rather than employing Scala 3 unions
  type Id = Int | String

  given Reads[Id] = new Reads[Id] {
    def reads(jsValue: JsValue): JsResult[Id] =
      jsValue match
        case JsNumber(num) => JsSuccess(num.toInt)
        case JsString(str) => JsSuccess(str)
        case _ => JsError("Expected an Int or String")
  }

  given Writes[Id] = new Writes[Id] {
    override def writes(intOrString: Id): JsValue =
      intOrString match
        case int: Int => JsNumber(int)
        case str: String => JsString(str)
  }

  //
  //   NOTE
  //   -----
  //
  //   Note that the Bridge API engineers wanted to model the JSON `id` property as having
  //   either JSON null or JSON number (such as 123) or JSON string (such as "123") values.
  //
  //   We believe that the `id` JSON values should have been modelled as having either JSON
  //   null or JSON string value (such as "123"), simply because that would have included all
  //   practical scenarios of usage. Therefore, rather than mirroring the Bridge API design,
  //   and rather than having to employ Scala 3 union types, we may prefer to model our
  //   Scala `id` counterparts as of Scala "Option[String]" type.
  //
  //   Therefore, the following alternative Scala code may replace the above:
  //
  //      ┌──────────────────────────────────────────────────────────────────┐
  //      │ type Id = String                                                 │
  //      │                                                                  │
  //      │ given Reads[Id] = new Reads[Id] {                                │
  //      │   def reads(jsValue: JsValue): JsResult[Id] =                    │
  //      │     jsValue match                                                │
  //      │       case JsNumber(num) => JsSuccess(num.toString)              │
  //      │       case JsString(str) => JsSuccess(str)                       │
  //      │       case _             => JsError("Expected an Int or String") │
  //      │ }                                                                │
  //      │                                                                  │
  //      │ given Writes[Id] = new Writes {                                  │
  //      │   override def writes(id: Id): JsValue = JsString(id)            │
  //      │ }                                                                │
  //      └──────────────────────────────────────────────────────────────────┘
  //
  //   Note that we still need to specify the Scala type Id (as synonym for String) to make
  //   sure that Scala the compiler will pick our custom Reader from the build scope.
  //
  //   Finally note, that our custom Reader is still a better alternative to whatever the
  //   PlayFramework macros (such as Json.format) can generate inline. It is better because
  //   it will make our deserializer work even if the Bridge API service decide to send
  //   JSON number values (such as 123) ...
  //
  //   ... unless we can persuade Bridge API engineers at giving up with JSON union types
  //   in their JSON schema definitions!
  //



