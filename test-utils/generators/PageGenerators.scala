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

package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryDoYouHaveSecondContactPage: Arbitrary[DoYouHaveSecondContactPage.type] =
    Arbitrary(DoYouHaveSecondContactPage)

  implicit lazy val arbitrarySecondContactPhonePage: Arbitrary[SecondContactPhonePage.type] =
    Arbitrary(SecondContactPhonePage)

  implicit lazy val arbitrarySecondContactHavePhonePage: Arbitrary[SecondContactHavePhonePage.type] =
    Arbitrary(SecondContactHavePhonePage)

  implicit lazy val arbitraryHaveTelephonePage: Arbitrary[HaveTelephonePage.type] =
    Arbitrary(HaveTelephonePage)

  implicit lazy val arbitrarySecondContactEmailPage: Arbitrary[SecondContactEmailPage.type] =
    Arbitrary(SecondContactEmailPage)

  implicit lazy val arbitraryContactEmailPage: Arbitrary[ContactEmailPage.type] =
    Arbitrary(ContactEmailPage)

  implicit lazy val arbitrarySecondContactNamePage: Arbitrary[SecondContactNamePage.type] =
    Arbitrary(SecondContactNamePage)

  implicit lazy val arbitraryContactNamePage: Arbitrary[ContactNamePage.type] =
    Arbitrary(ContactNamePage)

  implicit lazy val arbitraryContactPhonePage: Arbitrary[ContactPhonePage.type] =
    Arbitrary(ContactPhonePage)

  implicit lazy val arbitraryBusinessWithoutIdAddressPage: Arbitrary[BusinessWithoutIdAddressPage.type] =
    Arbitrary(BusinessWithoutIdAddressPage)

  implicit lazy val arbitraryWhatIsTradingNamePage: Arbitrary[WhatIsTradingNamePage.type] =
    Arbitrary(WhatIsTradingNamePage)

  implicit lazy val arbitraryUTRPage: Arbitrary[UTRPage.type] =
    Arbitrary(UTRPage)

  implicit lazy val arbitraryBusinessHaveDifferentNamePage: Arbitrary[BusinessHaveDifferentNamePage.type] =
    Arbitrary(BusinessHaveDifferentNamePage)

  implicit lazy val arbitraryBusinessWithoutIDNamePage: Arbitrary[BusinessWithoutIDNamePage.type] =
    Arbitrary(BusinessWithoutIDNamePage)

  implicit lazy val arbitraryBusinessNamePage: Arbitrary[BusinessNamePage.type] =
    Arbitrary(BusinessNamePage)

  implicit lazy val arbitraryBusinessTypePage: Arbitrary[BusinessTypePage.type] =
    Arbitrary(BusinessTypePage)
}
