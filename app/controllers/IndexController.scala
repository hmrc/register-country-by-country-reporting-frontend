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

import config.FrontendAppConfig
import controllers.actions.StandardActionSets
import models.{InternalProblemError, NormalMode, NotFoundError, UserAnswers}
import pages.{AutoMatchedUTRPage, PrivateBetaAccessCodePage, RegistrationInfoPage}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.BusinessMatchingWithIdService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ThereIsAProblemView

import java.time.{Clock, Instant}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  sessionRepository: SessionRepository,
  clock: Clock,
  matchingService: BusinessMatchingWithIdService,
  standardActionSets: StandardActionSets,
  errorView: ThereIsAProblemView
)(implicit ec: ExecutionContext, config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(): Action[AnyContent] = standardActionSets.identifiedUserWithEnrolmentCheckAndCtUtrRetrieval().async { implicit request =>
    request.utr
      .map { utrFromCtEnrolment =>
        val userAnswers = UserAnswers(request.userId, lastUpdated = Instant.now(clock))
      (for {
        ua  <- Future.fromTry(userAnswers.set(PrivateBetaAccessCodePage, config.privateBetaPassword))
        ua2 <- Future.fromTry(ua.set(AutoMatchedUTRPage, utrFromCtEnrolment))
        registrationPayload = matchingService.buildRegisterWithIdForAutoMatched(utrFromCtEnrolment)
        registrationData <- matchingService.sendBusinessRegistrationInformation(registrationPayload)
        ua3              <- Future.fromTry(ua2.set(RegistrationInfoPage, registrationData))
        _                <- sessionRepository.set(ua3)
      } yield Redirect(routes.IsThisYourBusinessController.onPageLoad(NormalMode)))
        .recover { case InternalProblemError | NotFoundError =>
          Redirect(routes.IsRegisteredAddressInUkController.onPageLoad(NormalMode))
        }
      }
      .getOrElse(Future.successful(Redirect(routes.IsRegisteredAddressInUkController.onPageLoad(NormalMode))))
  }
}
