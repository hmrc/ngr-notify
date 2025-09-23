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

package uk.gov.hmrc.ngrnotify.controllers

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.ngrnotify.connectors.AllowedCredentialsConnector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AllowedCredentialsController @Inject() (
  connector: AllowedCredentialsConnector,
  cc: ControllerComponents
)(implicit ec: ExecutionContext
) extends BackendController(cc)
  with Logging
  with JsonSupport:

  def isAllowedInPrivateBeta(credId: String): Action[AnyContent] = Action.async { implicit request =>
    connector.isAllowed(credId)
      .map { allowed =>
        logger.info(s"Private beta check for credId: $credId - allowed: $allowed")
        Ok(Json.obj("allowed" -> allowed))
      }
      .recover {
        case e: Exception =>
          logger.error(s"Failed to check private beta access for credId: $credId", e)
          InternalServerError
      }
  }
