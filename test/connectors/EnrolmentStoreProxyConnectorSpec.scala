/*
 * Copyright 2022 HM Revenue & Customs
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

package connectors

import base.{SpecBase, WireMockServerHandler}
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import generators.Generators
import models.SubscriptionID
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{NOT_FOUND, NO_CONTENT, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.ExecutionContext.Implicits.global

class EnrolmentStoreProxyConnectorSpec extends SpecBase with WireMockServerHandler with Generators with ScalaCheckPropertyChecks {

  lazy override val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.enrolment-store-proxy.port" -> server.port()
    )
    .build()

  lazy val connector: EnrolmentStoreProxyConnector = app.injector.instanceOf[EnrolmentStoreProxyConnector]
  val enrolmentStoreProxyUrl                       = "/enrolment-store-proxy/enrolment-store/enrolments"
  val enrolmentStoreProxyMDR200Url                 = "/enrolment-store-proxy/enrolment-store/enrolments/HMRC-CBC-ORG~CBCID~xxx200/groups"
  val enrolmentStoreProxyMDR204Url                 = "/enrolment-store-proxy/enrolment-store/enrolments/HMRC-CBC-ORG~CBCID~xxx204/groups"

  val enrolmentStoreProxyResponseJson: String =
    """{
      |  "principalGroupIds": [
      |    "ABCEDEFGI1234567",
      |    "ABCEDEFGI1234568"
      |  ],
      |  "delegatedGroupIds": [
      |    "ABCEDEFGI1234567",
      |    "ABCEDEFGI1234568"
      |  ]
      |}""".stripMargin

  val enrolmentStoreProxyResponseNoPrincipalIdJson: String =
    """{
      |  "principalGroupIds": []
      |}""".stripMargin

  "EnrolmentStoreProxyConnector" - {
    "when calling enrolmentStatus" - {

      "return 200 and a enrolmentStatus response when already enrolment exists" in {
        val subscriptionID = SubscriptionID("xxx200")
        stubResponse(enrolmentStoreProxyMDR200Url, OK, enrolmentStoreProxyResponseJson)
        val result = connector.enrolmentExists(subscriptionID)
        result.futureValue mustBe true
      }

      "return 204 and a enrolmentStatus response when no enrolment exists" in {
        val subscriptionID = SubscriptionID("xxx204")
        stubResponse(enrolmentStoreProxyMDR204Url, NO_CONTENT, "")

        val result = connector.enrolmentExists(subscriptionID)
        result.futureValue mustBe false
      }

      "return 204 enrolmentStatus response when principalGroupId is empty seq" in {
        val subscriptionID = SubscriptionID("xxx204")
        stubResponse(enrolmentStoreProxyMDR204Url, OK, enrolmentStoreProxyResponseNoPrincipalIdJson)
        val result = connector.enrolmentExists(subscriptionID)
        result.futureValue mustBe false
      }

      "return 404 and a enrolmentStatus response when invalid or malfromed URL" in {
        val subscriptionID = SubscriptionID("xxx404")
        stubResponse(enrolmentStoreProxyMDR204Url, NOT_FOUND, "")
        intercept[IllegalStateException](await(connector.enrolmentExists(subscriptionID)))

      }

    }

  }

  private def stubResponse(expectedEndpoint: String, expectedStatus: Int, expectedBody: String): StubMapping =
    server.stubFor(
      get(urlEqualTo(expectedEndpoint))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
            .withBody(expectedBody)
        )
    )

}