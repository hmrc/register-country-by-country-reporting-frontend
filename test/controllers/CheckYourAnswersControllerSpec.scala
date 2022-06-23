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
import models.matching.RegistrationInfo
import models.register.response.details.AddressResponse
import models.{EnrolmentCreationError, SafeId, SubscriptionID, UserAnswers}
import org.mockito.ArgumentMatchers.any
import pages.RegistrationInfoPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{SubscriptionService, TaxEnrolmentService}
import viewmodels.govuk.SummaryListFluency

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {

  val mockSubscriptionService: SubscriptionService  = mock[SubscriptionService]
  val mockTaxEnrolmentsService: TaxEnrolmentService = mock[TaxEnrolmentService]

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains(messages(app)("checkYourAnswers.businessDetails"))
        contentAsString(result).contains(messages(app)("checkYourAnswers.firstContact"))
        contentAsString(result).contains(messages(app)("checkYourAnswers.secondContact"))
      }
    }

    "must redirect to sign out page for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.auth.routes.AuthController.signOutNoSurvey.url
      }
    }
  }

  "must return 501 NotImplemented and the correct view onSubmit" in {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
      .overrides(
        bind[SubscriptionService].toInstance(mockSubscriptionService),
        bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
      )
      .build()

    running(application) {
      val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)
      val result  = route(application, request).value

      status(result) mustEqual NOT_IMPLEMENTED
    }
  }

  "must return OK  and the confirmation view onSubmit" in {
    val registrationInfo = RegistrationInfo(
      SafeId("safe"),
      "Business Name",
      AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
    )
    val userAnswers = UserAnswers(userAnswersId).set(RegistrationInfoPage, registrationInfo).success.value

    when(mockSubscriptionService.checkAndCreateSubscription(any(), any())(any(), any())) thenReturn Future.successful(Right(SubscriptionID("111111")))
    when(mockTaxEnrolmentsService.checkAndCreateEnrolment(any(), any(), any())(any(), any())) thenReturn Future.successful(Right(NO_CONTENT))
    when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

    val application = applicationBuilder(userAnswers = Some(userAnswers))
      .overrides(
        bind[SubscriptionService].toInstance(mockSubscriptionService),
        bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
      )
      .build()

    running(application) {
      val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.RegistrationConfirmationController.onPageLoad().url

    }
  }

  "must return OK  and the business already registered view onSubmit" in {
    val registrationInfo = RegistrationInfo(
      SafeId("safe"),
      "Business Name",
      AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
    )
    val userAnswers = UserAnswers(userAnswersId).set(RegistrationInfoPage, registrationInfo).success.value

    when(mockSubscriptionService.checkAndCreateSubscription(any(), any())(any(), any())) thenReturn Future.successful(Right(SubscriptionID("111111")))
    when(mockTaxEnrolmentsService.checkAndCreateEnrolment(any(), any(), any())(any(), any())) thenReturn Future.successful(Left(EnrolmentCreationError))
    when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

    val application = applicationBuilder(userAnswers = Some(userAnswers))
      .overrides(
        bind[SubscriptionService].toInstance(mockSubscriptionService),
        bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
      )
      .build()

    running(application) {
      val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.PreRegisteredController.onPageLoad(true).url

    }
  }

}
