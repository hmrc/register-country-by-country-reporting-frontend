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
import forms.SecondContactEmailFormProvider
import models.{Mode, UserAnswers}
import navigation.CBCRNavigator
import pages.{SecondContactEmailPage, SecondContactNamePage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SecondContactEmailView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SecondContactEmailController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: CBCRNavigator,
  standardActionSets: StandardActionSets,
  formProvider: SecondContactEmailFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: SecondContactEmailView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithInitializedData() {
    implicit request =>
      val preparedForm = request.userAnswers.get(SecondContactEmailPage).fold(form)(form.fill)

      Ok(view(preparedForm, mode, getSecondContactName(request.userAnswers)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithInitializedData().async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, getSecondContactName(request.userAnswers)))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(SecondContactEmailPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(SecondContactEmailPage, mode, updatedAnswers))
        )
  }

  private def getSecondContactName(ua: UserAnswers)(implicit messages: Messages): String =
    ua.get(SecondContactNamePage).getOrElse(messages("default.secondContact.name"))
}
