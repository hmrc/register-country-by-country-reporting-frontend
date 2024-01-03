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

package utils

import base.SpecBase
import models.matching.RegistrationInfo
import models.register.response.details.AddressResponse
import models.{Address, Country, SafeId}
import pages._
import play.api.i18n.Messages

class CheckYourAnswersHelperSpec extends SpecBase {

  val businessAddress: Address = Address("", None, "", None, None, Country("valid", "GB", "United Kingdom"))
  val matchedAddress: AddressResponse = AddressResponse("", None, None, None, None, "GB")
  implicit val messages: Messages = messages(app)
  val countryListFactory: CountryListFactory = app.injector.instanceOf[CountryListFactory]

  "RowBuilder" - {
    "must Create Business section for a Business without an ID" in {
      val userAnswers = emptyUserAnswers
        .set(DoYouHaveUTRPage, false)
        .success
        .value
        .set(BusinessWithoutIDNamePage, "Company")
        .success
        .value
        .set(BusinessHaveDifferentNamePage, true)
        .success
        .value
        .set(WhatIsTradingNamePage, "Company two")
        .success
        .value
        .set(BusinessWithoutIdAddressPage, businessAddress)
        .success
        .value
        .set(ContactEmailPage, "test@test.com")
        .success
        .value
        .set(ContactNamePage, "Name Name")
        .success
        .value
        .set(HaveTelephonePage, false)
        .success
        .value
        .set(DoYouHaveSecondContactPage, true)
        .success
        .value
        .set(SecondContactNamePage, "secondContactName")
        .success
        .value
        .set(SecondContactEmailPage, "secondContactEmail")
        .success
        .value
        .set(SecondContactHavePhonePage, true)
        .success
        .value
        .set(SecondContactPhonePage, "secondContactPhone")
        .success
        .value

        val checkYourAnswersHelper = new CheckYourAnswersHelper(userAnswers, countryListFactory)

        val businessRows = checkYourAnswersHelper.businessWithoutIDSection

        businessRows.size mustBe 4
    }
    "must Create Business section for a Business with an ID" in {
      val userAnswers = emptyUserAnswers
        .set(DoYouHaveUTRPage, true)
        .success
        .value
        .set(IsThisYourBusinessPage, true)
        .success
        .value
        .set(RegistrationInfoPage, RegistrationInfo(SafeId("xxx"), "Company", matchedAddress))
        .success
        .value
        .set(BusinessWithoutIdAddressPage, businessAddress)
        .success
        .value
        .set(ContactEmailPage, "test@test.com")
        .success
        .value
        .set(ContactNamePage, "Name Name")
        .success
        .value
        .set(HaveTelephonePage, false)
        .success
        .value
        .set(DoYouHaveSecondContactPage, true)
        .success
        .value
        .set(SecondContactNamePage, "secondContactName")
        .success
        .value
        .set(SecondContactEmailPage, "secondContactEmail")
        .success
        .value
        .set(SecondContactHavePhonePage, true)
        .success
        .value
        .set(SecondContactPhonePage, "secondContactPhone")
        .success
        .value

        val checkYourAnswersHelper = new CheckYourAnswersHelper(userAnswers, countryListFactory)

        val businessRows = checkYourAnswersHelper.businessWithIDSection

        businessRows.size mustBe 1
    }
    "must create first contact rows" in {
      val userAnswers = emptyUserAnswers
        .set(DoYouHaveUTRPage, false)
        .success
        .value
        .set(BusinessWithoutIDNamePage, "Company")
        .success
        .value
        .set(WhatIsTradingNamePage, "Company two")
        .success
        .value
        .set(BusinessWithoutIdAddressPage, businessAddress)
        .success
        .value
        .set(ContactEmailPage, "test@test.com")
        .success
        .value
        .set(ContactNamePage, "Name Name")
        .success
        .value
        .set(HaveTelephonePage, false)
        .success
        .value
        .set(DoYouHaveSecondContactPage, true)
        .success
        .value
        .set(SecondContactNamePage, "secondContactName")
        .success
        .value
        .set(SecondContactEmailPage, "secondContactEmail")
        .success
        .value
        .set(SecondContactHavePhonePage, true)
        .success
        .value
        .set(SecondContactPhonePage, "secondContactPhone")
        .success
        .value

      val checkYourAnswersHelper = new CheckYourAnswersHelper(userAnswers, countryListFactory)

      val firstContactRows = checkYourAnswersHelper.firstContactSection

      firstContactRows.size mustBe 3
    }
    "must create second contact rows" in {
      val userAnswers = emptyUserAnswers
        .set(DoYouHaveUTRPage, false)
        .success
        .value
        .set(BusinessWithoutIDNamePage, "Company")
        .success
        .value
        .set(WhatIsTradingNamePage, "Company two")
        .success
        .value
        .set(BusinessWithoutIdAddressPage, businessAddress)
        .success
        .value
        .set(ContactEmailPage, "test@test.com")
        .success
        .value
        .set(ContactNamePage, "Name Name")
        .success
        .value
        .set(HaveTelephonePage, false)
        .success
        .value
        .set(DoYouHaveSecondContactPage, true)
        .success
        .value
        .set(SecondContactNamePage, "secondContactName")
        .success
        .value
        .set(SecondContactEmailPage, "secondContactEmail")
        .success
        .value
        .set(SecondContactHavePhonePage, true)
        .success
        .value
        .set(SecondContactPhonePage, "secondContactPhone")
        .success
        .value

      val checkYourAnswersHelper = new CheckYourAnswersHelper(userAnswers, countryListFactory)

      val secondContactRows = checkYourAnswersHelper.secondContactSection

      secondContactRows.size mustBe 4
    }
    "must create no second contact row" in {
      val userAnswers = emptyUserAnswers
        .set(DoYouHaveUTRPage, false)
        .success
        .value
        .set(BusinessWithoutIDNamePage, "Company")
        .success
        .value
        .set(WhatIsTradingNamePage, "Company two")
        .success
        .value
        .set(BusinessWithoutIdAddressPage, businessAddress)
        .success
        .value
        .set(ContactEmailPage, "test@test.com")
        .success
        .value
        .set(ContactNamePage, "Name Name")
        .success
        .value
        .set(HaveTelephonePage, false)
        .success
        .value
        .set(DoYouHaveSecondContactPage, false)
        .success
        .value


      val checkYourAnswersHelper = new CheckYourAnswersHelper(userAnswers, countryListFactory)

      val secondContactRows = checkYourAnswersHelper.secondContactSection

      secondContactRows.size mustBe 1
    }
  }


}
