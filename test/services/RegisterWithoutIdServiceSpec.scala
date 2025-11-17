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
import connectors.RegistrationConnector
import models.requests.DataRequest
import models.{Address, ApiError, Country, MandatoryInformationMissingError, RegistrationWithoutIdInformationMissingError, SafeId, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.mockito.MockitoSugar
import pages.*
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContent

import java.time.LocalDate
import scala.concurrent.Future

class RegisterWithoutIdServiceSpec extends SpecBase with MockitoSugar {

  val mockRegistrationConnector: RegistrationConnector = mock[RegistrationConnector]

  val service: RegisterWithoutIdService = app.injector.instanceOf[RegisterWithoutIdService]

  override lazy val app: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[RegistrationConnector].toInstance(mockRegistrationConnector)
    )
    .build()

  override def beforeEach(): Unit = {
    reset(
      mockRegistrationConnector
    )
    super.beforeEach()
  }

  val dob: LocalDate = LocalDate.now

  val address: Address = Address("line 1", Some("line 2"), "line 3", Some("line 4"), Some(""), Country.GB)

  "RegisterWithoutIdService" - {

    "sendBusinessRegistration" - {
      "must return matching information when safeId can be recovered" in {
        val address = Address("", None, "", None, None, Country("valid", "GB", "United Kingdom"))
        val userAnswers = UserAnswers("")
          .set(DoYouHaveUTRPage, false)
          .success
          .value
          .set(BusinessWithoutIDNamePage, "TestName")
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
          .set(DoYouHaveSecondContactPage, false)
          .success
          .value
        val mockDataRequest = mock[DataRequest[AnyContent]]
        when(mockDataRequest.userAnswers).thenReturn(userAnswers)
        val response = Future.successful(Right(Some(SafeId("XE0000123456789"))))

        when(mockRegistrationConnector.registerWithoutID(any())(any(), any())).thenReturn(response)

        val result: Future[Either[ApiError, SafeId]] = service.registerWithoutId()(mockDataRequest, hc)

        result.futureValue mustBe Right(SafeId("XE0000123456789"))
      }

      "must return an error when when safeId can't be recovered" in {
        val address = Address("", None, "", None, None, Country("valid", "GB", "United Kingdom"))
        val userAnswers = UserAnswers("")
          .set(DoYouHaveUTRPage, false)
          .success
          .value
          .set(BusinessWithoutIDNamePage, "TestName")
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
          .set(DoYouHaveSecondContactPage, true)
          .success
          .value
        val mockDatarequest = mock[DataRequest[AnyContent]]
        when(mockDatarequest.userAnswers).thenReturn(userAnswers)

        val response = Future.successful(Right(None))

        when(mockRegistrationConnector.registerWithoutID(any())(any(), any())).thenReturn(response)

        val result: Future[Either[ApiError, SafeId]] = service.registerWithoutId()(mockDatarequest, hc)

        result.futureValue mustBe Left(RegistrationWithoutIdInformationMissingError("SafeId missing"))
      }

    }
  }
}
