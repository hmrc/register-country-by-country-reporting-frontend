/*
 * Copyright 2025 HM Revenue & Customs
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
import pages._
import org.scalatest.matchers.should.Matchers.shouldBe

class RegistrationInformationValidatorSpec extends SpecBase {

  "isInformationMissing" - {
    "for NonRegisteredUserFlow" - {

      val address        = arbitraryBusinessWithoutIdAddress.arbitrary.sample.get
      val maxPhoneNumber = 24

      "must return BusinessWithoutIDName if all mandatory values are not available" in {
        RegistrationInformationValidator(emptyUserAnswers).isInformationMissing shouldBe true
      }

      "must return BusinessWithoutIdAddress if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers.withPage(BusinessWithoutIDNamePage, "test business")
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return BusinessHaveDifferentName if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return WhatIsTradingName if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, true)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return PrimaryContactName if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return PrimaryContactEmail if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return HaveTelephonePage if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return PrimaryContactNumber if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return DoYouHaveSecondContactPage if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return SecondContactName if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return SecondContactEmail if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return SecondContactHavePhone if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return SecondContactPhonePage if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, true)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return false if all values are available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, true)
          .withPage(WhatIsTradingNamePage, "testTradingName")
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)
          .withPage(ContactPhonePage, validPhoneNumber(maxPhoneNumber).sample.get)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, true)
          .withPage(SecondContactPhonePage, validPhoneNumber(maxPhoneNumber).sample.get)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe false
      }

      "must return false if all mandatory values are available" in {
        val userAnswers = emptyUserAnswers
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, false)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe false
      }

    }

    "for RegisteredUserFlow" - {

      val registrationInfo = arbitraryRegistrationInfo.arbitrary.sample.get
      val maxPhoneNumber   = 24

      "must return PrimaryContactName if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return PrimaryContactEmail if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(ContactNamePage, "test user")
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return HaveTelephonePage if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return PrimaryContactNumber if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return DoYouHaveSecondContactPage if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return SecondContactName if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return SecondContactEmail if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return SecondContactHavePhone if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return SecondContactPhonePage if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, true)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return IsThisBusinessPage when Page value is not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, false)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return IsThisBusinessPage when Page value is false" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, false)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe true
      }

      "must return false if all values are available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(WhatIsTradingNamePage, "testTradingName")
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)
          .withPage(ContactPhonePage, validPhoneNumber(maxPhoneNumber).sample.get)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, true)
          .withPage(SecondContactPhonePage, validPhoneNumber(maxPhoneNumber).sample.get)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe false
      }

      "must return false if all mandatory values are available" in {
        val userAnswers = emptyUserAnswers
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, false)
        RegistrationInformationValidator(userAnswers).isInformationMissing shouldBe false
      }

    }
  }
}
