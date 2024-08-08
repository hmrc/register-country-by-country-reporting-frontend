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
import forms.DoYouHaveSecondContactFormProvider
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import pages.{ContactNamePage, DoYouHaveSecondContactPage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.DoYouHaveSecondContactView

import scala.concurrent.Future

class DoYouHaveSecondContactControllerSpec extends SpecBase {

  val formProvider             = new DoYouHaveSecondContactFormProvider()
  val form: Form[Boolean]      = formProvider()
  val contactName              = "someContact"
  val userAnswers: UserAnswers = UserAnswers(userAnswersId).set(ContactNamePage, contactName).success.value

  lazy val doYouHaveSecondContactRoute: String = routes.DoYouHaveSecondContactController.onPageLoad(NormalMode).url

  "DoYouHaveSecondContact Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, doYouHaveSecondContactRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DoYouHaveSecondContactView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, contactName)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswersWithAnswer = userAnswers.set(DoYouHaveSecondContactPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswersWithAnswer)).build()

      running(application) {
        val request = FakeRequest(GET, doYouHaveSecondContactRoute)

        val view = application.injector.instanceOf[DoYouHaveSecondContactView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode, contactName)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, doYouHaveSecondContactRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, doYouHaveSecondContactRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DoYouHaveSecondContactView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, contactName)(request, messages(application)).toString
      }
    }
  }
}
