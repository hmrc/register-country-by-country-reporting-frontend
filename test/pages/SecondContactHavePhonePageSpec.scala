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

class SecondContactHavePhonePageSpec extends PageBehaviours {

  "SecondContactHavePhonePage" - {

    beRetrievable[Boolean](SecondContactHavePhonePage)

    beSettable[Boolean](SecondContactHavePhonePage)

    beRemovable[Boolean](SecondContactHavePhonePage)
  }

  "clean up" - {
    "remove Second ContactTelephone if user answers not to have second contact telephone" in {
      val userAnswersId: String = "id"
      val userAnswers = UserAnswers(userAnswersId)
        .set(SecondContactPhonePage, "1234567890")
        .success
        .value
        .set(SecondContactHavePhonePage, false)
        .success
        .value

      userAnswers.get(SecondContactPhonePage) must not be defined
      userAnswers.get(SecondContactHavePhonePage) mustBe Some(false)
    }
    "leave Second ContactTelephone if user answers yes to have second contact telephone" in {
      val userAnswersId: String = "id"
      val userAnswers = UserAnswers(userAnswersId)
        .set(SecondContactPhonePage, "1234567890")
        .success
        .value
        .set(SecondContactHavePhonePage, true)
        .success
        .value

      userAnswers.get(SecondContactPhonePage) mustBe Some("1234567890")
      userAnswers.get(SecondContactHavePhonePage) mustBe Some(true)
    }
  }
}
