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

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.routes
import models.requests.IdentifierRequest
import play.api.Logging
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.{affinityGroup, credentialRole}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  config: FrontendAppConfig,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends IdentifierAction
    with AuthorisedFunctions
    with Logging {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val validKeys: Set[String] = Set(config.enrolmentKey, config.nonUkEnrolmentKey)

    authorised()
      .retrieve(Retrievals.internalId and Retrievals.allEnrolments and affinityGroup and credentialRole) {
        case _ ~ enrolments ~ _ ~ Some(Assistant)
            if enrolments.enrolments.exists(
              enrolment => validKeys.contains(enrolment.key)
            ) =>
          Future.successful(Redirect(config.countryByCountryReportingFrontendUrl))
        case _ ~ _ ~ Some(Agent) ~ _ =>
          Future.successful(Redirect(config.countryByCountryReportingFrontendUrl))
        case _ ~ _ ~ _ ~ Some(Assistant) =>
          Future.successful(Redirect(routes.UnauthorisedStandardUserController.onPageLoad()))
        case _ ~ _ ~ Some(Individual) ~ _ =>
          Future.successful(Redirect(routes.UnauthorisedIndividualController.onPageLoad()))
        case Some(internalID) ~ enrolments ~ _ ~ _ => block(IdentifierRequest(request, internalID, enrolments.enrolments))
        case _                                     => throw new UnauthorizedException("Unable to retrieve internal Id")
      }
      .recover {
        case _: NoActiveSession =>
          Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
        case _: AuthorisationException =>
          Redirect(controllers.routes.ThereIsAProblemController.onPageLoad())
      }
  }
}
