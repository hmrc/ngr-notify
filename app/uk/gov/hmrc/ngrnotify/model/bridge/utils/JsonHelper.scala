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

package uk.gov.hmrc.ngrnotify.model.bridge.utils

object JsonHelper {

  object bridge {
    case class NullableValue[T](value: Option[T])

    import play.api.libs.json._

    object NullableValue {
      implicit def format[T: Format]: Format[NullableValue[T]] = Format(
        {
          case JsNull => JsSuccess(NullableValue(None))
          case json => implicitly[Format[T]].reads(json).map(v => NullableValue(Some(v)))
        },
        nv => nv.value.map(implicitly[Format[T]].writes).getOrElse(JsNull)
      )
    }
  }
}
