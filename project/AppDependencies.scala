import play.core.PlayVersion
import sbt.*

object AppDependencies {

  private val bootstrapVersion    = "10.1.0"
  private val hmrcMongoVersion    = "2.7.0"
  private val playLanguageVersion = "9.1.0"
  private val swaggerVersion      = "5.28.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"        % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-language-play-30"     % playLanguageVersion,
    "org.webjars"        % "swagger-ui"                % swaggerVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapVersion         % Test,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % hmrcMongoVersion         % Test,
    "org.apache.pekko"  %% "pekko-testkit"           % PlayVersion.pekkoVersion % Test
  )

  val it: Seq[ModuleID] = Seq.empty

}
