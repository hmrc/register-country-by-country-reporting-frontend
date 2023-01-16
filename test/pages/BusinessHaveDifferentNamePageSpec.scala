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

package pages

import models.UserAnswers
import pages.behaviours.PageBehaviours

class BusinessHaveDifferentNamePageSpec extends PageBehaviours {

  "BusinessHaveDifferentNamePage" - {

    beRetrievable[Boolean](BusinessHaveDifferentNamePage)

    beSettable[Boolean](BusinessHaveDifferentNamePage)

    beRemovable[Boolean](BusinessHaveDifferentNamePage)
  }

  "clean up" - {
    "remove Business trading name if user answers not to have a different trading name" in {
      val userAnswersId: String = "id"
      val userAnswers = UserAnswers(userAnswersId)
        .set(WhatIsTradingNamePage, "Company")
        .success
        .value
        .set(BusinessHaveDifferentNamePage, false)
        .success
        .value

      userAnswers.get(WhatIsTradingNamePage) must not be defined
      userAnswers.get(BusinessHaveDifferentNamePage) mustBe Some(false)
    }
    "leave ContactTelephone if user answers yes to have telephone" in {
      val userAnswersId: String = "id"
      val userAnswers = UserAnswers(userAnswersId)
        .set(WhatIsTradingNamePage, "Company")
        .success
        .value
        .set(BusinessHaveDifferentNamePage, true)
        .success
        .value

      userAnswers.get(WhatIsTradingNamePage) mustBe Some("Company")
      userAnswers.get(BusinessHaveDifferentNamePage) mustBe Some(true)
    }
  }
}
