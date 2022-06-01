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

import com.sun.xml.internal.bind.v2.model.nav.Navigator
import controllers.routes
import models._
import pages._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

// @formatter:off
@Singleton
class CBCRNavigator @Inject()() extends Navigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case doYouHaveUTRPage   => _ => routes.DoYouHaveUTRController.onPageLoad //TODO change to doYouHaveUTR(NormalMode)
//    case WhatAreYouRegisteringAsPage            => whatAreYouRegisteringAs(NormalMode)
//    case DoYouHaveNINPage                       => doYouHaveNINORoutes(NormalMode)
//    case WhatIsYourNationalInsuranceNumberPage  => _ => Some(routes.WhatIsYourNameController.onPageLoad(NormalMode))
//    case WhatIsYourNamePage                     => _ => Some(routes.WhatIsYourDateOfBirthController.onPageLoad(NormalMode))
//    case WhatIsYourDateOfBirthPage             => whatIsYourDateOfBirthRoutes(NormalMode)
//    case BusinessWithoutIDNamePage              => _ => Some(routes.BusinessHaveDifferentNameController.onPageLoad(NormalMode))
//    case BusinessHaveDifferentNamePage          => businessHaveDifferentNameRoutes(NormalMode)
//    case WhatIsTradingNamePage                  => _ => Some(routes.BusinessAddressWithoutIdController.onPageLoad(NormalMode))
//    case NonUkNamePage                          => _ => Some(routes.DateOfBirthWithoutIdController.onPageLoad(NormalMode))
//    case DateOfBirthWithoutIdPage              => whatIsYourDateOfBirthRoutes(NormalMode)
//    case DoYouLiveInTheUKPage                   => doYouLiveInTheUkRoutes(NormalMode)
//    case AddressUKPage                          => _ => Some(routes.IndividualContactEmailController.onPageLoad(NormalMode))
//    case BusinessAddressWithoutIdPage           => _ => Some(routes.ContactNameController.onPageLoad(NormalMode))
//    case IndividualAddressWithoutIdPage         => _ => Some(routes.IndividualContactEmailController.onPageLoad(NormalMode))
//    case WhatIsYourPostcodePage                 => _ => Some(routes.SelectAddressController.onPageLoad(NormalMode))
//    case SelectAddressPage                      => _ => Some(routes.IndividualContactEmailController.onPageLoad(NormalMode))
//    case BusinessTypePage                       => _ => Some(routes.UTRController.onPageLoad(NormalMode))
//    case UTRPage                               => isSoleProprietor(NormalMode)
//    case SoleNamePage                           => _ => Some(routes.IsThisYourBusinessController.onPageLoad(NormalMode))
//    case BusinessNamePage                       => _ => Some(routes.IsThisYourBusinessController.onPageLoad(NormalMode))
//    case IsThisYourBusinessPage                => isThisYourBusiness(NormalMode)
//    case RegistrationInfoPage                   => _ => Some(routes.IndividualContactEmailController.onPageLoad(NormalMode))
  }

  override val checkRouteMap: Page => UserAnswers => Call = {
    case doYouHaveUTRPage  => _ => routes.DoYouHaveUTRController.onPageLoad //TODO change to doYouHaveUTR(CheckMode)
//    case WhatAreYouRegisteringAsPage           => whatAreYouRegisteringAs(CheckMode)
//    case BusinessHaveDifferentNamePage         => businessHaveDifferentNameRoutes(CheckMode)
//    case DoYouHaveNINPage                      => doYouHaveNINORoutes(CheckMode)
//    case WhatIsYourDateOfBirthPage            => whatIsYourDateOfBirthRoutes(CheckMode)
//    case DateOfBirthWithoutIdPage             => whatIsYourDateOfBirthRoutes(CheckMode)
//    case DoYouLiveInTheUKPage                  => doYouLiveInTheUkRoutes(CheckMode)
//    case WhatIsYourPostcodePage                => _ => Some(routes.SelectAddressController.onPageLoad(CheckMode))
//    case BusinessTypePage                      => _ => Some(routes.UTRController.onPageLoad(CheckMode))
//    case UTRPage                              => isSoleProprietor(CheckMode)
//    case IsThisYourBusinessPage               => isThisYourBusiness(CheckMode)
//    case WhatIsYourNationalInsuranceNumberPage  => _ => Some(routes.WhatIsYourNameController.onPageLoad(CheckMode))
//    case WhatIsYourNamePage                    => _ => Some(routes.WhatIsYourDateOfBirthController.onPageLoad(CheckMode))
//
//
//    case RegistrationInfoPage                  => ua =>
//      checkNextPageForValueThenRoute(CheckMode, ua, IndividualContactEmailPage, routes.IndividualContactEmailController.onPageLoad(CheckMode)
//      )
//
//    case SoleNamePage  => ua =>
//      checkNextPageForValueThenRoute(CheckMode, ua, RegistrationInfoPage, routes.IsThisYourBusinessController.onPageLoad(CheckMode)
//      )
//
//    case BusinessNamePage  => ua =>
//      checkNextPageForValueThenRoute(CheckMode, ua, RegistrationInfoPage, routes.IsThisYourBusinessController.onPageLoad(CheckMode)
//      )
//
//    case ContactNamePage  => ua =>
//      checkNextPageForValueThenRoute(
//        CheckMode, ua, ContactEmailPage, routes.ContactEmailController.onPageLoad(CheckMode)
//      )
//
//    case BusinessWithoutIDNamePage  => ua =>
//      checkNextPageForValueThenRoute(
//        CheckMode, ua, BusinessHaveDifferentNamePage, routes.BusinessHaveDifferentNameController.onPageLoad(CheckMode)
//      )
//
//    case WhatIsTradingNamePage  => ua =>
//      checkNextPageForValueThenRoute(
//        CheckMode, ua, BusinessAddressWithoutIdPage, routes.BusinessAddressWithoutIdController.onPageLoad(CheckMode)
//      )
//
//    case NonUkNamePage  => ua =>
//      checkNextPageForValueThenRoute(
//        CheckMode, ua, DateOfBirthWithoutIdPage, routes.DateOfBirthWithoutIdController.onPageLoad(CheckMode)
//      )
//
//    case IndividualAddressWithoutIdPage  => ua =>
//      checkNextPageForValueThenRoute(
//        CheckMode, ua, IndividualContactEmailPage, routes.IndividualContactEmailController.onPageLoad(CheckMode)
//      )
//
//    case SelectAddressPage  => ua =>
//      checkNextPageForValueThenRoute(
//        CheckMode, ua, IndividualContactEmailPage, routes.IndividualContactEmailController.onPageLoad(CheckMode)
//      )
//
//    case AddressUKPage  => ua =>
//      checkNextPageForValueThenRoute(
//        CheckMode, ua, IndividualContactEmailPage, routes.IndividualContactEmailController.onPageLoad(CheckMode)
//    )
//
//    case BusinessAddressWithoutIdPage  => ua =>
//      checkNextPageForValueThenRoute(
//        CheckMode, ua, ContactNamePage, routes.ContactNameController.onPageLoad(CheckMode)
//      )

    case _  => _ => controllers.routes.CheckYourAnswersController.onPageLoad
  }
//
//  private def doYouHaveUTR(mode: Mode)(ua: UserAnswers): Option[Call] =
//    ua.get(doYouHaveUTRPage) map {
//      case true => routes.BusinessTypeController.onPageLoad(mode)
//      case false => routes.WhatAreYouRegisteringAsController.onPageLoad(mode)
//    }
//
//  private def whatAreYouRegisteringAs(mode: Mode)(ua: UserAnswers): Option[Call] =
//    ua.get(WhatAreYouRegisteringAsPage) map {
//      case RegistrationTypeBusiness =>
//        checkNextPageForValueThenRoute(mode, ua, BusinessWithoutIDNamePage, routes.BusinessWithoutIDNameController.onPageLoad(mode)).get
//      case RegistrationTypeIndividual =>
//        checkNextPageForValueThenRoute(mode, ua, DoYouHaveNINPage, routes.DoYouHaveNINController.onPageLoad(mode)).get
//    }
//
//  private def businessHaveDifferentNameRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
//    ua.get(BusinessHaveDifferentNamePage) map {
//      case true  =>
//        routes.WhatIsTradingNameController.onPageLoad(mode)
//      case false =>
//        checkNextPageForValueThenRoute(mode, ua, BusinessAddressWithoutIdPage, routes.BusinessAddressWithoutIdController.onPageLoad(mode)).get
//    }
//
//  private def doYouHaveNINORoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
//    ua.get(DoYouHaveNINPage) map {
//      case true =>
//        checkNextPageForValueThenRoute(mode, ua, WhatIsYourNationalInsuranceNumberPage,
//          routes.WhatIsYourNationalInsuranceNumberController.onPageLoad(mode)).get
//      case false =>
//        checkNextPageForValueThenRoute(mode, ua, NonUkNamePage, routes.NonUkNameController.onPageLoad(mode)).get
//    }
//
//  private def whatIsYourDateOfBirthRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
//    ua.get(DoYouHaveNINPage) map {
//      case true =>
//        routes.WeHaveConfirmedYourIdentityController.onPageLoad(mode)
//      case false =>
//        checkNextPageForValueThenRoute(mode, ua, DoYouLiveInTheUKPage, routes.DoYouLiveInTheUKController.onPageLoad(mode)).get
//    }
//
//  private def doYouLiveInTheUkRoutes(mode: Mode)(ua: UserAnswers): Option[Call] =
//    ua.get(DoYouLiveInTheUKPage) map {
//      case true  => routes.WhatIsYourPostcodeController.onPageLoad(mode)
//      case false => routes.IndividualAddressWithoutIdController.onPageLoad(mode)
//    }
//
//  private def isSoleProprietor(mode: Mode)(ua: UserAnswers): Option[Call] =
//    ua.get(BusinessTypePage) map {
//      case BusinessType.Sole => routes.SoleNameController.onPageLoad(mode)
//      case _                 => routes.BusinessNameController.onPageLoad(mode)
//    }
//
//  private def isThisYourBusiness(mode: Mode)(ua: UserAnswers): Option[Call] =
//    (ua.get(IsThisYourBusinessPage), ua.get(BusinessTypePage)) match {
//      case (Some(true), Some(Sole)) =>
//        checkNextPageForValueThenRoute(mode, ua, IndividualContactEmailPage, routes.IndividualContactEmailController.onPageLoad(mode))
//      case (Some(true), Some(_))    =>
//        checkNextPageForValueThenRoute(mode, ua, ContactNamePage, routes.ContactNameController.onPageLoad(mode))
//      case _                        => Some(routes.BusinessNotIdentifiedController.onPageLoad())
//    }
}
