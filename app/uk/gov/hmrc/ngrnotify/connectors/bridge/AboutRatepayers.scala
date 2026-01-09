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

import uk.gov.hmrc.ngrnotify.model.bridge.*
import uk.gov.hmrc.ngrnotify.model.bridge.ForeignIdSystem.Government_Gateway
import uk.gov.hmrc.ngrnotify.model.bridge.utils.JsonHelper.bridge.NullableValue
import uk.gov.hmrc.ngrnotify.model.propertyDetails.CredId
import uk.gov.hmrc.ngrnotify.model.ratepayer.RegisterRatepayerRequest

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/*
 * This class provides convenient methods for composing HTTP conversations between our NGR Notify service
 * and the Bridge API service. A conversation is a sequence of HTTP request/response pairs that are sent
 * back and forth to carry out a specific task.
 *
 * Typical conversations go through 3 steps:
 *
 *    1. >> NGR Notify sends a GET request to the Bridge API to retrieve a JSON template for a Bridge job
 *    2. << Bridge API returns a JSON template for a Bridge job.
 *    3. >> NGR Notify modifies such a template by replacing values into the relevant points of the JSON structure.
 *    4. >> NGR Notify sends a POST request to the Bridge API to submit the modified JSON template as a Bridge job.
 *    5. << Bridge API returns a response indicating whether the job was successfully submitted.
 *
 */
class AboutRatepayers @Inject() (implicit ec: ExecutionContext):

  def process(bridgeTemplate: JobMessage, ngrRequest: RegisterRatepayerRequest, id: String): BridgeResult[JobMessage] = Future.successful {
    try {
      //
      // During a discussion with the Bridge API team, on 2025-11-24, we understood that the "products" compartment
      // is provided as an exact replica of the "persons" compartment, ready to be "filled" with our NGR data.
      //
      val productsCompartment = bridgeTemplate.job.compartments.products

      if (productsCompartment.isEmpty) {
        // This should never happen, but we'll handle it gracefully anyway
        Left("job.compartments.products[] is empty")
      } else if (productsCompartment.size > 1) {
        // Also, this should never happen, but we'll handle it gracefully anyway
        Left("job.compartments.products[] has more than one product in it")
      } else {
        // We can safely access the first element of the productsCompartment list
        // because we checked that it's non-empty (see above)

        // TODO Following assertions wouldn't be necessary if we had programmed a better JSON serializer/deserializer
        //      See the bridge.model.Product Scala model for more details.
        val categoryCode = productsCompartment(0).category.code
        assert(categoryCode == "LTX-DOM-PSN", s"Unexpected category code: $categoryCode")
        assert(productsCompartment(0).data.isInstanceOf[PersonData])

        /*
         * NOTE
         * ------
         *   The following condition (when the identifier of the person in the template is 'null')
         *
         *      template.job.compartments.products(0).id == null
         *
         *   represents the so-called "person-not-found" scenario, opposed to the "person-found" scenario
         *   for which the identifier would be not-null.
         *
         *   It seems our Scala code doesn't need to distinguish between those 2 distinct scenarios,
         *   as it would always consider the template as a sort of pre-filled form, which needs to be
         *   amended in a few places with the actual data from the NGR request.
         *
         *   Therefore, regardless of the above scenarios. the following Scala code rightfully results
         *   with a copy of the incoming template, for which it amends the product compartment with the
         *   actual data from the NGR request.
         *
         *   Also
         *   notice that this implementation does NOT support the so-called "multiple persons" scenarios!
         *   These additional scenarios would require different portions of the template to be amended,
         *   particularly those in the "job.compartments.persons(0).items" array, for which we would need
         *   to match entities against the category, type, and class codes.
         */

        val productData = productsCompartment(0).data match {
          case personData: PersonData => personData
          case d                      => throw new RuntimeException("Unexpected data type for job.compartments.products[0].data :" + d.getClass.getName)
        }

        // TODO How about the ngrRequest.trnReferenceNumber
        // TODO How about the ngrRequest.isRegistered
        // TODO How about the ngrRequest.recoveryId
        val processed =
          bridgeTemplate.copy(
            job = bridgeTemplate.job.copy(
              name = NullableValue(Some("Register " + ngrRequest.name.map(_.value).getOrElse(""))),
              compartments = bridgeTemplate.job.compartments.copy(
                products = List(productsCompartment(0).copy(
                  name = NullableValue(ngrRequest.name.map(_.value)),
                  data = productData.copy(
                    foreignIds = List(
                      ForeignDatum(system = Some(Government_Gateway), value = Some(id)),
                      ForeignDatum(location = Some("NINO"), value = ngrRequest.nino.map(_.value)),
                      ForeignDatum(location = Some("secondary_telephone_number"), value = ngrRequest.secondaryNumber.map(_.value))
                    )
                  ).copy(
                    foreignNames = List.empty
                  ).copy(
                    foreignLabels = List(
                      ForeignDatum(location = Some("RatepayerType"), value = ngrRequest.userType.map(_.toString)),
                      ForeignDatum(location = Some("AgentStatus"), value = ngrRequest.agentStatus.map(_.toString)),
                      ForeignDatum(location = Some("RecoveryId"), value = ngrRequest.recoveryId)
                    )
                  ).copy(
                    names = Names(
                      // TODO titleCommon = ???
                      // TODO titleUncommon = ???
                      forenames = ngrRequest.forenameAndSurname._1,
                      surname = ngrRequest.forenameAndSurname._2,
                      corporateName = ngrRequest.tradingName.map(_.value)
                    )
                  ).copy(
                    communications = Communications(
                      postalAddress = ngrRequest.address.map(_.singleLine),
                      telephoneNumber = ngrRequest.contactNumber.map(_.value),
                      email = ngrRequest.email.map(_.value)
                    )
                  )
                ))
              )
            )
          )

        Right(processed)
      }
    } catch {
      case e: Throwable => Left(e.getMessage)
    }
  }
