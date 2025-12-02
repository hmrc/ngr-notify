import play.core.PlayVersion
import sbt.*

object AppDependencies {

  private val bootstrapVersion    = "10.4.0"
  private val hmrcMongoVersion    = "2.10.0"
  private val playLanguageVersion = "9.5.0"
  private val playDomainVersion   = "13.0.0"
  private val swaggerVersion      = "5.30.2"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"        % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-language-play-30"     % playLanguageVersion,
    "uk.gov.hmrc"       %% "domain-play-30"            % playDomainVersion,
    "org.webjars"        % "swagger-ui"                % swaggerVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapVersion         % Test,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % hmrcMongoVersion         % Test,
    "org.apache.pekko"  %% "pekko-testkit"           % PlayVersion.pekkoVersion % Test
  )

  val it: Seq[ModuleID] = Seq.empty

}
