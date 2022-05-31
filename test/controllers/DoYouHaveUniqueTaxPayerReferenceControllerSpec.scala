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
import forms.DoYouHaveUniqueTaxPayerReferenceFormProvider
import models.UserAnswers
import navigation.{CBCRNavigator, FakeCBCRNavigator, FakeNavigator}
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.mockito.MockitoSugar
import pages.DoYouHaveUniqueTaxPayerReferencePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.DoYouHaveUniqueTaxPayerReferenceView

import scala.concurrent.Future

class DoYouHaveUniqueTaxPayerReferenceControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new DoYouHaveUniqueTaxPayerReferenceFormProvider()
  val form         = formProvider()

  lazy val DoYouHaveUniqueTaxPayerReferenceRoute = routes.DoYouHaveUniqueTaxPayerReferenceController.onPageLoad.url

  "DoYouHaveUniqueTaxPayerReference Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder().build()

      running(application) {
        val request = FakeRequest(GET, DoYouHaveUniqueTaxPayerReferenceRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DoYouHaveUniqueTaxPayerReferenceView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder().build()

      running(application) {
        val request =
          FakeRequest(POST, DoYouHaveUniqueTaxPayerReferenceRoute).withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder().build()

      running(application) {
        val request =
          FakeRequest(POST, DoYouHaveUniqueTaxPayerReferenceRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DoYouHaveUniqueTaxPayerReferenceView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm)(request, messages(application)).toString
      }
    }

  }
}
