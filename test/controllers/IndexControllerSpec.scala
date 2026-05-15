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

package controllers

import base.SpecBase
import controllers.actions.{
  CheckForSubmissionAction,
  DataRequiredAction,
  DataRequiredActionImpl,
  DataRetrievalAction,
  FakeCheckForSubmissionAction,
  FakeDataRetrievalAction,
  FakeIdentifierAction,
  FakeIdentifierActionWithCtUtr,
  IdentifierAction
}
import models.matching.RegistrationInfo
import models.register.response.details.AddressResponse
import models.{InternalProblemError, NormalMode, SafeId, UniqueTaxpayerReference, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.AutoMatchedUTRPage
import play.api.inject
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository
import services.BusinessMatchingWithIdService

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {
  val mockBusinessMatchingService: BusinessMatchingWithIdService = mock[BusinessMatchingWithIdService]

  "Index Controller" - {

    "must redirect to the IsRegisteredAddressInUkController for a GET" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IsRegisteredAddressInUkController.onPageLoad(NormalMode).url
      }
    }

    "must redirect to 'Is register AddressIn the uk' when sendBusinessRegistrationInformation fails" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockBusinessMatchingService.sendBusinessRegistrationInformation(any())(any()))
        .thenReturn(
          Future.failed(InternalProblemError)
        )

      val application = customApplicationBuilder(userAnswers = None)
        .configure(
          "keys.enrolmentKey.ct" -> "IR-CT"
        )
        .overrides(
          bind[BusinessMatchingWithIdService].toInstance(mockBusinessMatchingService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)
        val result  = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IsRegisteredAddressInUkController.onPageLoad(NormalMode).url
      }
    }

    "must redirect to isThisYourBusinessController when automatched by CT" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockBusinessMatchingService.sendBusinessRegistrationInformation(any())(any()))
        .thenReturn(
          Future.successful(
            RegistrationInfo(
              SafeId("safe"),
              "Business Name",
              AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
            )
          )
        )

      val application = customApplicationBuilder(userAnswers = None)
        .configure(
          "keys.enrolmentKey.ct" -> "IR-CT"
        )
        .overrides(
          bind[BusinessMatchingWithIdService].toInstance(mockBusinessMatchingService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)
        val result  = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IsThisYourBusinessController.onPageLoad(NormalMode).url

        val captor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository, times(3)).set(captor.capture())
        captor.getValue.get(AutoMatchedUTRPage).value mustEqual UniqueTaxpayerReference("1234567890")
      }
    }

    def customApplicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
      new GuiceApplicationBuilder()
        .overrides(
          bind[DataRequiredAction].to[DataRequiredActionImpl],
          bind[IdentifierAction].to[FakeIdentifierActionWithCtUtr],
          bind[CheckForSubmissionAction].to[FakeCheckForSubmissionAction],
          bind[SessionRepository].toInstance(mockSessionRepository),
          bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers))
        )
  }
}
