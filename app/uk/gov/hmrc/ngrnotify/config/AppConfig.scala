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

package uk.gov.hmrc.ngrnotify.config

import play.api.Configuration
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.ngrnotify.model.propertyDetails.CredId
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject() (config: Configuration, servicesConfig: ServicesConfig):

  val appName: String                  = config.get[String]("appName")
  val submissionExportEnabled: Boolean = config.get[Boolean]("sendSubmission.enabled")
  val retryWindowHours: Int            = config.get[Int]("sendSubmission.retryWindowHours")
  val exportFrequency: Int             = config.get[Int]("sendSubmission.frequencySeconds")
  val exportBatchSize: Int             = config.get[Int]("sendSubmission.batchSize")
  val importScheduleHour: Int          = config.getOptional[Int]("validationImport.hourToRunAt").getOrElse(0)
  val importScheduleMinute: Int        = config.getOptional[Int]("validationImport.minuteToRunAt").getOrElse(0)

  private val hipBaseUrl                = servicesConfig.baseUrl("hip")
  private val registerRatepayerPath     = servicesConfig.getConfString("hip.registerRatepayerPath", "/job/ratepayer")
  private val getRatepayerPath          = servicesConfig.getConfString("hip.getRatepayerPath", "/job/ratepayer/")
  private val propertiesPath            = servicesConfig.getConfString("hip.propertiesPath", "/job/properties")
  private val updatePropertyChangesPath = servicesConfig.getConfString("hip.updatePropertyChangesPath", "/job/physical")
  private val propertyLinkingPath       = servicesConfig.getConfString("hip.propertyLinkingPath", "/job/property")
  val registerRatepayerUrl: URL         = url"${hipBaseUrl + registerRatepayerPath}"
  val updatePropertyChangesUrl: URL     = url"${hipBaseUrl + updatePropertyChangesPath}"
  val propertyLinkingUrl: URL           = url"${hipBaseUrl + propertyLinkingPath}"
  def getRatepayerUrl(id: CredId): URL  = url"${hipBaseUrl + getRatepayerPath + id.toString}"
  def getRatepayerStatusUrl(id: String): URL = url"${hipBaseUrl + getRatepayerPath + id}/dashboard"

  def getPropertiesUrl(
    id: CredId,
    assessmentId: String
  ): URL = {
    val url = s"$hipBaseUrl$propertiesPath?id=${id.toString}&assessmentId=$assessmentId"
    url"$url"
  }

  val hipClientId: String     = servicesConfig.getConfString("hip.clientId", "CLIENT_ID")
  val hipClientSecret: String = servicesConfig.getConfString("hip.clientSecret", "CLIENT_SECRET")
