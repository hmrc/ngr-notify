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

package uk.gov.hmrc.ngrnotify.controllers.actions

import com.google.inject.Inject
import play.api.Logging
import play.api.mvc.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrnotify.model.propertyDetails.CredId
import uk.gov.hmrc.ngrnotify.model.request.IdentifierRequest
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext
) extends IdentifierAction
  with AuthorisedFunctions with Logging {

  private type RetrievalsType = Option[Credentials]

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    val retrievals: Retrieval[RetrievalsType] = Retrievals.credentials

    authorised(ConfidenceLevel.L250).retrieve(retrievals) {
      case Some(credentials) =>
        block(IdentifierRequest(request = request, credId = CredId(credentials.providerId)))

      case None =>
        logger.warn("Credentials are missing for the authenticated user")
        Future.successful(Results.Unauthorized("Credentials are missing"))

    } recover {
      case ex: Throwable =>
        logger.warn(s"Authorization failed: ${ex.getMessage}", ex)
        Results.Unauthorized(s"Authorization failed: ${ex.getMessage}")
    }
  }

}
