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

import com.google.inject.Inject
import controllers.actions.StandardActionSets
import models.requests.DataRequest
import models.{SafeId, SubscriptionID}
import pages.{RegistrationInfoPage, SubscriptionIDPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.{SubscriptionService, TaxEnrolmentService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{CheckYourAnswersHelper, CountryListFactory}
import viewmodels.govuk.summarylist._
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            standardActionSets: StandardActionSets,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView,
                                            countryListFactory: CountryListFactory
                                          ) extends FrontendBaseController with I18nSupport {
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  standardActionSets: StandardActionSets,
  val controllerComponents: MessagesControllerComponents,
  sessionRepository: SessionRepository,
  subscriptionService: SubscriptionService,
  taxEnrolmentService: TaxEnrolmentService,
  view: CheckYourAnswersView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = standardActionSets.identifiedUserWithData() {
    implicit request =>

      val checkYourAnswersHelper =  new CheckYourAnswersHelper(userAnswers = request.userAnswers, countryListFactory = countryListFactory)

      val businessList = SummaryListViewModel(checkYourAnswersHelper.businessSection)
      val firstContactList = SummaryListViewModel(checkYourAnswersHelper.firstContactSection)
      val secondContactList = SummaryListViewModel(checkYourAnswersHelper.secondContactSection)

      Ok(view(businessList, firstContactList, secondContactList))
  }



  def onSubmit(): Action[AnyContent] = standardActionSets.identifiedUserWithData().async {
    implicit request =>
      request.userAnswers.get(RegistrationInfoPage).map(_.safeId) match {
        case Some(safeId) =>
          subscriptionService.checkAndCreateSubscription(safeId, request.userAnswers) flatMap {
            case Right(subscriptionId) => updateSubscriptionIdAndCreateEnrolment(safeId, subscriptionId)
            case Left(error)           => Future.successful(NotImplemented)
          }
        case _ => Future.successful(NotImplemented)
      }
  }

  def updateSubscriptionIdAndCreateEnrolment(safeId: SafeId, subscriptionId: SubscriptionID)(implicit
    hc: HeaderCarrier,
    request: DataRequest[AnyContent]
  ): Future[Result] = {
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(SubscriptionIDPage, subscriptionId))
      _              <- sessionRepository.set(updatedAnswers)
    } yield updatedAnswers
  }.flatMap {
    updatedAnswers =>
      taxEnrolmentService.checkAndCreateEnrolment(safeId, updatedAnswers, subscriptionId) flatMap {
        case Right(_) => Future.successful(Redirect(routes.RegistrationConfirmationController.onPageLoad()))
        case Left(_) =>
          if (request.userAnswers.get(RegistrationInfoPage).isDefined) {
            Future.successful(Redirect(routes.PreRegisteredController.onPageLoad()))
          } else {
            Future.successful(Redirect(routes.PreRegisteredController.onPageLoad()))
          }
      }
  }
}
