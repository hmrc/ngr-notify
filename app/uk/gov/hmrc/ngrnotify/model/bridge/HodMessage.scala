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

import play.api.libs.json.{Format, JsValue, Json, Reads}

/*

    This has been written  to deserialize with JSON response messages like the following:

        {
          "origin": "HoD",
          "response": {
            "origin": "HIP",
            "response": {
              "failures": [
                {
                  "type": "Id",
                  "reason": "Invalid format for Id – provided identifier does not match the expected pattern."
                }
              ]
            }
          }
        }

    It is not clear if Hod and HIP are the same thing or they are different intermediate systems.
    Neither is clear if those systems are present in both development, testing or production arrangements.

 */
case class HipResponse(failures: List[BridgeFailure])
object HipResponse:
  given Format[HipResponse] = Json.format

case class HodResponse(origin: String, response: HipResponse)
object HodResponse:
  given Reads[HodResponse] = Reads { json =>
    for
      origin <- (json \ "origin").validate[String]
      responseJson <- (json \ "response").validate[JsValue]
      hipResponse <- responseJson.validate[HipResponse]
        .orElse(
          responseJson.validate[HodResponse].map(hr => hr.response)
        )
    yield HodResponse(origin, hipResponse)
  }

case class HodMessage(origin: String, response: HodResponse)
object HodMessage:
  given Reads[HodMessage] = Reads { json =>
    for
      origin <- (json \ "origin").validate[String]
      responseJson <- (json \ "response").validate[JsValue]
      hodResponse <- responseJson.validate[HodResponse]
    yield HodMessage(origin, hodResponse)
  }