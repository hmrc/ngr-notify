/*
 * Copyright 2026 HM Revenue & Customs
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

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.{Government_Gateway, NDRRPublicInterface}
import uk.gov.hmrc.ngrnotify.model.bridge.{ForeignDatum, JobMessage}

import scala.concurrent.ExecutionContext.Implicits.global
class ReviewChangesUserAnswersSpec extends AnyFreeSpec with JobMessageTestData with ScalaFutures{
  "ReviewChangesUserAnswers" - {
    "should serialize and deserialize correctly" in {
      val reviewChanges = ReviewChangesUserAnswers(declarationRef = "DECL12345")

      val json = ReviewChangesUserAnswers.format.writes(reviewChanges)
      assert((json \ "declarationRef").as[String] == "DECL12345")

      val deserialized = ReviewChangesUserAnswers.format.reads(json).get
      assert(deserialized == reviewChanges)
    }
    
    "should process correctly with bridge template" in {


      val bridgeTemplate: JobMessage = sampleJobMessage()
      val reviewChanges = ReviewChangesUserAnswers(declarationRef = "DECL12345")

      val result: JobMessage = ReviewChangesUserAnswers.process(bridgeTemplate, reviewChanges).toFuture.futureValue

      val foreignIds = result.job.data.foreignIds
      foreignIds mustBe List(
        ForeignDatum(
          system = Some(Government_Gateway),
          location = Some("location"),
          value = Some("SomeId")
        ),
        ForeignDatum(
          system = Some(NDRRPublicInterface),
          location = None,
          value = Some("DECL12345")
        )
      )
    }
  }
}
