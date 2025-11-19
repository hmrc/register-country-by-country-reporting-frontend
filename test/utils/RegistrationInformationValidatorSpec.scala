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
import models.BusinessType.LimitedCompany
import models.UniqueTaxpayerReference
import org.scalatest.matchers.should.Matchers.shouldBe
import pages.*

class RegistrationInformationValidatorSpec extends SpecBase {

  "isInformationMissing" - {
    val registrationInfo = arbitraryRegistrationInfo.arbitrary.sample.get
    val address          = arbitraryBusinessWithoutIdAddress.arbitrary.sample.get
    val utr              = UniqueTaxpayerReference("testUTR")
    val maxPhoneNumber   = 24

    "AutoMatch Flow" - {

      "must return true when RegistrationInfo is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(RegistrationInfoPage) shouldBe true
      }

      "must return true when IsThisYourBusiness is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(IsThisYourBusinessPage) shouldBe true
      }

      "must return true when PrimaryContactName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactNamePage) shouldBe true
      }

      "must return true when PrimaryContactEmail is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactEmailPage) shouldBe true
      }

      "must return true when DoYouHavePhone is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(HaveTelephonePage) shouldBe true
      }

      "must return true when HaveTelephone true and ContactPhonePage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactPhonePage) shouldBe true
      }

      "must return true when DoYouHaveSecondContactPage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(DoYouHaveSecondContactPage) shouldBe true
      }

      "must return true when DoYouHaveSecondContactPage is True & SecondContactName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactNamePage) shouldBe true
      }

      "must return true when DoYouHaveSecondContactPage is True & SecondContactEmail is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactEmailPage) shouldBe true
      }

      "must return true when DoYouHaveSecondContactPage is True & SecondContactHavePhone is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactHavePhonePage) shouldBe true
      }

      "must return true when DoYouHaveSecondContactPage and SecondContactHavePhonePage is true and SecondContactPhonePage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, true)
        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactPhonePage) shouldBe true
      }

      "must return true when all values are present" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)
          .withPage(ContactPhonePage, validPhoneNumber(maxPhoneNumber).sample.get)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, true)
          .withPage(SecondContactPhonePage, validPhoneNumber(maxPhoneNumber).sample.get)
        RegistrationInformationValidator(userAnswers).isInformationMissing.isEmpty shouldBe true
      }

      "must return true when all mandatory values are present" in {
        val userAnswers = emptyUserAnswers
          .withPage(AutoMatchedUTRPage, utr)
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, false)
        RegistrationInformationValidator(userAnswers).isInformationMissing.isEmpty shouldBe true
      }
    }

    "Non AutoMatch Flow - for RegisterWithIDFlow" - {

      "must return true when IsRegisteredAddressInUkPage is None" in {
        val userAnswers = emptyUserAnswers

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(IsRegisteredAddressInUkPage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage is true And BusinessType is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(BusinessTypePage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage is true And UTR is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(UTRPage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage is true And Business Name is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(BusinessNamePage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage is true And RegistrationInfo is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(RegistrationInfoPage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage is true And IsThisYourBusiness is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(IsThisYourBusinessPage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage is true And IsThisYourBusiness is false" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, false)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(IsThisYourBusinessPage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage is true And PrimaryContactName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactNamePage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage is true And PrimaryContactEmail is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactEmailPage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage is true And DoYouHavePhone is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(HaveTelephonePage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage and HaveTelephone is true and ContactPhonePage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactPhonePage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage is true And DoYouHaveSecondContactPage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(DoYouHaveSecondContactPage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage and DoYouHaveSecondContactPage is True & SecondContactName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactNamePage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage and DoYouHaveSecondContactPage is True & SecondContactEmail is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactEmailPage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage and DoYouHaveSecondContactPage is True & SecondContactHavePhone is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactHavePhonePage) shouldBe true
      }

      "must return true when IsRegisteredAddressInUkPage and DoYouHaveSecondContactPage and SecondContactHavePhonePage is true and SecondContactPhonePage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, true)
        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactPhonePage) shouldBe true
      }

      "must return true when all values are present" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)
          .withPage(ContactPhonePage, validPhoneNumber(maxPhoneNumber).sample.get)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, true)
          .withPage(SecondContactPhonePage, validPhoneNumber(maxPhoneNumber).sample.get)
        RegistrationInformationValidator(userAnswers).isInformationMissing.isEmpty shouldBe true
      }

      "must return true when all mandatory values are present" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, false)
        RegistrationInformationValidator(userAnswers).isInformationMissing.isEmpty shouldBe true
      }

      "must return false when IsRegisteredAddressInUkPage is false And DoYouHaveUTR is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(DoYouHaveUTRPage) shouldBe true
      }

      "must return false when DoYouHaveUTR is true And BusinessType is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(BusinessTypePage) shouldBe true
      }

      "must return true when DoYouHaveUTR is true And UTR is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(UTRPage) shouldBe true
      }

      "must return true when DoYouHaveUTR is true And Business Name is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(BusinessNamePage) shouldBe true
      }

      "must return true when DoYouHaveUTR is true And RegistrationInfo is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(RegistrationInfoPage) shouldBe true
      }

      "must return true when DoYouHaveUTR is true And IsThisYourBusiness is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(IsThisYourBusinessPage) shouldBe true
      }

      "must return true when DoYouHaveUTR is true And IsThisYourBusiness is false" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, false)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(IsThisYourBusinessPage) shouldBe true
      }

      "must return true when DoYouHaveUTR is true And PrimaryContactName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactNamePage) shouldBe true
      }

      "must return true when DoYouHaveUTR is true And PrimaryContactEmail is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactEmailPage) shouldBe true
      }

      "must return true when DoYouHaveUTR is true And DoYouHavePhone is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(HaveTelephonePage) shouldBe true
      }

      "must return true when DoYouHaveUTR and HaveTelephone is true and ContactPhonePage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactPhonePage) shouldBe true
      }

      "must return true when DoYouHaveUTR is true And DoYouHaveSecondContactPage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(DoYouHaveSecondContactPage) shouldBe true
      }

      "must return true when DoYouHaveUTR and DoYouHaveSecondContactPage is True & SecondContactName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactNamePage) shouldBe true
      }

      "must return true when DoYouHaveUTR and DoYouHaveSecondContactPage is True & SecondContactEmail is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactEmailPage) shouldBe true
      }

      "must return true when DoYouHaveUTR and DoYouHaveSecondContactPage is True & SecondContactHavePhone is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactHavePhonePage) shouldBe true
      }

      "must return true when DoYouHaveUTR and DoYouHaveSecondContactPage and SecondContactHavePhonePage is true and SecondContactPhonePage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, true)
        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactPhonePage) shouldBe true
      }

      "must return true when DoYouHaveUTR is true all values are present" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)
          .withPage(ContactPhonePage, validPhoneNumber(maxPhoneNumber).sample.get)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")
          .withPage(SecondContactHavePhonePage, true)
          .withPage(SecondContactPhonePage, validPhoneNumber(maxPhoneNumber).sample.get)
        RegistrationInformationValidator(userAnswers).isInformationMissing.isEmpty shouldBe true
      }

      "must return true when DoYouHaveUTR is true all mandatory values are present" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, true)
          .withPage(BusinessTypePage, LimitedCompany)
          .withPage(UTRPage, utr)
          .withPage(BusinessNamePage, "test business")
          .withPage(RegistrationInfoPage, registrationInfo)
          .withPage(IsThisYourBusinessPage, true)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, false)
        RegistrationInformationValidator(userAnswers).isInformationMissing.isEmpty shouldBe true
      }
    }

    "Non AutoMatch Flow - for NonRegisterWithoutIDFlow - IsRegisteredAddressInUkPage & DoYouHaveUTRPage is false" - {

      "must return true when BusinessWithoutIDName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(BusinessWithoutIDNamePage) shouldBe true
      }

      "must return true when BusinessWithoutIdAddress is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(BusinessWithoutIdAddressPage) shouldBe true
      }

      "must return true when BusinessHaveDifferentName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)

        RegistrationInformationValidator(userAnswers).isInformationMissing.nonEmpty shouldBe true
      }

      "must return true when WhatIsTradingName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(WhatIsTradingNamePage) shouldBe true
      }

      "must return true when PrimaryContactName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactNamePage) shouldBe true
      }

      "must return true when PrimaryContactEmail is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactEmailPage) shouldBe true
      }

      "must return true when HaveTelephonePage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(HaveTelephonePage) shouldBe true
      }

      "must return true when HaveTelephone is true and PrimaryContactNumber is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(ContactPhonePage) shouldBe true
      }

      "must return true when DoYouHaveSecondContactPage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(DoYouHaveSecondContactPage) shouldBe true
      }

      "must return true when SecondContactName is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactNamePage) shouldBe true
      }

      "must return true when SecondContactEmail is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactEmailPage) shouldBe true
      }

      "must return SecondContactHavePhone if all mandatory values are not available" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, true)
          .withPage(SecondContactNamePage, "Test Second contact name")
          .withPage(SecondContactEmailPage, "Test2@test.com")

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactHavePhonePage) shouldBe true
      }

      "must return true when SecondContactHavePhonePage is true and SecondContactPhonePage is None" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
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

        RegistrationInformationValidator(userAnswers).isInformationMissing.contains(SecondContactPhonePage) shouldBe true
      }

      "must return true if all values are available" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
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

        RegistrationInformationValidator(userAnswers).isInformationMissing.isEmpty shouldBe true
      }

      "must return true if all mandatory values are available" in {
        val userAnswers = emptyUserAnswers
          .withPage(IsRegisteredAddressInUkPage, false)
          .withPage(DoYouHaveUTRPage, false)
          .withPage(BusinessWithoutIDNamePage, "test business")
          .withPage(BusinessWithoutIdAddressPage, address)
          .withPage(BusinessHaveDifferentNamePage, false)
          .withPage(ContactNamePage, "test user")
          .withPage(ContactEmailPage, "test@test.com")
          .withPage(HaveTelephonePage, false)
          .withPage(DoYouHaveSecondContactPage, false)

        RegistrationInformationValidator(userAnswers).isInformationMissing.isEmpty shouldBe true
      }

    }

  }
}
