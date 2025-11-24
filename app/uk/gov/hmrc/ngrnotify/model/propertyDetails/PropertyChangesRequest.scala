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

package uk.gov.hmrc.ngrnotify.model.propertyDetails

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class PropertyChangesRequest(
  credId: CredId,
  dateOfChange: LocalDate,
  useOfSpace: Option[ChangeToUseOfSpace] = None,
  internalFeatures: Seq[(String, String)],
  externalFeatures: Seq[(String, String)],
  additionalInfo: Option[AnythingElseData] = None,
  uploadedDocuments: Seq[String],
  declarationRef: Option[String] = None
) {
  private val useOfSpaceData: String     = useOfSpace.map(_.toString).getOrElse("No change to use of space")
  private val additionalInfoData: String = additionalInfo.map(_.toString).getOrElse("No additional information provided")

  override def toString: String = s"credId: $credId - dateOfChange: $dateOfChange - useOfSpace: $useOfSpaceData - " +
    s"internalFeatures: ${internalFeatures.map { case (k, v) => s"($k, $v)" }.mkString("[", ", ", "]")}, " +
    s"externalFeatures: ${externalFeatures.map { case (k, v) => s"($k, $v)" }.mkString("[", ", ", "]")}, " +
    s"additionalInfo: $additionalInfoData - uploadedDocuments: ${uploadedDocuments.map(x => s"$x").mkString("[", ", ", "]")}"
}

object PropertyChangesRequest {
  implicit val format: OFormat[PropertyChangesRequest] = Json.format
}
