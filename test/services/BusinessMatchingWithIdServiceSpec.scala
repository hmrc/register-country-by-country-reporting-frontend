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

package services

import base.SpecBase
import connectors.RegistrationConnector
import controllers.routes
import models.*
import models.BusinessType.LimitedCompany
import models.IdentifierType.UTR
import models.matching.{RegistrationInfo, RegistrationRequest}
import models.register.request.RegisterWithID
import models.register.response.RegisterWithIDResponse
import models.register.response.details.{AddressResponse, OrganisationResponse}
import models.requests.DataRequest
import org.mockito.ArgumentMatchers.any
import pages.{BusinessNamePage, BusinessTypePage, RegistrationInfoPage, UTRPage}
import play.api.mvc.AnyContent
import play.api.mvc.Results.Redirect

import scala.concurrent.ExecutionContext.*
import scala.concurrent.{ExecutionContext, Future}

class BusinessMatchingWithIdServiceSpec extends SpecBase {
  implicit val ec: ExecutionContext = global

  val mockRegistrationConnector: RegistrationConnector       = mock[RegistrationConnector]
  val businessMatchingService: BusinessMatchingWithIdService = mock[BusinessMatchingWithIdService]
  val mockSubscriptionService: SubscriptionService           = mock[SubscriptionService]
  val mockTaxEnrolmentsService: TaxEnrolmentService          = mock[TaxEnrolmentService]

  val registrationInfo: RegistrationInfo = RegistrationInfo(
    SafeId("safe"),
    "Business Name",
    AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
  )

  val testService: BusinessMatchingWithIdService = new BusinessMatchingWithIdService(
    registrationConnector = mockRegistrationConnector,
    sessionRepository = mockSessionRepository,
    subscriptionService = mockSubscriptionService,
    taxEnrolmentService = mockTaxEnrolmentsService,
    uuidGen = uuidGenerator,
    clock = fixedClock
  )

  "BusinessMatchingWithIdService" - {

    "sendBusinessRegistrationInformation" - {
      "should return RegistrationInfo when registration connector call is successful" in {
        val registerWithID = RegisterWithID(RegistrationRequest(UTR, "1234567890", "Test Business", None, None))
        val registerWithIDResponse = RegisterWithIDResponse(
          SafeId("safe"),
          OrganisationResponse("Business Name", isAGroup = false, Some("limited"), None),
          AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
        )

        when(mockRegistrationConnector.registerWithID(any())(any(), any())).thenReturn(Future.successful(registerWithIDResponse))

        val result = testService.sendBusinessRegistrationInformation(registerWithID).futureValue

        result mustEqual registrationInfo
      }

      "should return an exception when registration connector call fails" in {
        val registerWithID = RegisterWithID(RegistrationRequest(UTR, "1234567890", "Test Business", None, None))
        when(mockRegistrationConnector.registerWithID(any())(any(), any())).thenReturn(Future.failed(NotFoundError))

        val result = testService.sendBusinessRegistrationInformation(registerWithID)

        whenReady(result.failed) { exception =>
          exception mustBe NotFoundError
        }
      }
    }

    "buildRegisterWithIdForAutoMatched" - {
      "should return a RegisterWithID  when given a UniqueTaxpayerReference" in {
        val utr    = UniqueTaxpayerReference("1234567890")
        val result = testService.buildRegisterWithIdForAutoMatched(utr)

        result must matchPattern { case RegisterWithID(_) => }
      }
    }

    "buildRegistrationRequest" - {
      "should return a RegisterWithID  when given a usernanswers" in {
        val answers = emptyUserAnswers
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(BusinessNamePage, "Test Business")
          .withPage(UTRPage, UniqueTaxpayerReference("1234567890"))

        val result = testService.buildRegistrationRequest(answers)

        result.get must matchPattern { case RegisterWithID(_) => }
      }
    }

    "selfHealingLogic" - {
      "should redirect to 'registration confirmation page' when data is present and checkAndCreateEnrolment update is successful" in {
        val userAnswers     = emptyUserAnswers.withPage(RegistrationInfoPage, registrationInfo)
        val mockDatarequest = mock[DataRequest[AnyContent]]
        when(mockDatarequest.userAnswers).thenReturn(userAnswers)
        when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
        when(mockSubscriptionService.getDisplaySubscriptionId(any())(any())).thenReturn(Future.successful(Some(SubscriptionID("subscriptionId"))))
        when(mockTaxEnrolmentsService.checkAndCreateEnrolment(any(), any(), any())(any(), any())).thenReturn(Future.successful(Right(())))

        val result = testService.selfHealingLogic()(hc, mockDatarequest).futureValue

        result mustBe Redirect(routes.RegistrationConfirmationController.onPageLoad())
      }

      "should redirect to 'your contact details page' when subscription id is not returned by the getDisplaySubscriptionId" in {
        val userAnswers     = emptyUserAnswers.withPage(RegistrationInfoPage, registrationInfo)
        val mockDatarequest = mock[DataRequest[AnyContent]]
        when(mockDatarequest.userAnswers).thenReturn(userAnswers)
        when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
        when(mockSubscriptionService.getDisplaySubscriptionId(any())(any())).thenReturn(Future.successful(None))

        val result = testService.selfHealingLogic()(hc, mockDatarequest).futureValue
        result mustBe Redirect(routes.YourContactDetailsController.onPageLoad(NormalMode))
      }

      "should redirect to 'problem page' checkAndCreateEnrolment returns EnrolmentCreationError" in {
        val userAnswers     = emptyUserAnswers.withPage(RegistrationInfoPage, registrationInfo)
        val mockDatarequest = mock[DataRequest[AnyContent]]
        when(mockDatarequest.userAnswers).thenReturn(userAnswers)
        when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
        when(mockSubscriptionService.getDisplaySubscriptionId(any())(any())).thenReturn(Future.successful(Some(SubscriptionID("subscriptionId"))))
        when(mockTaxEnrolmentsService.checkAndCreateEnrolment(any(), any(), any())(any(), any())).thenReturn(Future.successful(Left(EnrolmentCreationError)))

        val result = testService.selfHealingLogic()(hc, mockDatarequest).futureValue

        result mustBe Redirect(routes.ThereIsAProblemController.onPageLoad())
      }

      "should redirect to 'already registered page' checkAndCreateEnrolment returns EnrolmentExistsError" in {
        val userAnswers     = emptyUserAnswers.withPage(RegistrationInfoPage, registrationInfo)
        val mockDatarequest = mock[DataRequest[AnyContent]]
        when(mockDatarequest.userAnswers).thenReturn(userAnswers)
        when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
        when(mockSubscriptionService.getDisplaySubscriptionId(any())(any())).thenReturn(Future.successful(Some(SubscriptionID("subscriptionId"))))
        when(mockTaxEnrolmentsService.checkAndCreateEnrolment(any(), any(), any())(any(), any())).thenReturn(Future.successful(Left(EnrolmentExistsError)))

        val result = testService.selfHealingLogic()(hc, mockDatarequest).futureValue

        result mustBe Redirect(routes.PreRegisteredController.onPageLoad(true))
      }
    }
  }
}
