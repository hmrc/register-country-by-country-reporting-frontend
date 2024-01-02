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

import controllers.actions._
import forms.UTRFormProvider
import models.BusinessType.{LimitedCompany, UnincorporatedAssociation}
import models.{Mode, UserAnswers}
import navigation.CBCRNavigator
import pages.{BusinessTypePage, UTRPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.UTRView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UTRController @Inject()(
                               override val messagesApi: MessagesApi,
                               sessionRepository: SessionRepository,
                               navigator: CBCRNavigator,
                               standardActionSets: StandardActionSets,
                               formProvider: UTRFormProvider,
                               val controllerComponents: MessagesControllerComponents,
                               view: UTRView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithDependantAnswer(BusinessTypePage).async {
    implicit request =>
      val taxType = getTaxType(request.userAnswers)
      val form = formProvider(taxType)

      val preparedForm = request.userAnswers.get(UTRPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Future.successful(Ok(view(preparedForm, mode, taxType)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithDependantAnswer(BusinessTypePage).async {
    implicit request =>
      val taxType = getTaxType(request.userAnswers)
      val form = formProvider(taxType)

      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, taxType))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(UTRPage, value))
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(UTRPage, mode, updatedAnswers))
        )
  }

  private def getTaxType(userAnswers: UserAnswers)(implicit messages: Messages): String =
    userAnswers.get(BusinessTypePage) match {
      case Some(LimitedCompany) | Some(UnincorporatedAssociation) => messages("utr.corporationTax")
      case _ => messages("utr.selfAssessment")
    }
}
