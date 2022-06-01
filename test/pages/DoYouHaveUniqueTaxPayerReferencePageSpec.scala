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

package pages

import  models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class doYouHaveUTRPageSpec extends PageBehaviours {

//  private val address =
//    Address("He lives in a house", Some("a very big house"), "In the country", Some("blur 1995"), Some("BritPop"), Country("", "GB", "Great Britain"))
//
//  private val addressLookup = AddressLookup(
//    Some("Your house was very small"),
//    Some("with woodchip on the wall"),
//    Some("and when I came round to call"),
//    Some("you didn't notice me at all"),
//    "Pulp 1995",
//    Some("BritPop"),
//    "D3B0R4H"
//  )
//
//  "doYouHaveUTRPage" - {
//
//    beRetrievable[Boolean](doYouHaveUTRPage)
//
//    beSettable[Boolean](doYouHaveUTRPage)
//
//    beRemovable[Boolean](doYouHaveUTRPage)
//  }
//
//  "cleanup" - {
//
//    "must remove business pages when user selects no to do you have a utr?" in {
//      forAll(arbitrary[UserAnswers]) {
//        userAnswers =>
//          val result = userAnswers
//            .set(BusinessTypePage, LimitedCompany)
//            .success
//            .value
//            .set(UTRPage, UniqueTaxpayerReference("123456789"))
//            .success
//            .value
//            .set(BusinessNamePage, "businessName")
//            .success
//            .value
//            .set(SoleNamePage, Name("Sole", "Trader"))
//            .success
//            .value
//            .set(IsThisYourBusinessPage, true)
//            .success
//            .value
//            .set(AddressLookupPage, Seq(addressLookup))
//            .success
//            .value
//            .set(AddressUKPage, address)
//            .success
//            .value
//            .set(ContactNamePage, "SomeContact")
//            .success
//            .value
//            .set(ContactEmailPage, "contact@email.com")
//            .success
//            .value
//            .set(IsContactTelephonePage, true)
//            .success
//            .value
//            .set(ContactPhonePage, "07540000000")
//            .success
//            .value
//            .set(SecondContactPage, true)
//            .success
//            .value
//            .set(SndContactNamePage, "SomeSecondContact")
//            .success
//            .value
//            .set(SndContactEmailPage, "secondcontact@email.com")
//            .success
//            .value
//            .set(SndConHavePhonePage, true)
//            .success
//            .value
//            .set(SndContactPhonePage, "07540000000")
//            .success
//            .value
//            .set(RegistrationInfoPage, OrgRegistrationInfo(SafeId("safeId"), "Organisation", AddressResponse("Address", None, None, None, None, "GB")))
//            .success
//            .value
//            .set(doYouHaveUTRPage, false)
//            .success
//            .value
//
//          result.get(BusinessTypePage) must not be defined
//          result.get(UTRPage) must not be defined
//          result.get(BusinessNamePage) must not be defined
//          result.get(SoleNamePage) must not be defined
//          result.get(IsThisYourBusinessPage) must not be defined
//          result.get(AddressLookupPage) must not be defined
//          result.get(AddressUKPage) must not be defined
//          result.get(ContactNamePage) must not be defined
//          result.get(ContactEmailPage) must not be defined
//          result.get(IsContactTelephonePage) must not be defined
//          result.get(ContactPhonePage) must not be defined
//          result.get(SecondContactPage) must not be defined
//          result.get(SndContactNamePage) must not be defined
//          result.get(SndContactEmailPage) must not be defined
//          result.get(SndConHavePhonePage) must not be defined
//          result.get(SndContactPhonePage) must not be defined
//          result.get(RegistrationInfoPage) must not be defined
//          result.get(BusinessWithoutIDNamePage) must not be defined
//          result.get(BusinessHaveDifferentNamePage) must not be defined
//          result.get(WhatIsTradingNamePage) must not be defined
//      }
//    }
//
//    "must remove individual pages when user selects YES to do you have a utr?" in {
//      forAll(arbitrary[UserAnswers]) {
//        userAnswers =>
//          val result = userAnswers
//            .set(WhatAreYouRegisteringAsPage, RegistrationTypeIndividual)
//            .success
//            .value
//            .set(WhatIsYourNationalInsuranceNumberPage, Nino("AA123456A"))
//            .success
//            .value
//            .set(WhatIsYourNamePage, Name("Some", "name"))
//            .success
//            .value
//            .set(WhatIsYourDateOfBirthPage, LocalDate.now())
//            .success
//            .value
//            .set(DateOfBirthWithoutIdPage, LocalDate.now())
//            .success
//            .value
//            .set(DoYouHaveNINPage, true)
//            .success
//            .value
//            .set(NonUkNamePage, NonUkName("Some", "name"))
//            .success
//            .value
//            .set(DoYouLiveInTheUKPage, true)
//            .success
//            .value
//            .set(WhatIsYourPostcodePage, "NE320AA")
//            .success
//            .value
//            .set(BusinessAddressWithoutIdPage, address)
//            .success
//            .value
//            .set(IndividualAddressWithoutIdPage, address)
//            .success
//            .value
//            .set(AddressLookupPage, Seq(addressLookup))
//            .success
//            .value
//            .set(AddressUKPage, address)
//            .success
//            .value
//            .set(SelectAddressPage, "true")
//            .success
//            .value
//            .set(SelectedAddressLookupPage, addressLookup)
//            .success
//            .value
//            .set(RegistrationInfoPage, OrgRegistrationInfo(SafeId("safeId"), "Organisation", AddressResponse("Address", None, None, None, None, "GB")))
//            .success
//            .value
//            .set(doYouHaveUTRPage, true)
//            .success
//            .value
//
//          result.get(WhatAreYouRegisteringAsPage) must not be defined
//          result.get(WhatIsYourNationalInsuranceNumberPage) must not be defined
//          result.get(WhatIsYourNamePage) must not be defined
//          result.get(WhatIsYourDateOfBirthPage) must not be defined
//          result.get(DateOfBirthWithoutIdPage) must not be defined
//          result.get(DoYouHaveNINPage) must not be defined
//          result.get(NonUkNamePage) must not be defined
//          result.get(DoYouLiveInTheUKPage) must not be defined
//          result.get(WhatIsYourPostcodePage) must not be defined
//          result.get(BusinessAddressWithoutIdPage) must not be defined
//          result.get(IndividualAddressWithoutIdPage) must not be defined
//          result.get(AddressLookupPage) must not be defined
//          result.get(AddressUKPage) must not be defined
//          result.get(SelectAddressPage) must not be defined
//          result.get(SelectedAddressLookupPage) must not be defined
//          result.get(RegistrationInfoPage) must not be defined
//      }
//    }
//  }
  //TODO Bring back in once all pages are implemented
}
