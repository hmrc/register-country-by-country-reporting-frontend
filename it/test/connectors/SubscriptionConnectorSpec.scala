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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalTo, post, urlEqualTo}
import generators.Generators
import models.subscription.request.CreateSubscriptionForCBCRequest
import models.{SafeId, SubscriptionID}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{OK, REQUEST_TIMEOUT}
import play.api.inject.guice.GuiceApplicationBuilder
import utils.WireMockHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubscriptionConnectorSpec extends SpecBase with WireMockHelper with ScalaCheckPropertyChecks with Generators {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.register-country-by-country.port" -> server.port()
    )
    .build()

  lazy val connector: SubscriptionConnector = app.injector.instanceOf[SubscriptionConnector]
  private val subscriptionUrl               = "/register-country-by-country-reporting/subscription"
  private val errorCodes: Gen[Int]          = Gen.oneOf(Seq(400, 404, 403, 500, 501, 502, 503, 504))
  private val safeId                        = SafeId("safeId")
  private val businessName                  = "Some Business Name"

  "SubscriptionConnector" - {
    "readSubscription" - {
      "must return SubscriptionID for valid input request" in {
        val expectedResponse = SubscriptionID("subscriptionID")

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

        stubPostResponse(s"$subscriptionUrl/read-subscription/${safeId.value}", OK, subscriptionResponse)

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

        stubPostResponse(s"$subscriptionUrl/read-subscription/${safeId.value}", OK, subscriptionResponse)

        val result = connector.readSubscription(safeId)
        result.futureValue mustBe None
      }

      "must return None when read subscription fails" in {
        val errorCode = errorCodes.sample.value

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

        stubPostResponse(s"$subscriptionUrl/read-subscription/${safeId.value}", errorCode, subscriptionErrorResponse)

        val result = connector.readSubscription(safeId)
        result.futureValue mustBe None
      }

      "must return None when read subscription fails with request timeout" in {

        stubPostResponse(s"$subscriptionUrl/read-subscription/${safeId.value}", REQUEST_TIMEOUT)

        val result = connector.readSubscription(safeId)
        result.futureValue mustBe None
      }
    }

    "createSubscription" - {
      val createSubscriptionRequest = Arbitrary.arbitrary[CreateSubscriptionForCBCRequest].sample.value

      "must return SubscriptionID for valid input request" in {
        val expectedResponse = SubscriptionID("XACBC0000123456")

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

        stubPostResponse(s"$subscriptionUrl/create-subscription", OK, subscriptionResponse)

        val result: Future[Option[SubscriptionID]] = connector.createSubscription(createSubscriptionRequest, businessName)
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

        stubPostResponse(s"$subscriptionUrl/create-subscription", OK, subscriptionResponse)

        val result = connector.createSubscription(createSubscriptionRequest, businessName)
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

        stubPostResponse(s"$subscriptionUrl/create-subscription", errorCode, subscriptionErrorResponse)

        val result = connector.createSubscription(createSubscriptionRequest, businessName)
        result.futureValue mustBe None
      }

      "must return None when create subscription fails with Request Timeout" in {

        stubPostResponse(s"$subscriptionUrl/create-subscription", REQUEST_TIMEOUT)

        val result = connector.createSubscription(createSubscriptionRequest, businessName)
        result.futureValue mustBe None
      }

      "must include X-Business-Name header in the request" in {
        val expectedResponse = SubscriptionID("XACBC0000123456")

        val subscriptionResponse: String =
          s"""
             |{
             | "createSubscriptionForCBCResponse": {
             |   "responseCommon": {
             |     "status": "OK",
             |     "processingDate": "1000-01-01T00:00:00Z"
             |   },
             |   "responseDetail": {
             |     "subscriptionID": "XACBC0000123456"
             |   }
             | }
             |}""".stripMargin

        server.stubFor(
          post(urlEqualTo(s"$subscriptionUrl/create-subscription"))
            .withHeader("X-Business-Name", equalTo(businessName))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withBody(subscriptionResponse)
            )
        )

        val result = connector.createSubscription(createSubscriptionRequest, businessName)
        result.futureValue.value mustBe expectedResponse
      }
    }
  }

}
