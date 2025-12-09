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
import generators.Generators
import models.email.EmailRequest
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK, REQUEST_TIMEOUT}
import play.api.inject.guice.GuiceApplicationBuilder
import utils.WireMockHelper

class EmailConnectorSpec extends SpecBase with WireMockHelper with Generators with ScalaCheckPropertyChecks {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.email.port" -> server.port()
    )
    .build()

  lazy val connector: EmailConnector = app.injector.instanceOf[EmailConnector]

  "EmailConnector" - {
    "must return status as OK for valid email submission" in {

      forAll(arbitrary[EmailRequest]) { emailRequest =>
        stubPostResponse(s"/hmrc/email", OK)

        val result = connector.sendEmail(emailRequest)
        result.futureValue.status mustBe OK
      }
    }

    "must return status as BAD_REQUEST for invalid email submission" in {

      forAll(arbitrary[EmailRequest]) { emailRequest =>
        stubPostResponse(s"/hmrc/email", BAD_REQUEST)

        val result = connector.sendEmail(emailRequest)
        result.futureValue.status mustBe BAD_REQUEST
      }
    }

    "must return status as NOT_FOUND for invalid email submission" in {

      forAll(arbitrary[EmailRequest]) { emailRequest =>
        stubPostResponse(s"/hmrc/email", NOT_FOUND)

        val result = connector.sendEmail(emailRequest)
        result.futureValue.status mustBe NOT_FOUND
      }
    }

    "must return status as Internal Server Error when a fault occurs" in {

      forAll(arbitrary[EmailRequest]) { emailRequest =>
        stubFailure(s"/hmrc/email")
        val result = connector.sendEmail(emailRequest)
        result.futureValue.status mustBe INTERNAL_SERVER_ERROR

      }
    }

    "must return status as Request Timeout when a request timeout" in {

      val emailRequest = arbitraryEmailRequest.arbitrary.sample.value
      stubPostResponse(s"/hmrc/email", REQUEST_TIMEOUT)

      val result = connector.sendEmail(emailRequest)

      result.futureValue.status mustBe REQUEST_TIMEOUT
    }

  }

}
