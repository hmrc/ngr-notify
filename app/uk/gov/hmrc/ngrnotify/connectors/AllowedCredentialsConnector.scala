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

package uk.gov.hmrc.ngrnotify.connectors

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AllowedCredentialsConnector @Inject() ():

  private val allowedCredentials = List("test-cred-1", "test-cred-2", "test-cred-3", "test-cred-4")

  def isAllowed(credId: String): Future[Boolean] =
    Future.successful(allowedCredentials.contains(credId))
