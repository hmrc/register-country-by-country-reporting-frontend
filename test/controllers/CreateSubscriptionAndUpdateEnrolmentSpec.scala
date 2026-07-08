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
import models.*
import models.matching.RegistrationInfo
import models.register.response.details.AddressResponse
import models.requests.DataRequest
import org.mockito.ArgumentMatchers.any
import pages.RegistrationInfoPage
import play.api.http.Status.SEE_OTHER
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import repositories.SessionRepository
import services.{SubscriptionService, TaxEnrolmentService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateSubscriptionAndUpdateEnrolmentSpec extends SpecBase {

  val safeId: SafeId                 = SafeId("XE0001234567890")
  val subscriptionId: SubscriptionID = SubscriptionID("123456789012")
  val addrRes: AddressResponse       = AddressResponse("addressLine1", Some("addressLine2"), Some("addressLine3"), Some("addressLine4"), Some("AA1 1AA"), "GB")

  val mockSubscriptionService: SubscriptionService = mock[SubscriptionService]
  val mockTaxEnrolmentService: TaxEnrolmentService = mock[TaxEnrolmentService]

  class Harness extends CreateSubscriptionAndUpdateEnrolment {
    override val subscriptionService: SubscriptionService = mockSubscriptionService
    override val taxEnrolmentService: TaxEnrolmentService = mockTaxEnrolmentService
    override val sessionRepository: SessionRepository     = mockSessionRepository
  }

  val createSubAndUpdateEnrolment = new Harness

  override def beforeEach(): Unit = {
    reset(mockSubscriptionService, mockTaxEnrolmentService, mockSessionRepository)
    when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
  }

  def request(userAnswers: UserAnswers): DataRequest[AnyContent] =
    DataRequest(FakeRequest(), userAnswersId, userAnswers)

  "updateSubscriptionIdAndCreateEnrolment" - {

    "must redirect to RegistrationConfirmationController when enrolment is created successfully" in {
      implicit val dataRequest: DataRequest[AnyContent] = request(emptyUserAnswers)

      when(mockTaxEnrolmentService.checkAndCreateEnrolment(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(Right(())))

      val result = createSubAndUpdateEnrolment.updateSubscriptionIdAndCreateEnrolment(safeId, subscriptionId)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.RegistrationConfirmationController.onPageLoad().url
      verify(mockSessionRepository, times(1)).set(any())
    }

    "must redirect to ThereIsAProblemController when EnrolmentCreationError is returned" in {
      implicit val dataRequest: DataRequest[AnyContent] = request(emptyUserAnswers)

      when(mockTaxEnrolmentService.checkAndCreateEnrolment(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(Left(EnrolmentCreationError)))

      val result = createSubAndUpdateEnrolment.updateSubscriptionIdAndCreateEnrolment(safeId, subscriptionId)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ThereIsAProblemController.onPageLoad().url
    }

    "must redirect to PreRegisteredController when EnrolmentExistsError is returned and RegistrationInfoPage is defined" in {
      val someRegistrationInfo                          = RegistrationInfo(safeId, "name", addrRes)
      val userAnswers                                   = emptyUserAnswers.withPage(RegistrationInfoPage, someRegistrationInfo)
      implicit val dataRequest: DataRequest[AnyContent] = request(userAnswers)

      when(mockTaxEnrolmentService.checkAndCreateEnrolment(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(Left(EnrolmentExistsError)))

      val result = createSubAndUpdateEnrolment.updateSubscriptionIdAndCreateEnrolment(safeId, subscriptionId)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.PreRegisteredController.onPageLoad().url
    }

    "must redirect to ThereIsAProblemController when EnrolmentExistsError is returned and RegistrationInfoPage is not defined (should be impossible in practice)" in {
      implicit val dataRequest: DataRequest[AnyContent] = request(emptyUserAnswers)

      when(mockTaxEnrolmentService.checkAndCreateEnrolment(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(Left(EnrolmentExistsError)))

      val result = createSubAndUpdateEnrolment.updateSubscriptionIdAndCreateEnrolment(safeId, subscriptionId)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ThereIsAProblemController.onPageLoad().url
    }
  }
}
