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

package uk.gov.hmrc.ngrnotify.model.bridge

import play.api.libs.json.*
import uk.gov.hmrc.ngrnotify.model.bridge.BridgeJobModel.{CodeMeaning, Extracting, Protodata}
import uk.gov.hmrc.ngrnotify.model.email.Email
import uk.gov.hmrc.ngrnotify.model.ratepayer.{Name, PhoneNumber, RegisterRatepayerRequest}
import uk.gov.hmrc.ngrnotify.model.{Address, Postcode, bridge}

// ---------- Case Classes ----------

case class BridgeJobModel(
  $schema: String,
  job: BridgeJobModel.Job
)

object BridgeJobModel {

  case class Job(
    id: Option[Id],
    idx: Option[String],
    name: Option[String],
    label: Option[String],
    description: Option[String],
    origination: Option[String],
    termination: Option[String],
    category: CodeMeaning,
    `type`: CodeMeaning,
    `class`: CodeMeaning,
    data: Data,
    protodata: Seq[Protodata],
    metadata: Metadata,
    compartments: Compartments,
    items: Option[Seq[JobItem]]
  )

  case class CodeMeaning(
    code: Option[String] = None,
    meaning: Option[String] = None
  )

  case class Names(
    title_common: Option[String],
    title_uncommon: Option[String],
    forenames: Option[String],
    surname: Option[String],
    post_nominals: Option[String],
    corporate_name: Option[String],
    crown_name: Option[String],
    known_as: Option[String]
  )

  case class Protodata() // empty placeholder

  case class Extracting(
                         selecting: Map[String, String] = Map.empty
                       )
  object Extracting {
    implicit val extractingFormat: OFormat[Extracting] = Json.format[Extracting]
  }

  case class TransformingSending(
                                  filtering: Map[String, String] = Map.empty,
                                  supplementing: Map[String, String] = Map.empty,
                                  recontextualising: Map[String, String] = Map.empty,
                                )
  object TransformingSending {
    implicit val transformingSendingFormat: OFormat[TransformingSending] = Json.format[TransformingSending]
  }

  case class Sending(
                      extracting: Extracting,
                      transforming: TransformingSending,
                      loading: Loading
                    )

  object Sending {
    implicit val sendingFormat: OFormat[Sending] = Json.format[Sending]
  }

  case class Receiving(
                        unloading: Unloading,
                        transforming: TransformingReceiving,
                        storing: Storing
                      )

  object Receiving {
    implicit val receivingFormat: OFormat[Receiving] = Json.format[Receiving]
  }

  case class Unloading(
                        receiving: Map[String, String] = Map.empty,
                        decrypting: Map[String, String] = Map.empty,
                        verifying: Map[String, String] = Map.empty,
                        assuring: Map[String, String] = Map.empty,
                        readying: Map[String, String] = Map.empty
                      )
  object Unloading {
    implicit val unloadingFormat: OFormat[Unloading] = Json.format[Unloading]
  }

  case class TransformingReceiving(
                                    recontextualising: Map[String, String] = Map.empty,
                                    dropping: Map[String, String] = Map.empty,
                                    restoring: Map[String, String] = Map.empty
                                  )

  object TransformingReceiving {
    implicit val transformingReceivingFormat: OFormat[TransformingReceiving] = Json.format[TransformingReceiving]
  }

  case class Storing(
                      inserting: Map[String, String] = Map.empty
                    )
  object Storing {
    implicit val storingFormat: OFormat[Storing] = Json.format[Storing]
  }




  case class Loading(
                      readying: Option[Map[String, String]] = None,
                      assuring: Option[Map[String, String]] = None,
                      signing: Option[Signing] = None,
                      encrypting: Option[Map[String, String]] = None,
                      sending: Option[Map[String, String]] = None
                    )

  object Loading {
    implicit val loadingFormat: OFormat[Loading] = Json.format[Loading]
  }

  case class MetadataStage(
    extracting: MetadataAction = MetadataAction(),
    transforming: MetadataTransform = MetadataTransform(),
    loading: Loading
  )

  object MetadataStage {
    implicit val metadataStageFormat: OFormat[MetadataStage] = Json.format[MetadataStage]
  }

  case class MetadataAction(selecting: Map[String, String] = Map.empty)

  case class MetadataTransform(
    filtering: Map[String, String] = Map.empty,
    supplementing: Map[String, String] = Map.empty,
    recontextualising: Map[String, String] = Map.empty,
    dropping: Map[String, String] = Map.empty,
    restoring: Map[String, String] = Map.empty
  )

  case class MetadataLoading(
    readying: Map[String, String] = Map.empty,
    assuring: Map[String, String] = Map.empty,
    signing: Map[String, String] = Map.empty,
    encrypting: Map[String, String] = Map.empty,
    sending: Map[String, String] = Map.empty
  )

  case class MetadataUnloading(
    receiving: Map[String, String] = Map.empty,
    decrypting: Map[String, String] = Map.empty,
    verifying: Map[String, String] = Map.empty,
    assuring: Map[String, String] = Map.empty,
    readying: Map[String, String] = Map.empty
  )
  case class MetadataStoring(inserting: Map[String, String] = Map.empty)

  case class Compartments(
    properties: Seq[JobItem] = Seq.empty,
    persons: Seq[JobItem] = Seq.empty,
    processes: Seq[JobItem] = Seq.empty,
    products: Seq[JobItem] = Seq.empty,
    relationships: Seq[RelationShipJobItem] = Seq.empty
  )

  case class JobItem(
    id: Option[Int] = None,
    idx: Option[String] = None,
    name: Option[String] = None,
    label: Option[String] = None,
    description: Option[String] = None,
    origination: Option[String] = None,
    termination: Option[String] = None,
    category: CodeMeaning = CodeMeaning(),
    `type`: CodeMeaning = CodeMeaning(),
    `class`: CodeMeaning = CodeMeaning(),
    data: Data,
    protodata: Seq[Protodata] = Seq.empty,
    metadata: Metadata,
    compartments: Compartments = Compartments(),
    items: Option[Seq[JobItem]] = None
  )

  case class RelationShipJobItem(
    id: Option[Int] = None,
    idx: Option[String] = None,
    name: Option[String] = None,
    label: Option[String] = None,
    description: Option[String] = None,
    origination: Option[String] = None,
    termination: Option[String] = None,
    category: CodeMeaning = CodeMeaning(),
    `type`: CodeMeaning = CodeMeaning(),
    `class`: CodeMeaning = CodeMeaning(),
    data: Data,
    protodata: Seq[Protodata] = Seq.empty,
    metadata: Metadata,
    compartments: Compartments = Compartments(),
    items: Option[Seq[Pointer]] = None
  )

  object RelationShipJobItem {
    implicit val relationShipJobItemFormat: OFormat[RelationShipJobItem] = Json.format[RelationShipJobItem]
  }


  // ---------- Play JSON Implicits ----------

  import play.api.libs.json.*

  implicit val protodataFormat: OFormat[Protodata]                 = Json.format[Protodata]
  implicit val codeMeaningFormat: OFormat[CodeMeaning]             = Json.format[CodeMeaning]
  implicit val namesFormat: OFormat[Names]                         = Json.format[Names]
  implicit val metadataActionFormat: OFormat[MetadataAction]       = Json.format[MetadataAction]
  implicit val metadataTransformFormat: OFormat[MetadataTransform] = Json.format[MetadataTransform]
  implicit val metadataLoadingFormat: OFormat[MetadataLoading]     = Json.format[MetadataLoading]
  implicit val metadataUnloadingFormat: OFormat[MetadataUnloading] = Json.format[MetadataUnloading]
  implicit val metadataStoringFormat: OFormat[MetadataStoring]     = Json.format[MetadataStoring]

  implicit lazy val metadataStageFormat: OFormat[MetadataStage] = Json.format[MetadataStage]
  implicit lazy val compartmentsFormat: OFormat[Compartments]   = Json.format[Compartments]

  implicit lazy val jobItemFormat: OFormat[JobItem]        = Json.format[JobItem]
  implicit lazy val jobFormat: OFormat[Job]                = Json.format[Job]
  implicit lazy val jobRootFormat: OFormat[BridgeJobModel] = Json.format[BridgeJobModel]

  def toRatepayerModel(bridgeJobModel: BridgeJobModel): RegisterRatepayerRequest = {
    val data = bridgeJobModel.job.compartments.products.head
    val communications: Option[Communications] = data.data match {
      case personData: PersonEntityData => personData.communications
      case _ => None
    }
 
    val addressString = communications.flatMap(_.postalAddress).getOrElse("")
    RegisterRatepayerRequest(
      ratepayerCredId = "",
      userType = None,
      agentStatus = None,
      name = data.name.map(Name(_)),
      tradingName = None,
      email = communications.flatMap(_.email).map(Email(_)),
      nino = None,
      contactNumber = communications.flatMap(_.telephoneNumber).map(PhoneNumber(_)),
      secondaryNumber = None,
      address = Some(Address(line1 = addressString, line2 = None, town = "", county = None, postcode = Postcode("")))
    )
  }

}
