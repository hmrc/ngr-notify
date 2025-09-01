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

package uk.gov.hmrc.ngrnotify.services

import play.api.mvc.Headers
import uk.gov.hmrc.http.HeaderCarrier
import play.mvc.Http.HeaderNames
import uk.gov.hmrc.ngrnotify.utils.AuthHeaderBuilder

object HipService {

  def buildHipHeaderCarrier(
    requestHeaders: Headers,
    additionalHeader: Option[(String, String)] = None
  ): HeaderCarrier = {
    val headers: Seq[(String, String)] = Seq(
      HeaderNames.AUTHORIZATION -> buildAuthHeader(requestHeaders),
      HeaderNames.ACCEPT        -> "application/json",
      HeaderNames.CONTENT_TYPE  -> "application/json",
      "OriginatorId"            -> "NGR"
    ) ++ additionalHeader.toSeq

    HeaderCarrier().withExtraHeaders(headers*)
    
    //TEST PR
  }

  def buildAuthHeader(requestHeaders: Headers): String =
    AuthHeaderBuilder.buildAuthHeader(extractClientId(requestHeaders), extractClientSecret(requestHeaders))

  def extractClientId(requestHeaders: Headers): String = requestHeaders
    .get("Client-Id")
    .getOrElse(throw new RuntimeException("extractClientId ERROR: Client Id missing from headers"))

  def extractClientSecret(requestHeaders: Headers): String = requestHeaders
    .get("Client-Secret")
    .getOrElse(throw new RuntimeException("extractClientSecret ERROR: Client Secret missing from headers"))
}
