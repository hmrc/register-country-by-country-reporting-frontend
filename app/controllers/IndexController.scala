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

import controllers.actions.{IdentifierAction, StandardActionSets}
import models.{NormalMode, UserAnswers}
import pages.AutoMatchedUTRPage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ThereIsAProblemView

import java.time.{Clock, Instant}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject() (
                                  val controllerComponents: MessagesControllerComponents,
                                  sessionRepository: SessionRepository,
                                  clock: Clock,
                                  identify: IdentifierAction,
                                  standardActionSets: StandardActionSets,
                                  errorView: ThereIsAProblemView
                                )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(): Action[AnyContent] = standardActionSets.identifiedUserWithEnrolmentCheckAndCtUtrRetrieval().async {
    implicit request =>
      request.utr match {
        case Some(utr) =>
          val userAnswers = UserAnswers(request.userId, lastUpdated = Instant.now(clock))
          for {
            autoMatchedUserAnswers <- Future.fromTry(userAnswers.set(AutoMatchedUTRPage, utr))
            result <- sessionRepository.set(autoMatchedUserAnswers) map {
              case true =>
                Redirect(routes.IsThisYourBusinessController.onPageLoad(NormalMode))
              case false =>
                logger.error(s"Failed to update user answers with autoMatchedUTR field for userId: [${request.userId}]")
                InternalServerError(errorView())
            }
          } yield result
        case None => Future.successful(Redirect(routes.IsRegisteredAddressInUkController.onPageLoad(NormalMode)))
      }
  }
}
