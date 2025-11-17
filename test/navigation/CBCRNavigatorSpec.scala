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

package navigation

import base.SpecBase
import controllers.routes
import generators.Generators
import models.BusinessType.LimitedCompany
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class CBCRNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new CBCRNavigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from IsRegisteredAddressInUk page to BusinessType page if Yes is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(IsRegisteredAddressInUkPage, true)
              .success
              .value

          navigator
            .nextPage(IsRegisteredAddressInUkPage, NormalMode, updatedAnswers)
            .mustBe(routes.BusinessTypeController.onPageLoad(NormalMode))
        }
      }

      "must go from IsRegisteredAddressInUk page to DoYouHaveUTR page if NO is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(IsRegisteredAddressInUkPage, false)
              .success
              .value

          navigator
            .nextPage(IsRegisteredAddressInUkPage, NormalMode, updatedAnswers)
            .mustBe(routes.DoYouHaveUTRController.onPageLoad(NormalMode))
        }
      }

      "must go from DoYouHaveUTR page to BusinessType page if Yes is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(DoYouHaveUTRPage, true)
              .success
              .value

          navigator
            .nextPage(DoYouHaveUTRPage, NormalMode, updatedAnswers)
            .mustBe(routes.BusinessTypeController.onPageLoad(NormalMode))
        }
      }

      "must go from DoYouHaveUTR page to BusinessWithoutIDName page if NO is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(DoYouHaveUTRPage, false)
              .success
              .value

          navigator
            .nextPage(DoYouHaveUTRPage, NormalMode, updatedAnswers)
            .mustBe(routes.BusinessWithoutIDNameController.onPageLoad(NormalMode))
        }
      }

      "must go from BusinessType page to UTR page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(BusinessTypePage, LimitedCompany)
              .success
              .value

          navigator
            .nextPage(BusinessTypePage, NormalMode, updatedAnswers)
            .mustBe(routes.UTRController.onPageLoad(NormalMode))
        }
      }

      "must go from UTR page to BusinessName page" in {
        val utr: UniqueTaxpayerReference = UniqueTaxpayerReference("1234567890")
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(UTRPage, utr)
              .success
              .value

          navigator
            .nextPage(UTRPage, NormalMode, updatedAnswers)
            .mustBe(routes.BusinessNameController.onPageLoad(NormalMode))
        }
      }

      "must go from BusinessName page to IsThisYourBusiness page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(BusinessNamePage, "name")
              .success
              .value

          navigator
            .nextPage(BusinessNamePage, NormalMode, updatedAnswers)
            .mustBe(routes.IsThisYourBusinessController.onPageLoad(NormalMode))
        }
      }

      "must go from IsThisYourBusiness page to YourContactDetails page if Yes is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(IsThisYourBusinessPage, true)
              .success
              .value

          navigator
            .nextPage(IsThisYourBusinessPage, NormalMode, updatedAnswers)
            .mustBe(routes.YourContactDetailsController.onPageLoad(NormalMode))
        }
      }

      "must go from IsThisYourBusiness page to BusinessNotIdentified page if No is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(IsThisYourBusinessPage, false)
              .success
              .value

          navigator
            .nextPage(IsThisYourBusinessPage, NormalMode, updatedAnswers)
            .mustBe(routes.BusinessNotIdentifiedController.onPageLoad())
        }
      }

      "must go from BusinessWithoutIDName page to BusinessHaveDifferentName page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(BusinessWithoutIDNamePage, "Name")
              .success
              .value

          navigator
            .nextPage(BusinessWithoutIDNamePage, NormalMode, updatedAnswers)
            .mustBe(routes.BusinessHaveDifferentNameController.onPageLoad(NormalMode))
        }
      }

      "must go from BusinessHaveDifferentName page to WhatIsTradingName page if Yes is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(BusinessHaveDifferentNamePage, true)
              .success
              .value

          navigator
            .nextPage(BusinessHaveDifferentNamePage, NormalMode, updatedAnswers)
            .mustBe(routes.WhatIsTradingNameController.onPageLoad(NormalMode))
        }
      }

      "must go from BusinessHaveDifferentName page to BusinessWithoutIdAddress page if No is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(BusinessHaveDifferentNamePage, false)
              .success
              .value

          navigator
            .nextPage(BusinessHaveDifferentNamePage, NormalMode, updatedAnswers)
            .mustBe(routes.BusinessWithoutIdAddressController.onPageLoad(NormalMode))
        }
      }

      "must go from WhatIsTradingName page to BusinessWithoutIdAddress page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(WhatIsTradingNamePage, "Trading Name")
              .success
              .value

          navigator
            .nextPage(WhatIsTradingNamePage, NormalMode, updatedAnswers)
            .mustBe(routes.BusinessWithoutIdAddressController.onPageLoad(NormalMode))
        }
      }

      "must go from BusinessWithoutIdAddress page to YourContactDetails page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(BusinessWithoutIdAddressPage, Address("line 1", None, "Line 3", None, None, Country("valid", "DE", "Germany")))
              .success
              .value

          navigator
            .nextPage(BusinessWithoutIdAddressPage, NormalMode, updatedAnswers)
            .mustBe(routes.YourContactDetailsController.onPageLoad(NormalMode))
        }
      }

      "must go from ContactName page to ContactEmail page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(ContactNamePage, "Name")
              .success
              .value

          navigator
            .nextPage(ContactNamePage, NormalMode, updatedAnswers)
            .mustBe(routes.ContactEmailController.onPageLoad(NormalMode))
        }
      }

      "must go from ContactEmailPage page to HaveTelephone page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(ContactEmailPage, "test@test.com")
              .success
              .value

          navigator
            .nextPage(ContactEmailPage, NormalMode, updatedAnswers)
            .mustBe(routes.HaveTelephoneController.onPageLoad(NormalMode))
        }
      }

      "must go from HaveTelephone page to ContactPhone page if Yes is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(HaveTelephonePage, true)
              .success
              .value

          navigator
            .nextPage(HaveTelephonePage, NormalMode, updatedAnswers)
            .mustBe(routes.ContactPhoneController.onPageLoad(NormalMode))
        }
      }

      "must go from HaveTelephone page to DoYouHaveSecondContact page if No is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(HaveTelephonePage, false)
              .success
              .value

          navigator
            .nextPage(HaveTelephonePage, NormalMode, updatedAnswers)
            .mustBe(routes.DoYouHaveSecondContactController.onPageLoad(NormalMode))
        }
      }

      "must go from ContactPhonePage page to DoYouHaveSecondContact page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(ContactPhonePage, "0987654321")
              .success
              .value

          navigator
            .nextPage(ContactPhonePage, NormalMode, updatedAnswers)
            .mustBe(routes.DoYouHaveSecondContactController.onPageLoad(NormalMode))
        }
      }

      "must go from DoYouHaveSecondContact page to SecondContactName page if Yes is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(DoYouHaveSecondContactPage, true)
              .success
              .value

          navigator
            .nextPage(DoYouHaveSecondContactPage, NormalMode, updatedAnswers)
            .mustBe(routes.SecondContactNameController.onPageLoad(NormalMode))
        }
      }

      "must go from DoYouHaveSecondContact page to CheckYourAnswers page if No is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(DoYouHaveSecondContactPage, false)
              .success
              .value

          navigator
            .nextPage(DoYouHaveSecondContactPage, NormalMode, updatedAnswers)
            .mustBe(routes.CheckYourAnswersController.onPageLoad())
        }
      }

      "must go from SecondContactName page to SecondContactEmail page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(SecondContactNamePage, "Name")
              .success
              .value

          navigator
            .nextPage(SecondContactNamePage, NormalMode, updatedAnswers)
            .mustBe(routes.SecondContactEmailController.onPageLoad(NormalMode))
        }
      }

      "must go from SecondContactEmail page to SecondContactHavePhone page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(SecondContactEmailPage, "test@test.com")
              .success
              .value

          navigator
            .nextPage(SecondContactEmailPage, NormalMode, updatedAnswers)
            .mustBe(routes.SecondContactHavePhoneController.onPageLoad(NormalMode))
        }
      }

      "must go from SecondContactHavePhone page to SecondContactPhone page if Yes is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(SecondContactHavePhonePage, true)
              .success
              .value

          navigator
            .nextPage(SecondContactHavePhonePage, NormalMode, updatedAnswers)
            .mustBe(routes.SecondContactPhoneController.onPageLoad(NormalMode))
        }
      }

      "must go from SecondContactHavePhone page to CheckYourAnswers page if No is selected" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(SecondContactHavePhonePage, false)
              .success
              .value

          navigator
            .nextPage(SecondContactHavePhonePage, NormalMode, updatedAnswers)
            .mustBe(routes.CheckYourAnswersController.onPageLoad())
        }
      }

      "must go from SecondContactPhone page to CheckYourAnswers page" in {
        forAll(arbitrary[UserAnswers]) { answers =>
          val updatedAnswers =
            answers
              .set(SecondContactPhonePage, "0987654321")
              .success
              .value

          navigator
            .nextPage(SecondContactPhonePage, NormalMode, updatedAnswers)
            .mustBe(routes.CheckYourAnswersController.onPageLoad())
        }
      }
    }

    "in Check mode" - {

      "must go from IsRegisteredAddressInUk page to BusinessType page if Yes is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(IsRegisteredAddressInUkPage, true)
            .success
            .value

        navigator
          .nextPage(IsRegisteredAddressInUkPage, CheckMode, updatedAnswers)
          .mustBe(routes.BusinessTypeController.onPageLoad(CheckMode))

      }

      "must go from IsRegisteredAddressInUk page to DoYouHaveUTR page if NO is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(IsRegisteredAddressInUkPage, false)
            .success
            .value

        navigator
          .nextPage(IsRegisteredAddressInUkPage, CheckMode, updatedAnswers)
          .mustBe(routes.DoYouHaveUTRController.onPageLoad(CheckMode))
      }

      "must go from DoYouHaveUTR page to BusinessType page if Yes is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(DoYouHaveUTRPage, true)
            .success
            .value

        navigator
          .nextPage(DoYouHaveUTRPage, CheckMode, updatedAnswers)
          .mustBe(routes.BusinessTypeController.onPageLoad(CheckMode))
      }

      "must go from DoYouHaveUTR page to BusinessWithoutIDName page if NO is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(DoYouHaveUTRPage, false)
            .success
            .value

        navigator
          .nextPage(DoYouHaveUTRPage, CheckMode, updatedAnswers)
          .mustBe(routes.BusinessWithoutIDNameController.onPageLoad(CheckMode))
      }

      "must go from BusinessType page to UTR page" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(BusinessTypePage, LimitedCompany)
            .success
            .value

        navigator
          .nextPage(BusinessTypePage, CheckMode, updatedAnswers)
          .mustBe(routes.UTRController.onPageLoad(CheckMode))
      }

      "must go from UTR page to BusinessName page" in {
        val utr: UniqueTaxpayerReference = UniqueTaxpayerReference("1234567890")

        val updatedAnswers =
          emptyUserAnswers
            .set(UTRPage, utr)
            .success
            .value

        navigator
          .nextPage(UTRPage, CheckMode, updatedAnswers)
          .mustBe(routes.BusinessNameController.onPageLoad(CheckMode))
      }

      "must go from BusinessName page to IsThisYourBusiness page" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(BusinessNamePage, "name")
            .success
            .value

        navigator
          .nextPage(BusinessNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.IsThisYourBusinessController.onPageLoad(CheckMode))
      }

      "must go from IsThisYourBusiness page to YourContactDetails page if Yes is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(IsThisYourBusinessPage, true)
            .success
            .value

        navigator
          .nextPage(IsThisYourBusinessPage, CheckMode, updatedAnswers)
          .mustBe(routes.YourContactDetailsController.onPageLoad(CheckMode))
      }

      "must go from IsThisYourBusiness page to BusinessNotIdentified page if No is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(IsThisYourBusinessPage, false)
            .success
            .value

        navigator
          .nextPage(IsThisYourBusinessPage, CheckMode, updatedAnswers)
          .mustBe(routes.BusinessNotIdentifiedController.onPageLoad())
      }

      "must go from BusinessWithoutIDName page to BusinessHaveDifferentName page when next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(BusinessWithoutIDNamePage, "Name")
            .success
            .value

        navigator
          .nextPage(BusinessWithoutIDNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.BusinessHaveDifferentNameController.onPageLoad(CheckMode))
      }

      "must go from BusinessWithoutIDName page to CheckYourAnswers page when next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(BusinessWithoutIDNamePage, "Name")
            .success
            .value
            .set(BusinessHaveDifferentNamePage, true)
            .success
            .value

        navigator
          .nextPage(BusinessWithoutIDNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from BusinessHaveDifferentName page to WhatIsTradingName page if Yes is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(BusinessHaveDifferentNamePage, true)
            .success
            .value

        navigator
          .nextPage(BusinessHaveDifferentNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.WhatIsTradingNameController.onPageLoad(CheckMode))
      }

      "must go from BusinessHaveDifferentName page to BusinessWithoutIdAddress page if No is selected and next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(BusinessHaveDifferentNamePage, false)
            .success
            .value

        navigator
          .nextPage(BusinessHaveDifferentNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.BusinessWithoutIdAddressController.onPageLoad(CheckMode))
      }

      "must go from BusinessHaveDifferentName page to CheckYourAnswers page if No is selected and next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(BusinessHaveDifferentNamePage, false)
            .success
            .value
            .set(BusinessWithoutIdAddressPage, Address("line 1", None, "Line 3", None, None, Country("valid", "DE", "Germany")))
            .success
            .value

        navigator
          .nextPage(BusinessHaveDifferentNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from WhatIsTradingName page to BusinessWithoutIdAddress page when next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(WhatIsTradingNamePage, "Trading Name")
            .success
            .value

        navigator
          .nextPage(WhatIsTradingNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.BusinessWithoutIdAddressController.onPageLoad(CheckMode))
      }

      "must go from WhatIsTradingName page to CheckYourAnswers page when next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(WhatIsTradingNamePage, "Trading Name")
            .success
            .value
            .set(BusinessWithoutIdAddressPage, Address("line 1", None, "Line 3", None, None, Country("valid", "DE", "Germany")))
            .success
            .value

        navigator
          .nextPage(WhatIsTradingNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from BusinessWithoutIdAddress page to YourContactDetails page when next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(BusinessWithoutIdAddressPage, Address("line 1", None, "Line 3", None, None, Country("valid", "DE", "Germany")))
            .success
            .value

        navigator
          .nextPage(BusinessWithoutIdAddressPage, CheckMode, updatedAnswers)
          .mustBe(routes.YourContactDetailsController.onPageLoad(CheckMode))
      }

      "must go from BusinessWithoutIdAddress page to CheckYourAnswers page when next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(BusinessWithoutIdAddressPage, Address("line 1", None, "Line 3", None, None, Country("valid", "DE", "Germany")))
            .success
            .value
            .set(ContactNamePage, "Name")
            .success
            .value

        navigator
          .nextPage(BusinessWithoutIdAddressPage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from ContactName page to ContactEmail page when next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(ContactNamePage, "Name")
            .success
            .value

        navigator
          .nextPage(ContactNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.ContactEmailController.onPageLoad(CheckMode))
      }

      "must go from ContactName page to CheckYourAnswers page when next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(ContactNamePage, "Name")
            .success
            .value
            .set(ContactEmailPage, "test@test.com")
            .success
            .value

        navigator
          .nextPage(ContactNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from ContactEmailPage page to HaveTelephone page when next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(ContactEmailPage, "test@test.com")
            .success
            .value

        navigator
          .nextPage(ContactEmailPage, CheckMode, updatedAnswers)
          .mustBe(routes.HaveTelephoneController.onPageLoad(CheckMode))
      }

      "must go from ContactEmailPage page to CheckYourAnswers page when next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(ContactEmailPage, "test@test.com")
            .success
            .value
            .set(HaveTelephonePage, true)
            .success
            .value

        navigator
          .nextPage(ContactEmailPage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from HaveTelephone page to ContactPhone page if Yes is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(HaveTelephonePage, true)
            .success
            .value

        navigator
          .nextPage(HaveTelephonePage, CheckMode, updatedAnswers)
          .mustBe(routes.ContactPhoneController.onPageLoad(CheckMode))
      }

      "must go from HaveTelephone page to DoYouHaveSecondContact page if No is selected and next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(HaveTelephonePage, false)
            .success
            .value

        navigator
          .nextPage(HaveTelephonePage, CheckMode, updatedAnswers)
          .mustBe(routes.DoYouHaveSecondContactController.onPageLoad(CheckMode))
      }

      "must go from HaveTelephone page to CheckYourAnswers page if No is selected and next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(HaveTelephonePage, false)
            .success
            .value
            .set(DoYouHaveSecondContactPage, true)
            .success
            .value

        navigator
          .nextPage(HaveTelephonePage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from ContactPhonePage page to DoYouHaveSecondContact page when next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(ContactPhonePage, "0987654321")
            .success
            .value

        navigator
          .nextPage(ContactPhonePage, CheckMode, updatedAnswers)
          .mustBe(routes.DoYouHaveSecondContactController.onPageLoad(CheckMode))
      }

      "must go from ContactPhonePage page to CheckYourAnswers page when next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(ContactPhonePage, "0987654321")
            .success
            .value
            .set(DoYouHaveSecondContactPage, true)
            .success
            .value

        navigator
          .nextPage(ContactPhonePage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from DoYouHaveSecondContact page to SecondContactName page if Yes is selected and next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(DoYouHaveSecondContactPage, true)
            .success
            .value

        navigator
          .nextPage(DoYouHaveSecondContactPage, CheckMode, updatedAnswers)
          .mustBe(routes.SecondContactNameController.onPageLoad(CheckMode))
      }

      "must go from DoYouHaveSecondContact page to CheckYourAnswers page if Yes is selected and next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(DoYouHaveSecondContactPage, true)
            .success
            .value
            .set(SecondContactNamePage, "Name")
            .success
            .value

        navigator
          .nextPage(DoYouHaveSecondContactPage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from DoYouHaveSecondContact page to CheckYourAnswers page if No is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(DoYouHaveSecondContactPage, false)
            .success
            .value

        navigator
          .nextPage(DoYouHaveSecondContactPage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from SecondContactName page to SecondContactEmail page when next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(SecondContactNamePage, "Name")
            .success
            .value

        navigator
          .nextPage(SecondContactNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.SecondContactEmailController.onPageLoad(CheckMode))
      }

      "must go from SecondContactName page to CheckYourAnswers page when next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(SecondContactNamePage, "Name")
            .success
            .value
            .set(SecondContactEmailPage, "test@test.com")
            .success
            .value

        navigator
          .nextPage(SecondContactNamePage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from SecondContactEmail page to SecondContactHavePhone page when next page is not completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(SecondContactEmailPage, "test@test.com")
            .success
            .value

        navigator
          .nextPage(SecondContactEmailPage, CheckMode, updatedAnswers)
          .mustBe(routes.SecondContactHavePhoneController.onPageLoad(CheckMode))
      }

      "must go from SecondContactEmail page to CheckYourAnswers page when next page is completed" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(SecondContactEmailPage, "test@test.com")
            .success
            .value
            .set(SecondContactHavePhonePage, true)
            .success
            .value

        navigator
          .nextPage(SecondContactEmailPage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from SecondContactHavePhone page to SecondContactPhone page if Yes is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(SecondContactHavePhonePage, true)
            .success
            .value

        navigator
          .nextPage(SecondContactHavePhonePage, CheckMode, updatedAnswers)
          .mustBe(routes.SecondContactPhoneController.onPageLoad(CheckMode))
      }

      "must go from SecondContactHavePhone page to CheckYourAnswers page if No is selected" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(SecondContactHavePhonePage, false)
            .success
            .value

        navigator
          .nextPage(SecondContactHavePhonePage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }

      "must go from SecondContactPhone page to CheckYourAnswers page" in {
        val updatedAnswers =
          emptyUserAnswers
            .set(SecondContactPhonePage, "0987654321")
            .success
            .value

        navigator
          .nextPage(SecondContactPhonePage, CheckMode, updatedAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad())
      }
    }
  }
}
