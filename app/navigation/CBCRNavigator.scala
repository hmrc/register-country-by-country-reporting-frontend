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
    case IsRegisteredAddressInUkPage => ua =>
      yesNoPage(
        ua,
        IsRegisteredAddressInUkPage,
        routes.BusinessTypeController.onPageLoad(NormalMode),
        routes.DoYouHaveUTRController.onPageLoad(NormalMode)
      )
    case DoYouHaveUTRPage   => ua => yesNoPage(
      ua,
      DoYouHaveUTRPage,
      routes.BusinessTypeController.onPageLoad(NormalMode),
      routes.BusinessWithoutIDNameController.onPageLoad(NormalMode)
    )
    case BusinessTypePage   => _ => routes.UTRController.onPageLoad(NormalMode)
    case UTRPage   => _ => routes.BusinessNameController.onPageLoad(NormalMode)
    case BusinessNamePage   => _ => routes.IsThisYourBusinessController.onPageLoad(NormalMode)
    case IsThisYourBusinessPage   => ua => yesNoPage(
      ua,
      IsThisYourBusinessPage,
      routes.YourContactDetailsController.onPageLoad(NormalMode),
      routes.BusinessNotIdentifiedController.onPageLoad()
    )
    case BusinessWithoutIDNamePage   => _ => routes.BusinessHaveDifferentNameController.onPageLoad(NormalMode)
    case BusinessHaveDifferentNamePage   => ua => yesNoPage(
      ua,
      BusinessHaveDifferentNamePage,
      routes.WhatIsTradingNameController.onPageLoad(NormalMode),
      routes.BusinessWithoutIdAddressController.onPageLoad(NormalMode)
    )
    case WhatIsTradingNamePage   => _ => routes.BusinessWithoutIdAddressController.onPageLoad(NormalMode)
    case BusinessWithoutIdAddressPage   => _ => routes.YourContactDetailsController.onPageLoad(NormalMode)
    case ContactNamePage   => _ => routes.ContactEmailController.onPageLoad(NormalMode)
    case ContactEmailPage   => _ => routes.HaveTelephoneController.onPageLoad(NormalMode)
    case HaveTelephonePage   => ua => yesNoPage(
      ua,
      HaveTelephonePage,
      routes.ContactPhoneController.onPageLoad(NormalMode),
      routes.DoYouHaveSecondContactController.onPageLoad(NormalMode)
    )
    case ContactPhonePage   => _ => routes.DoYouHaveSecondContactController.onPageLoad(NormalMode)
    case DoYouHaveSecondContactPage   => ua => yesNoPage(
      ua,
      DoYouHaveSecondContactPage,
      routes.SecondContactNameController.onPageLoad(NormalMode),
      routes.CheckYourAnswersController.onPageLoad()
    )
    case SecondContactNamePage   => _ => routes.SecondContactEmailController.onPageLoad(NormalMode)
    case SecondContactEmailPage   => _ => routes.SecondContactHavePhoneController.onPageLoad(NormalMode)
    case SecondContactHavePhonePage   => ua => yesNoPage(
      ua,
      SecondContactHavePhonePage,
      routes.SecondContactPhoneController.onPageLoad(NormalMode),
      routes.CheckYourAnswersController.onPageLoad()
    )
    case SecondContactPhonePage => _ => routes.CheckYourAnswersController.onPageLoad()
  }

  override val checkRouteMap: Page => UserAnswers => Call = {
    case IsRegisteredAddressInUkPage => ua =>
      yesNoPage(
        ua,
        IsRegisteredAddressInUkPage,
        routes.BusinessTypeController.onPageLoad(CheckMode),
        routes.DoYouHaveUTRController.onPageLoad(CheckMode)
      )
    case DoYouHaveUTRPage   => ua => yesNoPage(
      ua,
      DoYouHaveUTRPage,
      routes.BusinessTypeController.onPageLoad(CheckMode),
      routes.BusinessWithoutIDNameController.onPageLoad(CheckMode)
    )
    case BusinessWithoutIDNamePage  => ua =>
      checkNextPageForValueThenRoute(
        CheckMode, ua, BusinessHaveDifferentNamePage, routes.BusinessHaveDifferentNameController.onPageLoad(CheckMode)
      )
    case BusinessHaveDifferentNamePage   => ua => yesNoPage(
      ua,
      BusinessHaveDifferentNamePage,
      routes.WhatIsTradingNameController.onPageLoad(CheckMode),
      checkNextPageForValueThenRoute(CheckMode, ua, BusinessWithoutIdAddressPage, routes.BusinessWithoutIdAddressController.onPageLoad(CheckMode))
    )
    case WhatIsTradingNamePage  => ua =>
      checkNextPageForValueThenRoute(
        CheckMode, ua, BusinessWithoutIdAddressPage, routes.BusinessWithoutIdAddressController.onPageLoad(CheckMode)
      )
    case BusinessWithoutIdAddressPage  => ua =>
      checkNextPageForValueThenRoute(
        CheckMode, ua, ContactNamePage, routes.YourContactDetailsController.onPageLoad(CheckMode)
      )
    case BusinessTypePage   => _ => routes.UTRController.onPageLoad(CheckMode)
    case UTRPage   => _ => routes.BusinessNameController.onPageLoad(CheckMode)
    case BusinessNamePage => _ => routes.IsThisYourBusinessController.onPageLoad(CheckMode)
    case IsThisYourBusinessPage   => ua => yesNoPage(
      ua,
      IsThisYourBusinessPage,
      checkNextPageForValueThenRoute(CheckMode, ua, ContactNamePage, routes.YourContactDetailsController.onPageLoad(CheckMode)),
      routes.BusinessNotIdentifiedController.onPageLoad()
    )
    case ContactNamePage => ua => checkNextPageForValueThenRoute(CheckMode, ua, ContactEmailPage, routes.ContactEmailController.onPageLoad(CheckMode))
    case ContactEmailPage => ua => checkNextPageForValueThenRoute(CheckMode, ua, HaveTelephonePage, routes.HaveTelephoneController.onPageLoad(CheckMode))
    case ContactPhonePage => ua => checkNextPageForValueThenRoute(CheckMode, ua, DoYouHaveSecondContactPage, routes.DoYouHaveSecondContactController.onPageLoad(CheckMode))
    case HaveTelephonePage   => ua => yesNoPage(
      ua,
      HaveTelephonePage,
      routes.ContactPhoneController.onPageLoad(CheckMode),
      checkNextPageForValueThenRoute(CheckMode, ua, DoYouHaveSecondContactPage, routes.DoYouHaveSecondContactController.onPageLoad(CheckMode))
    )
    case DoYouHaveSecondContactPage   => ua => yesNoPage(
      ua,
      DoYouHaveSecondContactPage,
      checkNextPageForValueThenRoute(CheckMode, ua, SecondContactNamePage, routes.SecondContactNameController.onPageLoad(CheckMode)),
      routes.CheckYourAnswersController.onPageLoad()
    )
    case SecondContactNamePage   => ua =>
      checkNextPageForValueThenRoute(CheckMode, ua, SecondContactEmailPage, routes.SecondContactEmailController.onPageLoad(CheckMode))
    case SecondContactEmailPage   => ua =>
      checkNextPageForValueThenRoute(CheckMode, ua, SecondContactHavePhonePage, routes.SecondContactHavePhoneController.onPageLoad(CheckMode))
    case SecondContactHavePhonePage   => ua => yesNoPage(
      ua,
      SecondContactHavePhonePage,
      routes.SecondContactPhoneController.onPageLoad(CheckMode),
      routes.CheckYourAnswersController.onPageLoad()
    )
    case SecondContactPhonePage => _ => routes.CheckYourAnswersController.onPageLoad()
    case _  => _ => controllers.routes.CheckYourAnswersController.onPageLoad()
  }

  def yesNoPage(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call =
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(routes.ThereIsAProblemController.onPageLoad())
}
