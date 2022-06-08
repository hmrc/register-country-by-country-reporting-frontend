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
import forms.SecondContactHavePhoneFormProvider
import models.{Mode, UserAnswers}
import navigation.CBCRNavigator
import pages.{SecondContactHavePhonePage, SecondContactNamePage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SecondContactHavePhoneView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SecondContactHavePhoneController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  sessionRepository: SessionRepository,
                                                  navigator: CBCRNavigator,
                                                  standardActionSets: StandardActionSets,
                                                  formProvider: SecondContactHavePhoneFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: SecondContactHavePhoneView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData() {
    implicit request =>

      val preparedForm = request.userAnswers.get(SecondContactHavePhonePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, getSecondContactName(request.userAnswers), mode))
  }

  private def getSecondContactName(userAnswers: UserAnswers)(implicit messages: Messages): String =
    (userAnswers.get(SecondContactNamePage)) match {
      case Some(contactName) => contactName
      case _                 => messages("default.secondContact.name")
    }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, getSecondContactName(request.userAnswers), mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SecondContactHavePhonePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SecondContactHavePhonePage, mode, updatedAnswers))
      )
  }
}
