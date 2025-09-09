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

package uk.gov.hmrc.ngrnotify.repository

import org.mongodb.scala.model.*
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.ngrnotify.model.AllowedCredentials

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllowedCredentialsRepo @Inject() (
  mongo: MongoComponent
)(implicit
  ec: ExecutionContext
) extends PlayMongoRepository[AllowedCredentials](
    collectionName = "allowed_credentials",
    mongoComponent = mongo,
    domainFormat = AllowedCredentials.format,
    indexes = Seq(
      IndexModel(
        Indexes.ascending("credId"),
        IndexOptions().name("credIdIndex").unique(true)
      )
    )
  ) {

  def isAllowed(credId: String): Future[Boolean] = {

    val filter = Filters.equal("credId", credId)

    collection
      .countDocuments(filter)
      .toFuture()
      .map(count => count > 0)
  }

}
