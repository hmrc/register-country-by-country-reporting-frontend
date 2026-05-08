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
import connectors.RegistrationConnector
import controllers.actions.*
import forms.IsThisYourBusinessFormProvider
import models.*
import models.BusinessType.LimitedCompany
import models.IdentifierType.UTR
import models.matching.{RegistrationInfo, RegistrationRequest}
import models.register.request.RegisterWithID
import models.register.response.RegisterWithIDResponse
import models.register.response.details.{AddressResponse, OrganisationResponse}
import org.mockito.ArgumentMatchers.{any, eq as mockitoEq}
import pages.*
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository
import services.{BusinessMatchingWithIdService, SubscriptionService, TaxEnrolmentService}
import views.html.{BusinessNotIdentifiedView, IsThisYourBusinessView}

import scala.concurrent.Future

class IsThisYourBusinessControllerSpec extends SpecBase {

  val formProvider = new IsThisYourBusinessFormProvider()
  val form         = formProvider()

  lazy val isThisYourBusinessRoute    = routes.IsThisYourBusinessController.onPageLoad(NormalMode).url
  lazy val businessNotIdentifiedRoute = routes.BusinessNotIdentifiedController.onPageLoad().url
  val findCompanyName                 = "https://find-and-update.company-information.service.gov.uk/"
  private val SafeIdValue             = "XE0000123456789"
  val UtrValue                        = "1234567890"
  val OrgName                         = "Some Test Org"
  val safeId: SafeId                  = SafeId("XE0000123456789")
  private val address                 = AddressResponse("line1", None, None, None, None, "GB")

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(BusinessTypePage, LimitedCompany)
    .success
    .value
    .set(UTRPage, utr)
    .success
    .value
    .set(BusinessNamePage, "Business Name")
    .success
    .value

  private val registrationRequest = RegistrationRequest(UTR, utr.uniqueTaxPayerReference, OrgName, Some(LimitedCompany))

  val registrationInfo: RegistrationInfo = RegistrationInfo(
    SafeId("safe"),
    "Business Name",
    AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
  )

  val mockRegistrationConnector: RegistrationConnector   = mock[RegistrationConnector]
  val mockSubscriptionService: SubscriptionService       = mock[SubscriptionService]
  val mockTaxEnrolmentsService: TaxEnrolmentService      = mock[TaxEnrolmentService]
  val mockMatchingService: BusinessMatchingWithIdService = mock[BusinessMatchingWithIdService]
  val mockUUIDGen: UUIDGen                               = mock[UUIDGen]

  override def beforeEach(): Unit =
    reset(
      mockSubscriptionService,
      mockTaxEnrolmentsService,
      mockRegistrationConnector
    )

  "IsThisYourBusiness Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers))
        .configure("show-is-your-business-page.on-self-healing-journey.enabled" -> true)
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector),
          bind[SubscriptionService].toInstance(mockSubscriptionService),
          bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
        )
        .build()
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      // when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockRegistrationConnector.registerWithID(any())(any(), any()))
        .thenReturn(
          Future.successful(
            Right(
              RegisterWithIDResponse(
                SafeId("safe"),
                OrganisationResponse("Business Name", isAGroup = false, Some("limited"), None),
                AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
              )
            )
          )
        )

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IsThisYourBusinessView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, registrationInfo, NormalMode)(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET when there is no CT UTR" in {

      val registerWithID = RegisterWithID(registrationRequest)

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers))
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector),
          bind[SubscriptionService].toInstance(mockSubscriptionService),
          bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
        )
        .build()
      when(mockMatchingService.sendBusinessRegistrationInformation(mockitoEq(registerWithID))(any(), any()))
        .thenReturn(Future.successful(Right(RegistrationInfo(safeId, OrgName, address))))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockRegistrationConnector.registerWithID(any())(any(), any()))
        .thenReturn(
          Future.successful(
            Right(
              RegisterWithIDResponse(
                SafeId("safe"),
                OrganisationResponse("Business Name", isAGroup = false, Some("limited"), None),
                AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
              )
            )
          )
        )

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IsThisYourBusinessView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, registrationInfo, NormalMode)(request, messages(application)).toString
      }
    }

    // todo remove  when FF is removed
    "redirect to Registration Confirmation Page for business when they are already subscribed but no enrolment created" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers))
        .configure("show-is-your-business-page.on-self-healing-journey.enabled" -> false)
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector),
          bind[SubscriptionService].toInstance(mockSubscriptionService),
          bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
        )
        .build()

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockRegistrationConnector.registerWithID(any())(any(), any()))
        .thenReturn(
          Future.successful(
            Right(
              RegisterWithIDResponse(
                SafeId("safe"),
                OrganisationResponse("Business Name", isAGroup = false, Some("limited"), None),
                AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
              )
            )
          )
        )
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(Some(SubscriptionID("subscriptionId"))))
      when(mockTaxEnrolmentsService.checkAndCreateEnrolment(any(), any(), any())(any(), any())).thenReturn(Future.successful(Right(NO_CONTENT)))

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result) mustBe Some(routes.RegistrationConfirmationController.onPageLoad().url)
      }
    }

    // todo remove  when FF is removed
    "redirect to Registration Confirmation Page for business when they are already subscribed and have enrolment with other goverment gateway account" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers))
        .configure("show-is-your-business-page.on-self-healing-journey.enabled" -> false)
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector),
          bind[SubscriptionService].toInstance(mockSubscriptionService),
          bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
        )
        .build()

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockRegistrationConnector.registerWithID(any())(any(), any()))
        .thenReturn(
          Future.successful(
            Right(
              RegisterWithIDResponse(
                SafeId("safe"),
                OrganisationResponse("Business Name", isAGroup = false, Some("limited"), None),
                AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
              )
            )
          )
        )
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(Some(SubscriptionID("subscriptionId"))))
      when(mockTaxEnrolmentsService.checkAndCreateEnrolment(any(), any(), any())(any(), any())).thenReturn(Future.successful(Left(EnrolmentExistsError)))

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result) mustBe Some(routes.PreRegisteredController.onPageLoad(true).url)
      }
    }

    "must redirect to the BusinessNotIdentifiedPage for a GET when there is no CT UTR and RegistrationInfo not found" in {

      val registerWithID = RegisterWithID(registrationRequest)
      val startUrl       = routes.IsRegisteredAddressInUkController.onPageLoad(NormalMode).url

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers))
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector),
          bind[SubscriptionService].toInstance(mockSubscriptionService),
          bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
        )
        .build()
      when(mockMatchingService.sendBusinessRegistrationInformation(mockitoEq(registerWithID))(any(), any()))
        .thenReturn(Future.successful(Left(NotFoundError)))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockRegistrationConnector.registerWithID(any())(any(), any()))
        .thenReturn(
          Future.successful(
            Right(
              RegisterWithIDResponse(
                SafeId("safe"),
                OrganisationResponse("Business Name", isAGroup = false, Some("limited"), None),
                AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
              )
            )
          )
        )

      running(application) {
        val request = FakeRequest(GET, businessNotIdentifiedRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[BusinessNotIdentifiedView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(findCompanyName, startUrl, Some(LimitedCompany))(request, messages(application)).toString
      }
    }

    "redirect to we are yet to identify your business when it's a non-match" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers))
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector)
        )
        .build()

      when(mockRegistrationConnector.registerWithID(any())(any(), any()))
        .thenReturn(Future.successful(Left(NotFoundError)))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result) mustBe Some(routes.BusinessNotIdentifiedController.onPageLoad().url)
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseUserAnswers.set(IsThisYourBusinessPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector)
        )
        .build()
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(None))

      when(mockRegistrationConnector.registerWithID(any())(any(), any()))
        .thenReturn(
          Future.successful(
            Right(
              RegisterWithIDResponse(
                SafeId("safe"),
                OrganisationResponse("Business Name", isAGroup = false, Some("limited"), None),
                AddressResponse("Line 1", Some("Line 2"), None, None, None, "DE")
              )
            )
          )
        )

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val view = application.injector.instanceOf[IsThisYourBusinessView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), registrationInfo, NormalMode)(request, messages(application)).toString
      }
    }

    "self-healing: must redirect to confirmation page when user answers yes and they are already subscribed and checkAndCreateEnrolment passes" in {
      val userAnswers = baseUserAnswers.set(RegistrationInfoPage, registrationInfo).success.value
      val application = customApplicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector),
          bind[SubscriptionService].toInstance(mockSubscriptionService),
          bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
        )
        .build()

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(Some(SubscriptionID("subscriptionId"))))
      when(mockTaxEnrolmentsService.checkAndCreateEnrolment(any(), any(), any())(any(), any())).thenReturn(Future.successful(Right(NO_CONTENT)))

      running(application) {
        val request =
          FakeRequest(POST, isThisYourBusinessRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result) mustBe Some(routes.RegistrationConfirmationController.onPageLoad().url)
      }
    }

    "self-healing: must redirect to problem page when user answers yes and they are already subscribed and checkAndCreateEnrolment returns EnrolmentCreationError" in {
      val userAnswers = baseUserAnswers.set(RegistrationInfoPage, registrationInfo).success.value
      val application = customApplicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector),
          bind[SubscriptionService].toInstance(mockSubscriptionService),
          bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
        )
        .build()

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(Some(SubscriptionID("subscriptionId"))))
      when(mockTaxEnrolmentsService.checkAndCreateEnrolment(any(), any(), any())(any(), any())).thenReturn(Future.successful(Left(EnrolmentCreationError)))

      running(application) {
        val request =
          FakeRequest(POST, isThisYourBusinessRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe routes.ThereIsAProblemController.onPageLoad().url
      }
    }

    "self-healing: must redirect to problem page when user answers yes and they are already subscribed and checkAndCreateEnrolment returns EnrolmentExistsError" in {
      val userAnswers = baseUserAnswers.set(RegistrationInfoPage, registrationInfo).success.value
      val application = customApplicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[RegistrationConnector].toInstance(mockRegistrationConnector),
          bind[SubscriptionService].toInstance(mockSubscriptionService),
          bind[TaxEnrolmentService].toInstance(mockTaxEnrolmentsService)
        )
        .build()

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(Some(SubscriptionID("subscriptionId"))))
      when(mockTaxEnrolmentsService.checkAndCreateEnrolment(any(), any(), any())(any(), any())).thenReturn(Future.successful(Left(EnrolmentExistsError)))

      running(application) {
        val request =
          FakeRequest(POST, isThisYourBusinessRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe routes.PreRegisteredController.onPageLoad(true).url
      }
    }

    "must redirect to your contact details when the user answers yes and there have no subscription" in {
      val userAnswers = baseUserAnswers.set(RegistrationInfoPage, registrationInfo).success.value
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = customApplicationBuilder(userAnswers = Some(userAnswers))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, isThisYourBusinessRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.YourContactDetailsController.onPageLoad(NormalMode).url
      }
    }

    "must redirect to it is a different business page  when the user answers no and their are auto matched by corporate tax" in {
      val userAnswers = baseUserAnswers
        .set(RegistrationInfoPage, registrationInfo)
        .success
        .value
        .set(AutoMatchedUTRPage, UniqueTaxpayerReference("1234567890"))
        .success
        .value
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = customApplicationBuilder(userAnswers = Some(userAnswers))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, isThisYourBusinessRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.DifferentBusinessController.onPageLoad().url
      }
    }

    "must redirect to it is a different business page  when the user answers no and their are not auto matched by corporate tax" in {
      val userAnswers = baseUserAnswers.set(RegistrationInfoPage, registrationInfo).success.value
      when(mockSubscriptionService.getDisplaySubscriptionId(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = customApplicationBuilder(userAnswers = Some(userAnswers))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, isThisYourBusinessRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.BusinessNotIdentifiedController.onPageLoad().url
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

    "must redirect to sign out page for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, isThisYourBusinessRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.auth.routes.AuthController.signOutNoSurvey().url
      }
    }

    "must redirect to sign out page for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, isThisYourBusinessRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.auth.routes.AuthController.signOutNoSurvey().url
      }
    }
  }

  protected def customApplicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[CheckForSubmissionAction].to[FakeCheckForSubmissionAction],
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers))
      )
}
