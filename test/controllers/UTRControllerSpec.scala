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
import forms.UTRFormProvider
import models.BusinessType.{LimitedCompany, Partnership}
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.mockito.MockitoSugar
import pages.{BusinessTypePage, UTRPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.UTRView

import scala.concurrent.Future

class UTRControllerSpec extends SpecBase with MockitoSugar {

  lazy val uTRRoute = routes.UTRController.onPageLoad(NormalMode).url
  val formProvider = new UTRFormProvider()
  val form: Form[String] = formProvider("")

  "UTR Controller" - {

    "must return OK and the correct view for a GET when businessType is Limited Company" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(BusinessTypePage, LimitedCompany)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, uTRRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[UTRView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, "Corporation Tax")(request, messages(application)).toString
      }
    }


    "must return OK and the correct view for a GET when businessType is Partnership" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(BusinessTypePage, Partnership)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, uTRRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[UTRView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, "Self Assessment")(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(UTRPage, "answer").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, uTRRoute)

        val view = application.injector.instanceOf[UTRView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), NormalMode, "")(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, uTRRoute)
            .withFormUrlEncodedBody(("value", "1234567890"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, uTRRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[UTRView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, "")(request, messages(application)).toString
      }
    }
  }
}
