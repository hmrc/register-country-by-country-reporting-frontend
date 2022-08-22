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

import connectors.RegistrationConnector
import controllers.actions._
import forms.IsThisYourBusinessFormProvider
import models.matching.RegistrationInfo
import models.register.request._
import models.register.response.RegisterWithIDResponse
import models.requests.DataRequest
import models.{Mode, NotFoundError}
import navigation.CBCRNavigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.{SubscriptionService, TaxEnrolmentService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IsThisYourBusinessView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsThisYourBusinessController @Inject() (
  override val messagesApi: MessagesApi,
  override val sessionRepository: SessionRepository,
  override val subscriptionService: SubscriptionService,
  override val taxEnrolmentService: TaxEnrolmentService,
  navigator: CBCRNavigator,
  standardActionSets: StandardActionSets,
  registrationConnector: RegistrationConnector,
  formProvider: IsThisYourBusinessFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IsThisYourBusinessView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with CreateSubscriptionAndUpdateEnrolment {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async {
    implicit request =>
      buildRegistrationRequest() match {
        case Some(registerWithID) =>
          registrationConnector.registerWithID(registerWithID).flatMap {
            case Right(response) =>
              checkExistingSubscription(mode, response)
            case Left(NotFoundError) =>
              Future.successful(Redirect(routes.BusinessNotIdentifiedController.onPageLoad()))
            case _ =>
              Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
          }
        case _ =>
          Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
      }
  }

  private def checkExistingSubscription(mode: Mode,
                                        response: RegisterWithIDResponse
  )(implicit request: DataRequest[AnyContent], hc: HeaderCarrier, ec: ExecutionContext): Future[Result] = {
    for {
      ua                  <- Future.fromTry(request.userAnswers.set(RegistrationInfoPage, RegistrationInfo(response)))
      _                   <- sessionRepository.set(ua)
      mayBeSubscriptionId <- subscriptionService.getDisplaySubscriptionId(response.safeId)
    } yield mayBeSubscriptionId match {
      case Some(subscriptionID) => updateSubscriptionIdAndCreateEnrolment(response.safeId, subscriptionID)
      case _ =>
        val preparedForm = request.userAnswers.get(IsThisYourBusinessPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Future.successful(Ok(view(preparedForm, RegistrationInfo(response), mode)))
    }
  }.flatten

  private def buildRegistrationRequest()(implicit request: DataRequest[AnyContent]): Option[RegisterWithID] =
    for {
      utr          <- request.userAnswers.get(UTRPage)
      businessName <- request.userAnswers.get(BusinessNamePage)
      businessType <- request.userAnswers.get(BusinessTypePage)
    } yield RegisterWithID(
      RegisterWithIDRequest(
        RequestCommon("CBCR"),
        RequestWithIDDetails(
          "UTR",
          utr,
          requiresNameMatch = true,
          isAnAgent = false,
          WithIDOrganisation(businessName, businessType.code)
        )
      )
    )

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            request.userAnswers
              .get(RegistrationInfoPage)
              .fold {
                Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
              } {
                registrationInfo => Future.successful(BadRequest(view(formWithErrors, registrationInfo, mode)))
              },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(IsThisYourBusinessPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(IsThisYourBusinessPage, mode, updatedAnswers))
        )
  }
}
