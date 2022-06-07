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

package navigation

import controllers.routes
import models._
import pages._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

// @formatter:off
@Singleton
class CBCRNavigator @Inject()() extends Navigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case DoYouHaveUTRPage   => ua => yesNoPage(
      ua,
      DoYouHaveUTRPage,
      routes.BusinessTypeController.onPageLoad(NormalMode),
      routes.BusinessWithoutIDNameController.onPageLoad(NormalMode)
    )
    case BusinessTypePage   => _ => routes.BusinessNameController.onPageLoad(NormalMode)
    case BusinessNamePage   => _ => routes.BusinessNameController.onPageLoad(NormalMode) //TODO change when next pages are implemented
    case BusinessWithoutIDNamePage   => _ => routes.BusinessHaveDifferentNameController.onPageLoad(NormalMode)
    case WhatIsTradingNamePage   => _ => routes.WhatIsTradingNameController.onPageLoad(NormalMode) //TODO change when next pages are implemented
    case ContactNamePage   => _ => routes.ContactNameController.onPageLoad(NormalMode) //TODO change when next pages are implemented
    case BusinessHaveDifferentNamePage   => ua => yesNoPage(
      ua,
      BusinessHaveDifferentNamePage,
      routes.BusinessHaveDifferentNameController.onPageLoad(NormalMode), //TODO change when next pages are implemented
      routes.BusinessWithoutIdAddressController.onPageLoad(NormalMode)
    )
    case BusinessWithoutIdAddressPage   => _ => routes.BusinessWithoutIdAddressController.onPageLoad(NormalMode) //TODO change when next pages are implemented
  }

  override val checkRouteMap: Page => UserAnswers => Call = {
    case _  => _ => controllers.routes.CheckYourAnswersController.onPageLoad
  }

  def yesNoPage(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call =
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(routes.JourneyRecoveryController.onPageLoad()) //TODO: Change to routes.ThereIsAProblemController.onPageLoad() when implemented
}
