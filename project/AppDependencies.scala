import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val mongoVersion = "0.74.0"
  val bootstrapVersion = "7.12.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-28"    % "8.5.0",
    "uk.gov.hmrc"       %% "crypto-json-play-28"           % "7.6.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.12.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"    % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"            % mongoVersion
  )

  val test = Seq(
    "org.scalatest"          %% "scalatest"               % "3.2.10",
    "org.scalatestplus"      %% "scalacheck-1-15"         % "3.2.10.0",
    "org.scalatestplus"      %% "mockito-3-4"             % "3.2.10.0",
    "org.scalatestplus.play" %% "scalatestplus-play"      % "5.1.0",
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.64.0",
    "org.pegdown"             % "pegdown"                 % "1.6.0",
    "org.jsoup"               % "jsoup"                   % "1.14.3",
    "com.typesafe.play"      %% "play-test"               % PlayVersion.current,
    "org.mockito"            %% "mockito-scala"           % "1.16.42",
    "wolfendale"             %% "scalacheck-gen-regexp"   % "0.1.2",
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % bootstrapVersion,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % mongoVersion,
    "com.github.tomakehurst"  % "wiremock-jre8"           % "2.26.0"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
