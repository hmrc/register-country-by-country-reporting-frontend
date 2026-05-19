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
import models.{SubscriptionID, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import pages.SubscriptionIDPage
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{inject, Application}
import services.EmailService
import views.html.RegistrationConfirmationView

import scala.concurrent.Future

class RegistrationConfirmationControllerSpec extends SpecBase with BeforeAndAfterEach {

  val subscriptionId = "XTCBC0100000001"

  private val mockEmailService: EmailService = mock[EmailService]

  override lazy val app: Application = new GuiceApplicationBuilder()
    .overrides(
      inject.bind[EmailService].toInstance(mockEmailService)
    )
    .build()

  override def beforeEach(): Unit =
    reset(
      mockEmailService
    )

  "RegistrationConfirmation Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockSessionRepository.reset(any())).thenReturn(Future.successful(true))

      when(mockEmailService.sendEmail(any(), any())(any())).thenReturn(Future.successful(Some(ACCEPTED)))

      val userAnswers = UserAnswers(userAnswersId)
        .set(SubscriptionIDPage, SubscriptionID(subscriptionId))
        .success
        .value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.RegistrationConfirmationController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RegistrationConfirmationView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(subscriptionId)(request, messages(application)).toString
      }
    }

    "must return Technical difficulties and the correct view for a GET" in {

      when(mockSessionRepository.reset(any())).thenReturn(Future.successful(true))
      val userAnswers = UserAnswers(userAnswersId)
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.RegistrationConfirmationController.onPageLoad().url)

        val result = route(application, request).value

        application.injector.instanceOf[RegistrationConfirmationView]

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.InformationSentController.onPageLoad().url

      }
    }
  }
}
