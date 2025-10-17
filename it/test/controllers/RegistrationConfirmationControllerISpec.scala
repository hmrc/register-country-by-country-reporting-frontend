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

import models.{SubscriptionID, UserAnswers}
import pages.SubscriptionIDPage
import play.api.http.Status.OK
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ISpecBehaviours

class RegistrationConfirmationControllerISpec extends ISpecBehaviours {

  val pageUrl: Option[String] = Some("/register/confirm-registration")

  "RegistrationConfirmationController" must {

    "should load page" in {
      stubAuthorised(appId = None)

      val subscriptionID = SubscriptionID("xxx200")
      val answers        = UserAnswers("internalId").set(SubscriptionIDPage, subscriptionID).get
      repository.set(answers)

      val response = await(
        buildClient(pageUrl)
          .withFollowRedirects(false)
          .addCookies(wsSessionCookie)
          .get()
      )

      response.status mustBe OK
      response.body must include("Registration successful")
    }
  }

}
