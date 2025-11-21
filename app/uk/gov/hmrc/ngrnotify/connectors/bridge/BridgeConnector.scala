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

import play.api.mvc.Request
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrnotify.config.AppConfig
import uk.gov.hmrc.ngrnotify.model.ratepayer.{RegisterRatepayerRequest, RegisterRatepayerResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class BridgeConnector @Inject()(
  appConfig: AppConfig,
  httpClient: HttpClientV2,
  val aboutRatepayers: AboutRatepayers
  // ... inject more conversation utilities here if needed ...

)(using ec: ExecutionContext):

  /*
   *  DESIGN NOTES
   *  ------------
   *
   *      1. Make this connector expose methods which take the incoming NgrNotifyMessage request
   *         as input and then return the outgoing NgrNotifyMessage response as output (conveniently
   *         wrapped in BridgeResult monadic type). This helps to keep the NGR controller layer
   *         decoupled from the complexity of the Bridge API design.
   *
   *      2. Encapsulate the conversions to/from the BridgeMessage requests/responses inside 
   *         this connector and do not leak them out to the controller layer.
   *
   *      3. Get inspired by the existing registerRatepayer() method below, which shows how to 
   *         use the "conversation pattern" to convert between NgrNotifyMessage and BridgeMessage
   *         requests/responses.
   *
   *               A typical conversation sequence (which, thanks to the new BridgeResult
   *               type, you can implement using Scala for-comprehension) would be:
   *
   *                 - GET the job template
   *                   (for the task at hand)
   *
   *                 - process the job template
   *                   (by replacing values in the relevant parts of it)
   *
   *                 - POST the job
   *                   (represented by the processed template)
   *
   *              The monadic nature of BridgeResult makes it easy to chain these steps
   *              together and it yields the wrapped NgrNotifyMessage (or it fails fast).
   *
   *      4. Do not get distracted by the implicit use of PlayFramework HTTP Request
   *         as that's needed only for the handling of the CorrelationId header
   */

  // TODO It is still unclear why the CorrelationId header serves any useful purpose for "conversating with the BridgeAPI"

  def registerRatepayer(ngrRequest: RegisterRatepayerRequest)(using request: Request[?]): BridgeResult[RegisterRatepayerResponse] = {
    for {
      template    <- aboutRatepayers.getJobTemplate(ngrRequest.ratepayerCredId)
      processed    = aboutRatepayers.processJobTemplate(template, ngrRequest)
      ngrResponse <- aboutRatepayers.postJob(processed)
    } yield ngrResponse
  }

  // TODO add more methods here as needed, but write them following the above DESIGN NOTES please
