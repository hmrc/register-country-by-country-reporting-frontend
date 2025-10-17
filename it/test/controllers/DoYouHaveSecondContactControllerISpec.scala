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

package controllers

import models.UserAnswers
import pages.ContactNamePage
import play.api.http.Status.OK
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ISpecBehaviours

class DoYouHaveSecondContactControllerISpec extends ISpecBehaviours {

  val requestBody: Map[String, Seq[String]] = Map("value" -> Seq("true"))
  val pageUrl: Option[String]               = Some("/register/have-second-contact")

  "DoYouHaveSecondContactController" must {
    "load relative page" in {
      stubAuthorised(appId = None)
      val userAnswers = UserAnswers("internalId").withPage(ContactNamePage, "testName")

      repository.set(userAnswers)

      val response = await(
        buildClient(pageUrl)
          .withFollowRedirects(false)
          .addCookies(wsSessionCookie)
          .get()
      )

      response.status mustBe OK

    }
    behave like standardOnPageLoadRedirects(pageUrl)

    behave like standardOnSubmit(pageUrl, requestBody)
  }

}
