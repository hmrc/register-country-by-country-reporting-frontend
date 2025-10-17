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

package utils

import generators.Generators
import models.UserAnswers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.{Application, Logging}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.time.{Clock, Instant, ZoneId}

trait ISpecBase
    extends GuiceOneServerPerSuite
    with DefaultPlayMongoRepositorySupport[UserAnswers]
    with ScalaFutures
    with WireMockHelper
    with Generators
    with Logging {

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(scaled(Span(20, Seconds)))
  implicit val fixedClock: Clock                       = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  lazy val repository: SessionRepository               = app.injector.instanceOf[SessionRepository]

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(config)
    .build()
  implicit val hc: HeaderCarrier = HeaderCarrier()

  def emptyUserAnswers: UserAnswers = UserAnswers("testid", Json.obj(), Instant.now(fixedClock))

  def config: Map[String, Any] = Map(
//    "logger.root"                                            -> "INFO",
//    "logger.controllers"                                     -> "DEBUG",
    "play.filters.csrf.header.bypassHeaders.Csrf-Token"      -> "nocheck",
    "microservice.services.auth.port"                        -> wireMockPort.toString,
    "microservice.services.register-country-by-country.port" -> wireMockPort.toString,
    "microservice.services.enrolment-store-proxy.port"       -> wireMockPort.toString,
    "microservice.services.tax-enrolments.port"              -> wireMockPort.toString,
    "mongodb.uri"                                            -> mongoUri
  )

  def buildClient(path: Option[String] = None): WSRequest = {
    val url = path match {
      case Some(value) => s"http://localhost:$port/register-to-send-a-country-by-country-report$value"
      case None        => s"http://localhost:$port/register-to-send-a-country-by-country-report"
    }
    app.injector.instanceOf[WSClient].url(url)
  }

  def buildFakeRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", s"http://localhost:$port/register-to-send-a-country-by-country-report").withSession("authToken" -> "my-token")

}
