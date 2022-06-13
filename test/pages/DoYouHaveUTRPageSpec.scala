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

import models.BusinessType.LimitedCompany
import models.matching.RegistrationInfo
import models.register.response.details.AddressResponse
import models.{Address, Country, SafeId, UserAnswers}
import pages.behaviours.PageBehaviours

class DoYouHaveUTRPageSpec extends PageBehaviours {

  "DoYouHaveUTRPage" - {

    beRetrievable[Boolean](DoYouHaveUTRPage)

    beSettable[Boolean](DoYouHaveUTRPage)

    beRemovable[Boolean](DoYouHaveUTRPage)
  }

  "cleanup" - {

    val businessAddress = Address("", None, "", None, None, Country("valid", "GB", "United Kingdom"))

    "must remove 'Without Id' pages when user selects yes to do you have a utr?" in {
      val userAnswersId: String = "id"
      val userAnswers = UserAnswers(userAnswersId)
        .set(BusinessWithoutIDNamePage, "Company")
        .success
        .value
        .set(BusinessHaveDifferentNamePage, true)
        .success
        .value
        .set(WhatIsTradingNamePage, "Company 2")
        .success
        .value
        .set(BusinessWithoutIdAddressPage, businessAddress)
        .success
        .value
        .set(DoYouHaveUTRPage, true)
        .success
        .value

        userAnswers.get(BusinessWithoutIDNamePage) must not be defined
        userAnswers.get(BusinessHaveDifferentNamePage) must not be defined
        userAnswers.get(WhatIsTradingNamePage) must not be defined
        userAnswers.get(BusinessWithoutIdAddressPage) must not be defined
        userAnswers.get(DoYouHaveUTRPage) mustBe Some(true)


    }
    "must remove 'With Id' pages when user selects no to do you have a utr?" in {
      val userAnswersId: String = "id"
      val userAnswers = UserAnswers(userAnswersId)
        .set(BusinessTypePage, LimitedCompany)
        .success
        .value
        .set(UTRPage, "1234567890")
        .success
        .value
        .set(BusinessNamePage, "Company 2")
        .success
        .value
        .set(IsThisYourBusinessPage, true)
        .success
        .value
        .set(RegistrationInfoPage, RegistrationInfo(SafeId("x"), "Company", AddressResponse("",None,None,None,None,"GB")))
        .success
        .value
        .set(DoYouHaveUTRPage, false)
        .success
        .value

        userAnswers.get(BusinessTypePage) must not be defined
        userAnswers.get(UTRPage) must not be defined
        userAnswers.get(BusinessNamePage) must not be defined
        userAnswers.get(IsThisYourBusinessPage) must not be defined
        userAnswers.get(RegistrationInfoPage) must not be defined
        userAnswers.get(DoYouHaveUTRPage) mustBe Some(false)


    }
    }
}
