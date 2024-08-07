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

class DoYouHaveSecondContactPageSpec extends PageBehaviours {

  "DoYouHaveSecondContactPage" - {

    beRetrievable[Boolean](DoYouHaveSecondContactPage)

    beSettable[Boolean](DoYouHaveSecondContactPage)

    beRemovable[Boolean](DoYouHaveSecondContactPage)
  }

  "cleanup" - {
    "must clean up Second contact pages when user chooses No to have second contact" in {
      val userAnswersId: String = "id"
      val userAnswers = UserAnswers(userAnswersId)
        .set(SecondContactNamePage, "Company")
        .success
        .value
        .set(SecondContactEmailPage, "test@test.com")
        .success
        .value
        .set(SecondContactHavePhonePage, true)
        .success
        .value
        .set(SecondContactPhonePage, "1234567890")
        .success
        .value
        .set(DoYouHaveSecondContactPage, false)
        .success
        .value

      userAnswers.get(SecondContactNamePage) must not be defined
      userAnswers.get(SecondContactEmailPage) must not be defined
      userAnswers.get(SecondContactHavePhonePage) must not be defined
      userAnswers.get(SecondContactPhonePage) must not be defined
      userAnswers.get(DoYouHaveSecondContactPage) mustBe Some(false)
    }
  }
}
