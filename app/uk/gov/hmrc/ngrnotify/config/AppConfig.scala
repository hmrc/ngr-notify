package uk.gov.hmrc.ngrnotify.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class AppConfig @Inject()(config: Configuration):

  val appName: String = config.get[String]("appName")
