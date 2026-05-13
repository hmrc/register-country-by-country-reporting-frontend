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
import models.register.request.RegisterWithID
import models.requests.DataRequest
import models.{ApiError, Mode, NotFoundError, UUIDGen, UniqueTaxpayerReference, UserAnswers}
import navigation.CBCRNavigator
import pages.*
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.Redirect
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
  view: IsThisYourBusinessView,
  uuidGen: UUIDGen,
  clock: Clock
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with CreateSubscriptionAndUpdateEnrolment {

  val form = formProvider()

  implicit private val uuidGenerator: UUIDGen = uuidGen
  implicit private val implicitClock: Clock   = clock

  private val thereIsAProblem: Result = Redirect(routes.ThereIsAProblemController.onPageLoad())

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async { implicit request =>
    val autoMatchedUtr = request.userAnswers.get(AutoMatchedUTRPage)
    buildRegisterWithId(autoMatchedUtr)
      .fold(Future.successful(thereIsAProblem)) { registerWithId =>
        matchingService
          .sendBusinessRegistrationInformation(registerWithId)
          .flatMap(handleRegistrationInfoResult(request.userAnswers, mode, autoMatchedUtr, _))
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
        value => selfHealIfNecessary(value, mode)
      )
  }

  private def handleRegistrationInfoResult(userAnswers: UserAnswers,
                                           mode: Mode,
                                           autoMatchedUtr: Option[UniqueTaxpayerReference],
                                           response: Either[ApiError, RegistrationInfo]
  )(implicit request: DataRequest[AnyContent]): Future[Result] =
    response match {
      case Right(registrationInfo) =>
        handleRegistrationFound(userAnswers, mode, autoMatchedUtr, registrationInfo)
      case Left(NotFoundError) =>
        handleRegistrationNotFound(userAnswers, mode, autoMatchedUtr)
      case _ =>
        Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
    }

  def handleRegistrationFound(userAnswers: UserAnswers, mode: Mode, autoMatchedUtr: Option[UniqueTaxpayerReference], registrationInfo: RegistrationInfo)(
    implicit request: DataRequest[AnyContent]
  ): Future[Result] = {
    val updatedAnswersWithUtrPage = autoMatchedUtr.map(userAnswers.set(UTRPage, _)).getOrElse(Success(userAnswers))
    for {
      updatedAnswers <- Future.fromTry(updatedAnswersWithUtrPage.flatMap(_.set(RegistrationInfoPage, registrationInfo)))
      _              <- sessionRepository.set(updatedAnswers)
    } yield {
      val preparedForm = userAnswers.get(IsThisYourBusinessPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, registrationInfo, mode))
    }
  }

  private def handleRegistrationNotFound(userAnswers: UserAnswers, mode: Mode, autoMatchedUtr: Option[UniqueTaxpayerReference]): Future[Result] =
    autoMatchedUtr.fold(Future.successful(Redirect(routes.BusinessNotIdentifiedController.onPageLoad()))) { _ =>
      for {
        autoMatchedUtrRemoved <- Future.fromTry(userAnswers.remove(AutoMatchedUTRPage))
        result                <- sessionRepository.set(autoMatchedUtrRemoved)
      } yield Redirect(routes.BusinessTypeController.onPageLoad(mode))
    }

  private def selfHealIfNecessary(value: Boolean, mode: Mode)(implicit request: DataRequest[AnyContent]): Future[Result] =
    if (!value) {
      proceedToNextPage(value, request, mode)
    } else {
      request.userAnswers
        .get(RegistrationInfoPage)
        .fold {
          logger.error(s"Registration info not found in user answers when user answered yes to 'is this your business question'")
          Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
        } { registrationInfo =>
          subscriptionService.getDisplaySubscriptionId(registrationInfo.safeId) flatMap {
            case Some(subscriptionId) =>
              updateSubscriptionIdAndCreateEnrolment(registrationInfo.safeId, subscriptionId)
            case _ =>
              proceedToNextPage(value, request, mode)
          }
        }
    }

  private def proceedToNextPage(value: Boolean, request: DataRequest[AnyContent], mode: Mode): Future[Result] =
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(IsThisYourBusinessPage, value))
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(IsThisYourBusinessPage, mode, updatedAnswers))

  private def buildRegistrationRequest()(implicit request: DataRequest[AnyContent]): Option[RegisterWithID] =
    for {
      utr          <- request.userAnswers.get(UTRPage)
      businessName <- request.userAnswers.get(BusinessNamePage)
      businessType = request.userAnswers.get(BusinessTypePage)
    } yield RegisterWithID(RegistrationRequest(UTR, utr.uniqueTaxPayerReference, businessName, businessType, None))

  private def buildAutoMatchedBusinessRegistrationRequest(utr: UniqueTaxpayerReference): RegisterWithID =
    RegisterWithID(AutoMatchedRegistrationRequest(UTR, utr.uniqueTaxPayerReference))

  private def buildRegisterWithId(autoMatchedUtr: Option[UniqueTaxpayerReference])(implicit request: DataRequest[AnyContent]): Option[RegisterWithID] =
    autoMatchedUtr
      .map(buildAutoMatchedBusinessRegistrationRequest)
      .orElse(buildRegistrationRequest())
}
