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

package controllers.actions

import base.SpecBase
import com.google.inject.Inject
import config.FrontendAppConfig
import models.requests.IdentifierRequest
import org.mockito.ArgumentMatchers.any
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{~, Retrieval}
import uk.gov.hmrc.http.HeaderCarrier
import utils.RetrievalOps._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase  {

  type AuthAction = ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

  class Harness(authAction: AuthAction) {

    def onPageLoad() = authAction {
      _ => Results.Ok
    }
  }

  type AuthRetrievals = Option[String] ~ Enrolments ~ Option[AffinityGroup] ~ Option[CredentialRole]

    applicationBuilder()
    .overrides(inject.bind[AuthConnector].toInstance(mockAuthConnector))

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val bodyParsers                      = app.injector.instanceOf[BodyParsers.Default]
  val appConfig                        = app.injector.instanceOf[FrontendAppConfig]

  "Auth Action" - {

    "when the user hasn't logged in" - {

      "must redirect the user to log in " in {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), appConfig, bodyParsers).apply()
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value must startWith(appConfig.loginUrl)
      }
    }

    "the user's session has expired" - {

      "must redirect the user to log in " in {

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired), appConfig, bodyParsers).apply()
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value must startWith(appConfig.loginUrl)
      }
    }

    "the user doesn't have sufficient enrolments" - {

      "must redirect the user to the unauthorised page" in {

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientEnrolments), appConfig, bodyParsers).apply()
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe controllers.routes.JourneyRecoveryController.onPageLoad().url //TODO change to controllers.routes.ThereIsAProblemController.onPageLoad().url
      }
    }

    "the user doesn't have sufficient confidence level" - {

      "must redirect the user to the unauthorised page" in {

        val authAction =
          new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientConfidenceLevel), appConfig, bodyParsers).apply()
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe controllers.routes.JourneyRecoveryController.onPageLoad().url //TODO change to controllers.routes.ThereIsAProblemController.onPageLoad().url
      }
    }

    "the user used an unaccepted auth provider" - {

      "must redirect the user to the unauthorised page" in {

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAuthProvider), appConfig, bodyParsers).apply()
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe controllers.routes.JourneyRecoveryController.onPageLoad().url //TODO change to controllers.routes.ThereIsAProblemController.onPageLoad().url
      }
    }

    "the user has an unsupported affinity group" - {

      "must redirect the user to the unauthorised page" in {

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedAffinityGroup), appConfig, bodyParsers).apply()
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe controllers.routes.JourneyRecoveryController.onPageLoad().url //TODO change to controllers.routes.ThereIsAProblemController.onPageLoad().url
      }
    }

    "the user has an unsupported credential role" - {

      "must redirect the user to the unauthorised page" in {

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new UnsupportedCredentialRole), appConfig, bodyParsers).apply()
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe controllers.routes.JourneyRecoveryController.onPageLoad().url //TODO change to controllers.routes.ThereIsAProblemController.onPageLoad().url
      }
    }

    "must redirect the user to CBC file upload service when assistant with CBC credentials" in {
      val mockAuthConnector: AuthConnector = mock[AuthConnector]
      val cbcEnrolment                     = Enrolment(key = "HMRC-CBC-ORG")

      val retrieval: AuthRetrievals = Some("internalID") ~ Enrolments(Set(cbcEnrolment)) ~ None ~ Some(Assistant)
      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(retrieval)

      val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers).apply()
      val controller = new Harness(authAction)
      val result     = controller.onPageLoad()(FakeRequest())

      val expectedRedirectUrl = s"${appConfig.countryByCountryReportingFrontendUrl}"

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(expectedRedirectUrl)
    }

    "must redirect the user to unauthorised page when assistant" in {
      val mockAuthConnector: AuthConnector = mock[AuthConnector]
      val emptyEnrolments: Enrolment       = Enrolment(key = "")

      val retrieval: AuthRetrievals = Some("internalID") ~ Enrolments(Set(emptyEnrolments)) ~ None ~ Some(Assistant)
      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(retrieval)

      val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers).apply()
      val controller = new Harness(authAction)
      val result     = controller.onPageLoad()(FakeRequest())

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.JourneyRecoveryController.onPageLoad().url) //TODO change to Some(controllers.routes.UnauthorisedAssistantController.onPageLoad().url)
    }

    "must redirect the user to CBC file upload service when Individual with CBC credentials" in {
      val mockAuthConnector: AuthConnector = mock[AuthConnector]
      val cbcEnrolment                     = Enrolment(key = "HMRC-CBC-ORG")

      val retrieval: AuthRetrievals = Some("internalID") ~ Enrolments(Set(cbcEnrolment)) ~ Some(Individual) ~ None
      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(retrieval)

      val authAction     = new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers).apply()
      val enrolledAction = new CheckEnrolledToServiceActionProvider(appConfig).apply()
      val controller     = new Harness(authAction andThen enrolledAction)
      val result         = controller.onPageLoad()(FakeRequest())

      val expectedRedirectUrl = s"${appConfig.countryByCountryReportingFrontendUrl}"

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(expectedRedirectUrl)
    }

    "must redirect the user to CBC file upload service when Organisation with CBC credentials" in {
      val mockAuthConnector: AuthConnector = mock[AuthConnector]
      val cbcEnrolment                     = Enrolment(key = "HMRC-CBC-ORG")

      val retrieval: AuthRetrievals = Some("internalID") ~ Enrolments(Set(cbcEnrolment)) ~ Some(Organisation) ~ None
      when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())) thenReturn Future.successful(retrieval)

      val authAction     = new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers).apply()
      val enrolledAction = new CheckEnrolledToServiceActionProvider(appConfig).apply()
      val controller     = new Harness(authAction andThen enrolledAction)
      val result         = controller.onPageLoad()(FakeRequest())

      val expectedRedirectUrl = s"${appConfig.countryByCountryReportingFrontendUrl}"

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(expectedRedirectUrl)
    }
  }
}

class FakeFailingAuthConnector @Inject() (exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
