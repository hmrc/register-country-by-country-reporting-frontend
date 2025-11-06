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
import forms.UTRFormProvider
import models.BusinessType.{LimitedCompany, Partnership}
import models.{NormalMode, UniqueTaxpayerReference, UserAnswers}
import org.mockito.ArgumentMatchers.any
import pages.{BusinessTypePage, RegistrationInfoPage, UTRPage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.UTRView

import scala.concurrent.Future

class UTRControllerSpec extends SpecBase {

  lazy val utrRoute                       = routes.UTRController.onPageLoad(NormalMode).url
  lazy val utrRouteSubmit                 = routes.UTRController.onSubmit(NormalMode).url
  val formProvider                        = new UTRFormProvider()
  val form: Form[UniqueTaxpayerReference] = formProvider("Self Assessment")
  val caTaxType                           = "corporation"
  val saTaxType                           = "partnership"

  override def beforeEach: Unit = {
    reset(mockSessionRepository)
    super.beforeEach
  }

  "UTR Controller" - {

    "must return OK and the correct view for a GET when businessType is Limited Company" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId).withPage(BusinessTypePage, LimitedCompany)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, utrRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[UTRView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, caTaxType)(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET when businessType is Partnership" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .withPage(BusinessTypePage, Partnership)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, utrRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[UTRView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, saTaxType)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .withPage(BusinessTypePage, Partnership)
        .withPage(UTRPage, utr)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, utrRoute)

        val view = application.injector.instanceOf[UTRView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(utr), NormalMode, saTaxType)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid New data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId)
        .withPage(BusinessTypePage, LimitedCompany)
        .withPage(RegistrationInfoPage, arbitraryRegistrationInfo.arbitrary.sample.get)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, utrRouteSubmit)
            .withFormUrlEncodedBody(("value", utr.uniqueTaxPayerReference))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the next page when same data is submitted" in {

      val userAnswers = UserAnswers(userAnswersId)
        .withPage(BusinessTypePage, LimitedCompany)
        .withPage(RegistrationInfoPage, arbitraryRegistrationInfo.arbitrary.sample.get)
        .withPage(UTRPage, utr)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, utrRouteSubmit)
            .withFormUrlEncodedBody(("value", utr.uniqueTaxPayerReference))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
        verify(mockSessionRepository, times(0)).set(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .withPage(BusinessTypePage, LimitedCompany)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, utrRoute)
            .withFormUrlEncodedBody(("value", ""))

        val form: Form[UniqueTaxpayerReference] = formProvider(caTaxType)
        val boundForm                           = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[UTRView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, caTaxType)(request, messages(application)).toString
      }
    }
  }
}
