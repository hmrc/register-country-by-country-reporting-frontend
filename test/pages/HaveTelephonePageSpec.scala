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

package pages

import models.UserAnswers
import pages.behaviours.PageBehaviours

class HaveTelephonePageSpec extends PageBehaviours {

  "HaveTelephonePage" - {

    beRetrievable[Boolean](HaveTelephonePage)

    beSettable[Boolean](HaveTelephonePage)

    beRemovable[Boolean](HaveTelephonePage)
  }

  "clean up" - {
    "remove ContactTelephone if user answers not to have telephone" in {
      val userAnswersId: String = "id"
      val userAnswers = UserAnswers(userAnswersId)
        .set(ContactPhonePage, "1234567890")
        .success
        .value
        .set(HaveTelephonePage, false)
        .success
        .value

      userAnswers.get(ContactPhonePage) must not be defined
      userAnswers.get(HaveTelephonePage) mustBe Some(false)
    }
    "leave ContactTelephone if user answers yes to have telephone" in {
      val userAnswersId: String = "id"
      val userAnswers = UserAnswers(userAnswersId)
        .set(ContactPhonePage, "1234567890")
        .success
        .value
        .set(HaveTelephonePage, true)
        .success
        .value

      userAnswers.get(ContactPhonePage) mustBe Some("1234567890")
      userAnswers.get(HaveTelephonePage) mustBe Some(true)
    }
  }
}
