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

import controllers.actions.*
import forms.IsThisYourBusinessFormProvider
import models.IdentifierType.UTR
import models.matching.{AutoMatchedRegistrationRequest, RegistrationInfo, RegistrationRequest}
import models.register.request.*
import models.requests.DataRequest
import models.{Mode, NotFoundError, UUIDGen, UniqueTaxpayerReference}
import navigation.CBCRNavigator
import pages.*
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.{BusinessMatchingWithIdService, SubscriptionService, TaxEnrolmentService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IsThisYourBusinessView

import java.time.Clock
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class IsThisYourBusinessController @Inject() (
  override val messagesApi: MessagesApi,
  override val sessionRepository: SessionRepository,
  override val subscriptionService: SubscriptionService,
  override val taxEnrolmentService: TaxEnrolmentService,
  navigator: CBCRNavigator,
  standardActionSets: StandardActionSets,
  matchingService: BusinessMatchingWithIdService,
  formProvider: IsThisYourBusinessFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IsThisYourBusinessView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with CreateSubscriptionAndUpdateEnrolment {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async { implicit request =>
    request.userAnswers
      .get(AutoMatchedUTRPage).map { autoMatchedUtr =>
        matchingService.sendBusinessRegistrationInformation(autoMatchedUtr, request.userAnswers).map{
          case Right(response) =>
            handleRegistrationFound(mode, autoMatchedUtr, response)
          case Left(NotFoundError) =>
           //not able to render the view and must go to businessType controller
        }
      }
      
      
      
      .getOrElse(view())
    {
      
      case _ =>
        Redirect(routes.ThereIsAProblemController.onPageLoad())
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
        value => Future.successful(BadRequest)
//          selfHealIfNecessary(value, mode)
      )
  }

}
