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

import controllers.actions.StandardActionSets
import forms.DoYouHaveUTRFormProvider
import models.Mode
import navigation.CBCRNavigator
import pages.DoYouHaveUTRPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DoYouHaveUTRView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DoYouHaveUTRController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: CBCRNavigator,
  standardActionSets: StandardActionSets,
  formProvider: DoYouHaveUTRFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DoYouHaveUTRView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] =
    standardActionSets.identifiedUserWithInitializedData() {
      implicit request =>
        val preparedForm = request.userAnswers.get(DoYouHaveUTRPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mode))
    }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithInitializedData().async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            if (request.userAnswers.hasNewValue(DoYouHaveUTRPage, value)) {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(DoYouHaveUTRPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(DoYouHaveUTRPage, mode, updatedAnswers))
            } else {
              Future.successful(Redirect(navigator.nextPage(DoYouHaveUTRPage, mode, request.userAnswers)))
            }
        )
  }
}
