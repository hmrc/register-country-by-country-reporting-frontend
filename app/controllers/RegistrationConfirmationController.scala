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

import controllers.actions._
import models.SubscriptionID
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.EmailService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.{RegistrationConfirmationView, ThereIsAProblemView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationConfirmationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       standardActionSets: StandardActionSets,
                                       sessionRepository: SessionRepository,
                                       emailService: EmailService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: RegistrationConfirmationView,
                                       errorView: ThereIsAProblemView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad: Action[AnyContent] = standardActionSets.identifiedWithoutEnrolmentCheck().async {
    implicit request =>
      val subscriptionId = Option(SubscriptionID("XTCBC0100000001"))
      subscriptionId match { //Todo: get the subscription ID correctly as per the line below
//      request.userAnswers.get(SubscriptionIDPage) match {
        case Some(id) =>
          emailService.sendEmail(request.userAnswers, id) flatMap {
            _ =>
              sessionRepository.clear(request.userId) map { _ =>
                Ok(view(id.value))
              }
          }
        case None =>
          logger.warn("SubscriptionIDPage: Subscription Id is missing")
          Future.successful(InternalServerError(errorView()))
      }
  }
}
