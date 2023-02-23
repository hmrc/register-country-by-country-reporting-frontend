/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.IsRegisteredAddressInUkFormProvider
import models.Mode
import navigation.CBCRNavigator
import pages.IsRegisteredAddressInUkPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IsRegisteredAddressInUkView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsRegisteredAddressInUkController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionRepository: SessionRepository,
                                         navigator: CBCRNavigator,
                                         standardActionSets: StandardActionSets,
                                         formProvider: IsRegisteredAddressInUkFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: IsRegisteredAddressInUkView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithInitializedData() {
    implicit request =>

      val preparedForm = request.userAnswers.get(IsRegisteredAddressInUkPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithInitializedData().async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IsRegisteredAddressInUkPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IsRegisteredAddressInUkPage, mode, updatedAnswers))
      )
  }
}
