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

package uk.gov.hmrc.ngrnotify.connectors.bridge

import scala.concurrent.{ExecutionContext, Future}

implicit class FutureEither[L, R](val wrapped: Future[Either[L, R]])(using ec: ExecutionContext) {

  def map[B](f: R => B): FutureEither[L, B] = wrapped.map(_.map(f))

  def flatMap[B](f: R => FutureEither[L, B]): FutureEither[L, B] = wrapped.flatMap {
    case Left(l)  => Future(Left(l))
    case Right(r) => f(r).wrapped
  }

  def recover[B >: R](pf: PartialFunction[L, B]): FutureEither[L, B] = wrapped.map {
    case Left(e) if pf.isDefinedAt(e) => Right(pf(e))
    case other                        => other
  }

  def toFuture = wrapped
    .flatMap {
      case Left(l)  => Future.failed(new Exception(l.toString))
      case Right(r) => Future.successful(r)
    }
    .recoverWith {
      case e: Throwable => Future.failed(e)
    }
}

type BridgeFailureReason = String
type BridgeResult[T]     = FutureEither[BridgeFailureReason, T]
type NoContent           = Unit
