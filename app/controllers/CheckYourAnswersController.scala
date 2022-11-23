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
import models.MandatoryInformationMissingError
import pages.RegistrationInfoPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.{RegisterWithoutIdService, SubscriptionService, TaxEnrolmentService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{CheckYourAnswersHelper, CountryListFactory}
import viewmodels.govuk.summarylist._
import views.html.CheckYourAnswersView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  standardActionSets: StandardActionSets,
  val controllerComponents: MessagesControllerComponents,
  override val sessionRepository: SessionRepository,
  override val subscriptionService: SubscriptionService,
  override val taxEnrolmentService: TaxEnrolmentService,
  registerWithoutIdService: RegisterWithoutIdService,
  view: CheckYourAnswersView,
  countryListFactory: CountryListFactory
) extends FrontendBaseController
    with I18nSupport
    with CreateSubscriptionAndUpdateEnrolment {

  def onPageLoad(): Action[AnyContent] = standardActionSets.identifiedUserWithData() {
    implicit request =>
      val checkYourAnswersHelper = new CheckYourAnswersHelper(userAnswers = request.userAnswers, countryListFactory = countryListFactory)

      val businessList      = SummaryListViewModel(checkYourAnswersHelper.businessSection)
      val firstContactList  = SummaryListViewModel(checkYourAnswersHelper.firstContactSection)
      val secondContactList = SummaryListViewModel(checkYourAnswersHelper.secondContactSection)

      Ok(view(businessList, firstContactList, secondContactList))
  }

  def onSubmit(): Action[AnyContent] = standardActionSets.identifiedUserWithData().async {
    implicit request =>
      request.userAnswers.get(RegistrationInfoPage).map(_.safeId) match {
        case Some(safeId) =>
          createSubscription(safeId)
        case _ =>
          registerWithoutIdService.registerWithoutId().flatMap {
            case Right(safeId) => createSubscription(safeId)
            case Left(value) =>
              logger.warn(s"Error $value")
              value match {
                case MandatoryInformationMissingError(_) =>  Future.successful(Redirect(routes.MissingInformationController.onPageLoad()))
                case _ =>  Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
              }
          }
      }
  }

}
