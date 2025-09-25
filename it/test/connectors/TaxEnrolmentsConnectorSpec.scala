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
import models.SubscriptionInfo
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NO_CONTENT}
import play.api.inject.guice.GuiceApplicationBuilder
import utils.WireMockHelper

import scala.concurrent.ExecutionContext.Implicits.global

class TaxEnrolmentsConnectorSpec extends SpecBase with WireMockHelper with Generators with ScalaCheckPropertyChecks {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.tax-enrolments.port" -> server.port()
    )
    .build()

  lazy val connector: TaxEnrolmentsConnector = app.injector.instanceOf[TaxEnrolmentsConnector]

  "TaxEnrolmentsConnector" - {

    "createEnrolment" - {

      "must return status as 204 for successful Tax Enrolment call" in {
        forAll(validSafeID, validSubscriptionID, validUtr) {
          (safeID, subID, utr) =>
            val enrolmentInfo = SubscriptionInfo(safeID = safeID, utr = Some(utr), cbcId = subID)

            stubPutResponse("/tax-enrolments/service/HMRC-CBC-ORG/enrolment", NO_CONTENT)

            val result = connector.createEnrolment(enrolmentInfo)
            result.futureValue mustBe Some(NO_CONTENT)
        }
      }

      "must return status as 400 and BadRequest error" in {
        forAll(validSafeID, validSubscriptionID, validUtr) {
          (safeID, subID, utr) =>
            val enrolmentInfo = SubscriptionInfo(safeID = safeID, utr = Some(utr), cbcId = subID)
            stubPutResponse("/tax-enrolments/service/HMRC-CBC-ORG/enrolment", BAD_REQUEST)
            val result = connector.createEnrolment(enrolmentInfo)
            result.futureValue mustBe None
        }
      }

      "must return status ServiceUnavailable Error" in {
        forAll(validSafeID, validSubscriptionID, validUtr) {
          (safeID, subID, utr) =>
            val enrolmentInfo = SubscriptionInfo(safeID = safeID, utr = Some(utr), cbcId = subID)
            stubPutResponse("/tax-enrolments/service/HMRC-CBC-ORG/enrolment", INTERNAL_SERVER_ERROR)
            val result = connector.createEnrolment(enrolmentInfo)
            result.futureValue mustBe None
        }
      }
    }

    "createEnrolmentRequest" - {

      "must return correct EnrolmentRequest when saUtr provided as verifier" in {

        forAll(validSafeID, validSubscriptionID, validUtr) {
          (safeID, subID, utr) =>
            val enrolmentInfo = SubscriptionInfo(safeID = safeID, utr = Some(utr), cbcId = subID)

            val expectedVerifiers = Seq()

            enrolmentInfo.convertToEnrolmentRequest.verifiers mustBe expectedVerifiers
        }
      }

      "must return correct EnrolmentRequest when ctUtr provided as verifier" in {

        forAll(validSafeID, validSubscriptionID, validUtr) {
          (safeID, subID, utr) =>
            val enrolmentInfo = SubscriptionInfo(safeID = safeID, utr = Some(utr), cbcId = subID)

            val expectedVerifiers = Seq()

            enrolmentInfo.convertToEnrolmentRequest.verifiers mustBe expectedVerifiers
        }
      }
    }
  }
}
