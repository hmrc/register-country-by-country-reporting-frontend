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

package controllers

import base.SpecBase
import connectors.RegistrationConnector
import forms.IsThisYourBusinessFormProvider
import models.BusinessType.LimitedCompany
import models.matching.RegistrationInfo
import models.register.response.RegisterWithIDResponse
import models.register.response.details.{AddressResponse, OrganisationResponse}
import models.{NormalMode, NotFoundError, SafeId}
import org.mockito.ArgumentMatchers.any
import pages.{BusinessNamePage, BusinessTypePage, IsThisYourBusinessPage, RegistrationInfoPage, UTRPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.IsThisYourBusinessView

import scala.concurrent.Future

class IsThisYourBusinessControllerSpec extends SpecBase {

  val formProvider = new IsThisYourBusinessFormProvider()
  val form = formProvider()

  lazy val isThisYourBusinessRoute = routes.IsThisYourBusinessController.onPageLoad(NormalMode).url

  val baseUserAnswers = emptyUserAnswers
    .set(BusinessTypePage, LimitedCompany).success.value
    .set(UTRPage, "1234567890").success.value
    .set(BusinessNamePage, "Business Name").success.value

  val registrationInfo = RegistrationInfo(
    SafeId("safe"),
    "Business Name",
    AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
  )

  val mockRegistrationConnector = mock[RegistrationConnector]

  "IsThisYourBusiness Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers))
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector)
        ).build()

      when(mockRegistrationConnector.registerWithID(any())(any(), any()))
        .thenReturn(Future.successful(Right(RegisterWithIDResponse(
          SafeId("safe"),
          OrganisationResponse("Business Name", isAGroup = false, Some("limited"), None),
          AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
        ))))

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IsThisYourBusinessView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, registrationInfo, NormalMode)(request, messages(application)).toString
      }
    }

    "redirect to we are yet to identify your business when it's a non-match" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers))
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector)
        ).build()

      when(mockRegistrationConnector.registerWithID(any())(any(), any()))
        .thenReturn(Future.successful(Left(NotFoundError)))

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IsThisYourBusinessView]

        status(result) mustEqual SEE_OTHER

       redirectLocation(result) mustBe Some(routes.BusinessNotIdentifiedController.onPageLoad().url)
      }
    }




    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseUserAnswers.set(IsThisYourBusinessPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector)
        ).build()

      when(mockRegistrationConnector.registerWithID(any())(any(), any()))
        .thenReturn(Future.successful(Right(RegisterWithIDResponse(
          SafeId("safe"),
          OrganisationResponse("Business Name", isAGroup = false, Some("limited"), None),
          AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
        ))))

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val view = application.injector.instanceOf[IsThisYourBusinessView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), registrationInfo, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, isThisYourBusinessRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = baseUserAnswers.set(RegistrationInfoPage, registrationInfo).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, isThisYourBusinessRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[IsThisYourBusinessView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, registrationInfo, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, isThisYourBusinessRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
