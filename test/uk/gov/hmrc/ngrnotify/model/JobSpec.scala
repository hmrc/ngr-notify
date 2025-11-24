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

package uk.gov.hmrc.ngrnotify.model

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.*
import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.System.GovernmentGateway
import uk.gov.hmrc.ngrnotify.model.bridge.TitleCommon.*

class JobSpec extends AnyWordSpec with Matchers:

  private val incomingJob =
    BridgeRequest(
      Job(
        id = None,
        idx = "1",
        name = "Register Ratepayer",
        compartments = Compartments(
          persons = List(
            Person(
              id = None,
              idx = "1.2.1",
              name = "Government Gateway User",
              data = PersonData(
                names = Some(Names()),
                communications = Some(Communications())
              )
            )
          ),
          products = List(
            Person(
              id = None,
              idx = "1.4.1",
              name = "Government Gateway User",
              data = PersonData(
                foreignIds = List(
                  ForeignId(
                    system = Some(GovernmentGateway),
                    value = Some("GGID123345")
                  )
                ),
                names = Some(
                  Names(
                    titleCommon = Some(Mr),
                    forenames = Some("Alan"),
                    surname = Some("O Neill"),
                    postNominals = Some("BSc (Hons) Land Management"),
                    corporateName = None
                  )
                ),
                communications = Some(
                  Communications(
                    postalAddress = Some("9 Anderton Close Tavistock Devon PL19 9RA"),
                    telephoneNumber = Some("01548 830687"),
                    email = Some("alan@somewhere.com")
                  )
                )
              )
            )
          )
        )
      )
    )

  private val outgoingJob =
    BridgeRequest(
      Job(
        id = Some("one-two-three"),
        idx = "1",
        name = "Register Ratepayer",
        compartments = Compartments(
          persons = List(
            Person(
              id = None,
              idx = "1.2.1",
              name = "Government Gateway User",
              data = PersonData(
                foreignIds = List(
                  ForeignId(
                    system = Some(GovernmentGateway),
                    value = Some("GGID123456")
                  )
                ),
                names = Some(
                  Names(
                    titleCommon = Some(Mr)
                  )
                ),
                communications = Some(
                  Communications(
                    email = Some("somebody@example.com")
                  )
                )
              )
            )
          )
        )
      )
    )

  "The ngr-notify service" when {
    "receiving the incoming JSON text"    should {
      "fully deserialize it into our Scala model" in {

        // Receive the incoming text,
        // parse it into an abstract syntax tree
        // and then convert it to our model
        val incomingText  = loadText("incoming_job.json")
        val absSyntaxTree = Json.parse(incomingText)
        val actualModel   = Json.fromJson[BridgeRequest](absSyntaxTree)

        // Assert the actual model is the same as the expected one
        actualModel mustBe a[JsSuccess[BridgeRequest]]
        actualModel.get mustBe incomingJob
      }
    }
    "transmitting the outgoing JSON text" should {
      "fully serialize it from our Scala model" in {

        // Turn our model into a JSON abstract syntax tree
        // and the pretty print it to text
        val abst       = Json.toJson(outgoingJob)
        val actualText = Json.prettyPrint(abst)

        // Assert the actual text is the same as the expected one
        val expectedText = loadText("outgoing_job.json")
        actualText mustBe expectedText
      }
    }
  }

  def loadText(resource: String): String =
    scala.io.Source.fromResource(resource).getLines().mkString("\n")
