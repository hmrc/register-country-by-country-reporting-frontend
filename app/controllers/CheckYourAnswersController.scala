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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
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

  def onPageLoad(): Action[AnyContent] = standardActionSets.identifiedUserWithData() {
    implicit request =>

      val checkYourAnswersHelper =  new CheckYourAnswersHelper(userAnswers = request.userAnswers, countryListFactory = countryListFactory)

      val businessList = SummaryListViewModel(checkYourAnswersHelper.businessSection).withCssClass("govuk-!-margin-bottom-0")
      val firstContactList = SummaryListViewModel(checkYourAnswersHelper.firstContactSection).withCssClass("govuk-!-margin-bottom-0")
      val secondContactList = SummaryListViewModel(checkYourAnswersHelper.secondContactSection).withCssClass("govuk-!-margin-bottom-0")



      Ok(view(businessList, firstContactList, secondContactList))
  }


  def onSubmit(): Action[AnyContent] = standardActionSets.identifiedUserWithData().async {
    implicit request =>
      Future.successful(Ok("Ok")) //ToDo submit registration
  }
}
