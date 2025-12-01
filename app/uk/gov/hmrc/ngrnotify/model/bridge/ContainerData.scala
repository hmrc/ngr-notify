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

import play.api.libs.json.{Format, Json}

case class ED(
               activity: EDProperty[String],
               code: EDProperty[String],
               description: EDProperty[String],
               quantity: EDProperty[BigDecimal],
               units: EDProperty[String]
             )

object ED:
  given Format[ED] = Json.format
  
case class EDProperty[T](
                          value: Option[T], // null or value
                          source: Option[String] // null or string (1-255 chars)
                        )

object EDProperty:
    given[T: Format]: Format[EDProperty[T]] = Json.format

case class ContainerData(
                          foreignIds: List[ForeignDatum] = List.empty,
                          foreignNames: List[ForeignDatum] = List.empty,
                          foreignLabels: List[ForeignDatum] = List.empty,
                          uses: List[ED] = List.empty,
                          constructions: List[ED]  = List.empty, 
                          facilities: List[ED] = List.empty,
                          artifacts: List[ED] = List.empty,
                          uninheritances: List[ED] = List.empty,
                          attributions: List[ED] = List.empty
                        )

object ContainerData:
  given Format[ContainerData] = Json.format


