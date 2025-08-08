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

import play.api.mvc.{Action, AnyContent, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.libs.json.JsValue
import uk.gov.hmrc.ngrnotify.connectors.HipConnector

@Singleton()
class HipController @Inject() (hipConnector: HipConnector, cc: ControllerComponents)(implicit executionContext: ExecutionContext) extends BackendController(cc) {

  def hipHelloWorld(): Action[AnyContent] = Action.async { implicit request =>
    val eventuallyHelloWorldResponse: Future[JsValue] = hipConnector.callHelloWorld(request.headers)
    
    eventuallyHelloWorldResponse.map(helloWorldResponse => Ok(s"Response was: $helloWorldResponse"))
  }

  def hipPersonDetails(): Action[AnyContent] = Action.async { implicit request =>
    val eventuallyPersonDetailsResponse: Future[JsValue] = hipConnector.callPersonDetails(request.headers)

    eventuallyPersonDetailsResponse.map(personDetailsResponse => Ok(s"Response was: $personDetailsResponse"))
  }

  def hipItems(): Action[AnyContent] = Action.async { implicit request =>
    val eventuallyItemsResponse: Future[JsValue] = hipConnector.callItems(request.headers)

    eventuallyItemsResponse.map(itemsResponse => Ok(s"Response was: $itemsResponse"))
  }
}

