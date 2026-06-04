/*
 * Copyright 2026 HM Revenue & Customs
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
import controllers.actions.{FakePrivateBetaAction, PrivateBetaAction}
import forms.PrivateBetaAccessCodeFormProvider
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import play.api.inject
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Future

class PrivateBetaAccessCodeControllerSpec extends SpecBase {

  val formProvider = new PrivateBetaAccessCodeFormProvider()
  val form         = formProvider()

  lazy val privateBetaAccessCodeRoute = routes.PrivateBetaAccessCodeController.onPageLoad().url

  "PrivateBetaAccessCode Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = customApplicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      running(application) {
        val request = FakeRequest(GET, privateBetaAccessCodeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[views.html.PrivateBetaAccessCodeView]

        status(result) mustEqual OK
      }
    }

    "must redirect to the index page when valid data is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers

      val application = customApplicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, privateBetaAccessCodeRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad().url
        verify(mockSessionRepository, times(0)).set(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers

      val application = customApplicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, privateBetaAccessCodeRoute)
            .withFormUrlEncodedBody(("value", ""))

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
      }
    }

    def customApplicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
      new GuiceApplicationBuilder()
        .overrides(
          bind[PrivateBetaAction].to[FakePrivateBetaAction]
        )
  }
}
