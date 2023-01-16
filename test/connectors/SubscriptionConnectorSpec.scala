/*
 * Copyright 2023 HM Revenue & Customs
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
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import generators.Generators
import models.subscription.request.CreateSubscriptionForCBCRequest
import models.{SafeId, SubscriptionID}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubscriptionConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks with Generators {

  lazy override val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.register-country-by-country.port" -> server.port()
    )
    .build()

  lazy val connector: SubscriptionConnector = app.injector.instanceOf[SubscriptionConnector]
  private val subscriptionUrl               = "/register-country-by-country-reporting/subscription"
  private val errorCodes: Gen[Int]          = Gen.oneOf(Seq(400, 404, 403, 500, 501, 502, 503, 504))
  private val safeId = SafeId("safeId")

  "SubscriptionConnector" - {
    "readSubscription" - {
      "must return SubscriptionID for valid input request" in {
        val expectedResponse  = SubscriptionID("subscriptionID")

        val subscriptionResponse: String =
          s"""
             |{
             | "displaySubscriptionForCBCResponse": {
             |   "responseCommon": {
             |     "status": "OK",
             |     "processingDate": "2020-09-23T16:12:11Z"
             |   },
             |   "responseDetail": {
             |      "subscriptionID": "subscriptionID"
             |   }
             | }
             |}""".stripMargin

        stubPostResponse(s"/read-subscription/${safeId.value}", OK, subscriptionResponse)

        val result: Future[Option[SubscriptionID]] = connector.readSubscription(safeId)
        result.futureValue.value mustBe expectedResponse
      }

      "must return None for invalid json response" in {
        val subscriptionResponse: String =
          s"""
             |{
             | "displaySubscriptionForCBCResponse": {
             |   "responseCommon": {
             |     "status": "OK",
             |     "processingDate": "2020-09-23T16:12:11Z"
             |   },
             |   "responseDetail": {}
             | }
             |}""".stripMargin

        stubPostResponse(s"/read-subscription/${safeId.value}", OK, subscriptionResponse)

        val result = connector.readSubscription(safeId)
        result.futureValue mustBe None
      }

      "must return None when read subscription fails" in {
        val errorCode     = errorCodes.sample.value

        val subscriptionErrorResponse: String =
          s"""
             | "errorDetail": {
             |    "timestamp": "2016-08-16T18:15:41Z",
             |    "correlationId": "f058ebd6-02f7-4d3f-942e-904344e8cde5",
             |    "errorCode": "$errorCode",
             |    "errorMessage": "Internal error",
             |    "source": "Internal error"
             |  }
             |""".stripMargin

        stubPostResponse(s"/read-subscription/${safeId.value}", errorCode, subscriptionErrorResponse)

        val result = connector.readSubscription(safeId)
        result.futureValue mustBe None
      }
    }

    "createSubscription" - {
      val createSubscriptionRequest = Arbitrary.arbitrary[CreateSubscriptionForCBCRequest].sample.value

      "must return SubscriptionID for valid input request" in {
        val expectedResponse  = SubscriptionID("XACBC0000123456")

        val subscriptionResponse: String =
          s"""
             |{
             | "createSubscriptionForCBCResponse": {
             |"responseCommon": {
             |"status": "OK",
             |"processingDate": "1000-01-01T00:00:00Z"
             |  },
             |  "responseDetail": {
             |   "subscriptionID": "XACBC0000123456"
             |  }
             |} }""".stripMargin

        stubPostResponse(s"/create-subscription", OK, subscriptionResponse)

        val result: Future[Option[SubscriptionID]] = connector.createSubscription(createSubscriptionRequest)
        result.futureValue.value mustBe expectedResponse
      }

      "must return None for invalid json response" in {
        val subscriptionResponse: String =
          s"""
             |{
             | "createSubscriptionForCBCResponse": {
             |"responseCommon": {
             |"status": "OK",
             |"processingDate": "1000-01-01T00:00:00Z"
             |  },
             |  "responseDetail": {
             |  }
             |} }""".stripMargin

        stubPostResponse(s"/create-subscription", OK, subscriptionResponse)

        val result = connector.createSubscription(createSubscriptionRequest)
        result.futureValue mustBe None
      }

      "must return None when create subscription fails" in {
        val errorCode: Int = errorCodes.sample.value

        val subscriptionErrorResponse: String =
          s"""
             | "errorDetail": {
             |    "timestamp": "2016-08-16T18:15:41Z",
             |    "correlationId": "f058ebd6-02f7-4d3f-942e-904344e8cde5",
             |    "errorCode": "$errorCode",
             |    "errorMessage": "Internal error",
             |    "source": "Internal error"
             |  }
             |""".stripMargin

        stubPostResponse(s"/create-subscription", errorCode, subscriptionErrorResponse)

        val result = connector.createSubscription(createSubscriptionRequest)
        result.futureValue mustBe None
      }
    }
  }

  private def stubPostResponse(expectedEndpoint: String, expectedStatus: Int, expectedBody: String): StubMapping =
    server.stubFor(
      post(urlEqualTo(s"$subscriptionUrl$expectedEndpoint"))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
            .withBody(expectedBody)
        )
    )

}
