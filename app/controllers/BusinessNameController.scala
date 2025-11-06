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
import forms.BusinessNameFormProvider
import models.Mode
import navigation.CBCRNavigator
import pages.{BusinessNamePage, BusinessTypePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.BusinessNameView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessNameController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: CBCRNavigator,
  standardActionSets: StandardActionSets,
  formProvider: BusinessNameFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: BusinessNameView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData() {
    implicit request =>
      request.userAnswers
        .get(BusinessTypePage)
        .map {
          businessType =>
            val preparedForm = request.userAnswers.get(BusinessNamePage) match {
              case None        => formProvider(businessType)
              case Some(value) => formProvider(businessType).fill(value)
            }

            Ok(view(preparedForm, businessType, mode))
        }
        .getOrElse(Redirect(routes.ThereIsAProblemController.onPageLoad()))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async {
    implicit request =>
      request.userAnswers
        .get(BusinessTypePage)
        .map {
          businessType =>
            formProvider(businessType)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, businessType, mode))),
                value =>
                  if (request.userAnswers.hasNewValue(BusinessNamePage, value)) {
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.set(BusinessNamePage, value))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(BusinessNamePage, mode, updatedAnswers))
                  } else {
                    Future.successful(Redirect(navigator.nextPage(BusinessNamePage, mode, request.userAnswers)))
                  }
              )
        }
        .getOrElse(Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad())))
  }
}
