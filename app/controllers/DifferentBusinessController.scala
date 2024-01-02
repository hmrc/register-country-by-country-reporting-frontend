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

import config.FrontendAppConfig
import controllers.actions._
import models.matching.RegistrationInfo
import pages.RegistrationInfoPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.{DifferentBusinessView, ThereIsAProblemView}

import javax.inject.Inject

class DifferentBusinessController @Inject() (
                                              override val messagesApi: MessagesApi,
                                              standardActionSets: StandardActionSets,
                                              val controllerComponents: MessagesControllerComponents,
                                              appConfig: FrontendAppConfig,
                                              view: DifferentBusinessView,
                                              errorView: ThereIsAProblemView
                                            ) extends FrontendBaseController
  with I18nSupport {

  def onPageLoad: Action[AnyContent] = standardActionSets.identifiedUserWithData() {
    implicit request =>
      request.userAnswers.get(RegistrationInfoPage) match {
        case Some(registrationInfo: RegistrationInfo) =>
          val (name, address) = (Some(registrationInfo.name), Some(registrationInfo.address.asList))
          Ok(view(appConfig.loginUrl, name, address))
        case _ => Ok(view(appConfig.loginUrl, None, None))
      }

  }
}
