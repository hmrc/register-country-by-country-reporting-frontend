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

import config.FrontendAppConfig
import controllers.actions._
import models.BusinessType.{LimitedCompany, UnincorporatedAssociation}
import models.NormalMode
import pages.BusinessTypePage

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.BusinessNotIdentifiedView

class BusinessNotIdentifiedController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       standardActionSets: StandardActionSets,
                                       val controllerComponents: MessagesControllerComponents,
                                       appConfig: FrontendAppConfig,
                                       view: BusinessNotIdentifiedView
                                     ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = standardActionSets.identifiedUserWithData() {
    implicit request =>
      val startUrl = routes.DoYouHaveUTRController.onPageLoad(NormalMode).url

      val contactUrl: String = request.userAnswers.get(BusinessTypePage) match {
        case Some(LimitedCompany) | Some(UnincorporatedAssociation) => appConfig.corporationTaxEnquiriesLink
        case _                                                      => appConfig.selfAssessmentEnquiriesLink
      }
      Ok(view(contactUrl,startUrl))
  }
}
