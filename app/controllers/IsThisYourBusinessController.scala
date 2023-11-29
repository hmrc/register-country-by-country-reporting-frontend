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

import connectors.RegistrationConnector
import controllers.actions._
import forms.IsThisYourBusinessFormProvider
import models.IdentifierType.UTR
import models.matching.{AutoMatchedRegistrationRequest, RegistrationInfo, RegistrationRequest}
import models.register.request._
import models.register.response.RegisterWithIDResponse
import models.requests.DataRequest
import models.{Mode, NotFoundError, UUIDGen, UniqueTaxpayerReference}
import navigation.CBCRNavigator
import pages._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.{BusinessMatchingWithIdService, RegisterWithoutIdService, SubscriptionService, TaxEnrolmentService}
import uk.gov.hmrc.http.HeaderCarrier
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
  registrationConnector: RegistrationConnector,
  matchingService: BusinessMatchingWithIdService,
  formProvider: IsThisYourBusinessFormProvider,
  val controllerComponents: MessagesControllerComponents,
  controllerHelper: CreateSubscriptionAndUpdateEnrolment,
  uuidGen: UUIDGen,
  clock: Clock,
  view: IsThisYourBusinessView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with CreateSubscriptionAndUpdateEnrolment {

  val form = formProvider()

  implicit private val uuidGenerator: UUIDGen = uuidGen
  implicit private val implicitClock: Clock = clock
  private def result(mode: Mode, form: Form[Boolean], registrationInfo: RegistrationInfo)(implicit
                                                                                             ec: ExecutionContext,
                                                                                             request: DataRequest[AnyContent]
  ): Future[Result] =
    subscriptionService.getDisplaySubscriptionId(registrationInfo.safeId) flatMap {
      case Some(subscriptionId) => controllerHelper.updateSubscriptionIdAndCreateEnrolment(registrationInfo.safeId, subscriptionId)
      case _ =>
        val preparedForm = request.userAnswers.get(IsThisYourBusinessPage) match {
          case None => form
          case Some(value) => form.fill(value)
        }
        Future.successful(Ok(view(preparedForm, registrationInfo, mode)))
    }

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async {
    implicit request =>
      val autoMatchedUtr = request.userAnswers.get(AutoMatchedUTRPage)
      buildRegisterWithId(autoMatchedUtr) match {
        case Some(registerWithID) =>
          matchingService.sendBusinessRegistrationInformation(registerWithID).flatMap {
            case Right(response) =>
              handleRegistrationFound(mode, autoMatchedUtr, response)
            case Left(NotFoundError) =>
              handleRegistrationNotFound(mode, autoMatchedUtr)
            case _ =>
              Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
          }
        case _ =>
          Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
      }
  }

  private def handleRegistrationFound(
                                       mode: Mode,
                                       autoMatchedUtr: Option[UniqueTaxpayerReference],
                                       registrationInfo: RegistrationInfo
                                     )(implicit request: DataRequest[AnyContent]): Future[Result] = {
    val updatedAnswersWithUtrPage = autoMatchedUtr.map(request.userAnswers.set(UTRPage, _)).getOrElse(Success(request.userAnswers))
    for {
      updatedAnswers <- Future.fromTry(updatedAnswersWithUtrPage.flatMap(_.set(RegistrationInfoPage, registrationInfo)))
      updatedRequest = DataRequest(request.request, request.userId, updatedAnswers)
      result <- sessionRepository.set(updatedAnswers).flatMap {
        case true => result(mode, form, registrationInfo)(ec, updatedRequest)
        case false =>
          logger.error(s"Failed to update user answers after registration was found for userId: [${request.userId}]")
          Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
      }
    } yield result
  }

  private def handleRegistrationNotFound(
                                          mode: Mode,
                                          autoMatchedUtr: Option[UniqueTaxpayerReference]
                                        )(implicit request: DataRequest[AnyContent]): Future[Result] =
    if (autoMatchedUtr.nonEmpty) {
      resultWithAutoMatchedFieldCleared(mode)
    } else {
      Future.successful(Redirect(routes.BusinessNotIdentifiedController.onPageLoad()))
    }

  private def resultWithAutoMatchedFieldCleared(mode: Mode)(implicit request: DataRequest[AnyContent]): Future[Result] =
    for {
      autoMatchedUtrRemoved <- Future.fromTry(request.userAnswers.remove(AutoMatchedUTRPage))
      result <- sessionRepository.set(autoMatchedUtrRemoved) flatMap {
        case true => Future.successful(Redirect(routes.BusinessTypeController.onPageLoad(mode)))
        case false =>
          logger.error(s"Failed to clear autoMatchedUTR field from user answers for userId: [${request.userId}]")
          Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
      }
    } yield result

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

  def buildRegistrationRequest()(implicit request: DataRequest[AnyContent]): Option[RegisterWithID] =
    for {
      utr <- request.userAnswers.get(UTRPage)
      businessName <- request.userAnswers.get(BusinessNamePage)
      businessType = request.userAnswers.get(BusinessTypePage)
    } yield RegisterWithID(RegistrationRequest(UTR, utr.uniqueTaxPayerReference, businessName, businessType, None))

  def buildAutoMatchedBusinessRegistrationRequest(utr: UniqueTaxpayerReference): Option[RegisterWithID] =
    Option(RegisterWithID(AutoMatchedRegistrationRequest(UTR, utr.uniqueTaxPayerReference)))

  private def buildRegisterWithId(autoMatchedUtr: Option[UniqueTaxpayerReference])(implicit request: DataRequest[AnyContent]): Option[RegisterWithID] =
        autoMatchedUtr match {
          case Some(utr) => buildAutoMatchedBusinessRegistrationRequest(utr)
          case None => buildRegistrationRequest()
        }

def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async {
  implicit request =>
    val thereIsAProblem = Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
    form
      .bindFromRequest()
      .fold(
        formWithErrors =>
          request.userAnswers
            .get(RegistrationInfoPage)
            .fold(thereIsAProblem) {
              case registrationInfo: RegistrationInfo =>
                Future.successful(BadRequest(view(formWithErrors, registrationInfo, mode)))
              case _ => thereIsAProblem
            },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IsThisYourBusinessPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(IsThisYourBusinessPage, mode, updatedAnswers))
      )
}
}
