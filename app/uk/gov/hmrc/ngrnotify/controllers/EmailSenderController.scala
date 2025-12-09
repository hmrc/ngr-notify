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
import play.api.libs.json.*
import play.api.mvc.{Action, ControllerComponents, Request, Result}
import uk.gov.hmrc.ngrnotify.controllers.actions.IdentifierAction
import uk.gov.hmrc.ngrnotify.model.EmailTemplate
import uk.gov.hmrc.ngrnotify.model.EmailTemplate.*
import uk.gov.hmrc.ngrnotify.model.ErrorCode.*
import uk.gov.hmrc.ngrnotify.model.db.EmailNotification
import uk.gov.hmrc.ngrnotify.model.email.*
import uk.gov.hmrc.ngrnotify.model.request.SendEmailRequest
import uk.gov.hmrc.ngrnotify.model.response.ApiSuccess
import uk.gov.hmrc.ngrnotify.repository.EmailNotificationRepo
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * @author Yuriy Tumakha
  */
@Singleton
class EmailSenderController @Inject() (
  emailNotificationRepo: EmailNotificationRepo,
  cc: ControllerComponents,
  identifierAction: IdentifierAction
)(using
  ec: ExecutionContext
) extends BackendController(cc)
  with JsonSupport
  with Logging:

  def sendEmail(emailTemplateId: String): Action[JsValue] = identifierAction.async(parse.json) { implicit request =>
    Try(EmailTemplate.valueOf(emailTemplateId)).toEither.fold(
      error => Left(BadRequest(buildFailureResponse(EMAIL_TEMPLATE_NOT_FOUND, error.getMessage))),
      emailTemplate =>
        (
          emailTemplate match {
            case `ngr_registration_successful`   => parseAndValidateTemplateParams[RegistrationSuccessful]
            case `ngr_add_property_request_sent` => parseAndValidateTemplateParams[AddPropertyRequestSent]
          }
        ).map { req =>
          val client = request.headers.get(USER_AGENT)
          EmailNotification(emailTemplate, req.trackerId, req.sendToEmails, req.templateParams, req.callbackUrl, client)
        }
    ) match {
      case Right(notification) => saveEmailNotification(notification)
      case Left(result)        => Future.successful(result)
    }
  }

  private def parseAndValidateTemplateParams[T](
    using
    request: Request[JsValue],
    rds: Reads[T]
  ): Either[Result, SendEmailRequest] =
    (
      request.body.validate[SendEmailRequest] match {
        case JsSuccess(sendEmailRequest, _) => Right(sendEmailRequest)
        case jsError: JsError               => Left(buildValidationErrorsResponse(jsError))
      }
    ).flatMap(sendEmailRequest =>
      sendEmailRequest.templateParams.validate[T] match {
        case JsSuccess(_, _)  => Right(sendEmailRequest)
        case jsError: JsError => Left(buildValidationErrorsResponse(jsError))
      }
    )

  private def saveEmailNotification(emailNotification: EmailNotification): Future[Result] =
    logger.info(s"\nSend ${emailNotification.emailTemplateId}. TrackerId: ${emailNotification.trackerId}")

    emailNotificationRepo
      .save(emailNotification)
      .map { id =>
        logger.info(s"\nSaved email notification with ID = $id. TrackerId: ${emailNotification.trackerId}")
        Created(Json.toJsObject(ApiSuccess("Success", "Email dispatch task successfully created.")))
      }
      .recover { error =>
        logger.error(s"Error on save to Mongo. TrackerId: ${emailNotification.trackerId}", error)
        InternalServerError(buildFailureResponse(MONGO_DB_ERROR, error.getMessage))
      }
