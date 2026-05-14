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

import cats.data.OptionT.{fromOption, liftF}
import controllers.actions.*
import forms.IsThisYourBusinessFormProvider
import models.{Mode, NotFoundError}
import models.matching.RegistrationInfo
import models.register.request.*
import navigation.CBCRNavigator
import pages.*
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.BusinessMatchingWithIdService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IsThisYourBusinessView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsThisYourBusinessController @Inject() (
  override val messagesApi: MessagesApi,
  val sessionRepository: SessionRepository,
  navigator: CBCRNavigator,
  standardActionSets: StandardActionSets,
  matchingService: BusinessMatchingWithIdService,
  formProvider: IsThisYourBusinessFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IsThisYourBusinessView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async { implicit request =>
    (for {
      futureRegInfo <- fromOption[Future](
        request.userAnswers
          .get(RegistrationInfoPage)
          .map(Future.successful)
          .orElse(matchingService.buildRegistrationRequest(request.userAnswers).map(matchingService.sendBusinessRegistrationInformation))
      )
      regInfo     <- liftF(futureRegInfo)
      userAnswers <- liftF(Future.fromTry(request.userAnswers.set(RegistrationInfoPage, regInfo)))
      _           <- liftF(sessionRepository.set(userAnswers))
    } yield {
      val preparedForm = request.userAnswers.get(IsThisYourBusinessPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, regInfo, mode))
    })
      .getOrElse(Redirect(controllers.routes.ThereIsAProblemController.onPageLoad()))
      .recover {
        case NotFoundError =>
          Redirect(controllers.routes.BusinessNotIdentifiedController.onPageLoad())
        case _ =>
          Redirect(controllers.routes.ThereIsAProblemController.onPageLoad())
      }

  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async { implicit request =>
    val thereIsAProblem = Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
    form
      .bindFromRequest()
      .fold(
        formWithErrors =>
          request.userAnswers
            .get(RegistrationInfoPage)
            .fold(thereIsAProblem) { case registrationInfo: RegistrationInfo =>
              Future.successful(BadRequest(view(formWithErrors, registrationInfo, mode)))
            },
        value =>
          (for {
            userAnswers <- Future.fromTry(request.userAnswers.set(IsThisYourBusinessPage, value))
            _           <- sessionRepository.set(userAnswers)
          } yield
            if !value then Future.successful(Redirect(navigator.nextPage(IsThisYourBusinessPage, mode, userAnswers)))
            else matchingService.selfHealingLogic()).flatten
      )
  }

}
