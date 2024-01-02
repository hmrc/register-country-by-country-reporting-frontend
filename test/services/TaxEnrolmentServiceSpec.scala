/*
 * Copyright 2024 HM Revenue & Customs
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

package services

import base.SpecBase
import connectors.{EnrolmentStoreProxyConnector, TaxEnrolmentsConnector}
import models.{Address, Country, EnrolmentCreationError, EnrolmentExistsError, SafeId, SubscriptionID, UserAnswers}
import org.mockito.ArgumentMatchers.any
import pages._
import play.api.Application
import play.api.http.Status.NO_CONTENT
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaxEnrolmentServiceSpec extends SpecBase {

  val mockTaxEnrolmentsConnector       = mock[TaxEnrolmentsConnector]
  val mockEnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]

  val service: TaxEnrolmentService = app.injector.instanceOf[TaxEnrolmentService]

  override lazy val app: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[TaxEnrolmentsConnector].toInstance(mockTaxEnrolmentsConnector)
    )
    .overrides(
      bind[EnrolmentStoreProxyConnector].toInstance(mockEnrolmentStoreProxyConnector)
    )
    .build()

  override def beforeEach: Unit = {
    reset(mockTaxEnrolmentsConnector, mockEnrolmentStoreProxyConnector)
    super.beforeEach()
  }

  "TaxEnrolmentService" - {
    "must create a Enrolment from userAnswers and call the taxEnrolmentsConnector returning with a Successful NO_CONTENT" in {

      val response = Future.successful(Some(NO_CONTENT))
      val safeId   = SafeId("CBC12345678")

      when(mockTaxEnrolmentsConnector.createEnrolment(any())(any(), any())).thenReturn(response)
      when(mockEnrolmentStoreProxyConnector.enrolmentExists(any())(any(), any())).thenReturn(Future.successful(false))

      val subscriptionID = SubscriptionID("id")
      val address        = Address("", None, "", None, None, Country("valid", "GB", "United Kingdom"))
      val userAnswers = UserAnswers("")
        .set(DoYouHaveUTRPage, false)
        .success
        .value
        .set(ContactNamePage, "TestName")
        .success
        .value
        .set(ContactEmailPage, "test@gmail.com")
        .success
        .value
        .set(ContactPhonePage, "000000000")
        .success
        .value
        .set(BusinessWithoutIdAddressPage, address)
        .success
        .value

      val result = service.checkAndCreateEnrolment(safeId, userAnswers, subscriptionID)

      result.futureValue mustBe Right(NO_CONTENT)
    }

    "must create a Enrolment from userAnswers and call the taxEnrolmentsConnector returning with a Successful NO_CONTENT for Business without ID" in {

      val response = Future.successful(Some(NO_CONTENT))
      val safeId   = SafeId("CBC12345678")

      when(mockTaxEnrolmentsConnector.createEnrolment(any())(any(), any())).thenReturn(response)
      when(mockEnrolmentStoreProxyConnector.enrolmentExists(any())(any(), any())).thenReturn(Future.successful(false))

      val subscriptionID = SubscriptionID("id")
      val address        = Address("", None, "", None, Some("34244556"), Country("valid", "US", "United States"))
      val userAnswers = UserAnswers("")
        .set(DoYouHaveUTRPage, false)
        .success
        .value
        .set(ContactNamePage, "TestName")
        .success
        .value
        .set(ContactEmailPage, "test@gmail.com")
        .success
        .value
        .set(ContactPhonePage, "000000000")
        .success
        .value
        .set(BusinessWithoutIdAddressPage, address)
        .success
        .value

      val result = service.checkAndCreateEnrolment(safeId, userAnswers, subscriptionID)

      result.futureValue mustBe Right(NO_CONTENT)
    }

    "must return none when any other Status  is received from taxEnrolments" in {
      val response = Future.successful(None)

      val safeId = SafeId("CBC12345678")

      when(mockTaxEnrolmentsConnector.createEnrolment(any())(any(), any())).thenReturn(response)
      when(mockEnrolmentStoreProxyConnector.enrolmentExists(any())(any(), any())).thenReturn(Future.successful(false))

      val subscriptionID = SubscriptionID("id")

      val userAnswers = UserAnswers("")
        .set(DoYouHaveUTRPage, false)
        .success
        .value
        .set(ContactNamePage, "TestName")
        .success
        .value
        .set(ContactEmailPage, "test@gmail.com")
        .success
        .value
        .set(ContactPhonePage, "000000000")
        .success
        .value

      val result = service.checkAndCreateEnrolment(safeId, userAnswers, subscriptionID)

      result.futureValue mustBe Left(EnrolmentCreationError)
    }

    "must return EnrolmentExistsError when there is already an enrolment" in {

      val response = Future.successful(Some(NO_CONTENT))

      val safeId = SafeId("CBC12345678")

      when(mockTaxEnrolmentsConnector.createEnrolment(any())(any(), any())).thenReturn(response)
      when(mockEnrolmentStoreProxyConnector.enrolmentExists(any())(any(), any())).thenReturn(Future.successful(true))

      val subscriptionID = SubscriptionID("id")
      val userAnswers = UserAnswers("")
        .set(DoYouHaveUTRPage, false)
        .success
        .value
        .set(ContactNamePage, "TestName")
        .success
        .value
        .set(ContactEmailPage, "test@gmail.com")
        .success
        .value
        .set(ContactPhonePage, "000000000")
        .success
        .value

      val result = service.checkAndCreateEnrolment(safeId, userAnswers, subscriptionID)

      result.futureValue mustBe Left(EnrolmentExistsError)
    }
  }
}
