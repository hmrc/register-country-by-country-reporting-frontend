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

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.EmailService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.RegistrationConfirmationView

import scala.concurrent.ExecutionContext

class RegistrationConfirmationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       standardActionSets: StandardActionSets,
                                       sessionRepository: SessionRepository,
                                       emailService: EmailService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: RegistrationConfirmationView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = standardActionSets.identifiedWithoutEnrolmentCheck().async {
    implicit request =>
      val subscriptionId = SubscriptionID("XTCBC0100000001") //Todo: get the subscription ID correctly
      emailService.sendEmail(request.userAnswers, subscriptionId) flatMap {
        _ =>
          sessionRepository.clear(request.userId) map { _ =>
            Ok(view(subscriptionId.value))
          }
      }
  }
}
