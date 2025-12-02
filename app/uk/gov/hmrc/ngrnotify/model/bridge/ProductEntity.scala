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

import uk.gov.hmrc.ngrnotify.model.bridge.Bridge.{ProductData, ProductItem}
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue

// #/$defs/ENTITIES/PRODUCTS/VALIDATION
case class ProductEntity(
  id: NullableValue[Id],
  idx: String,
  name: NullableValue[String],
  label: String,
  description: NullableValue[String],
  origination: NullableValue[String],
  termination: NullableValue[String],
  category: CodeMeaning,
  `type`: CodeMeaning,
  `class`: CodeMeaning,
  data: ProductData,
  protodata: List[Protodata],
  metadata: Metadata,
  compartments: Compartments,
  // TODO Review the definition of ProductItem as it might be missing relationship entities
  items: List[ProductItem]
) extends Entity[ProductData, Compartments, List[ProductItem]]
  with StandardProperties

object ProductEntity:
  import Bridge.given
  import play.api.libs.json.*
  given Format[ProductEntity] = Json.format
