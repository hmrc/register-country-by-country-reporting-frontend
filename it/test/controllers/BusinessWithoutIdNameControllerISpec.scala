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
import play.api.http.Status._
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ISpecBehaviours;

class BusinessWithoutIdNameControllerISpec extends ISpecBehaviours {

  private val userAnswers = UserAnswers("internalId")
  private val pageUrl     = Some("/register/without-id/business-name")

  "GET / BusinessWithoutIdAddressController.onPageLoad" must {

    behave like standardOnPageLoad(pageUrl)

    "should load page" in {
      stubAuthorised(appId = None)

      repository.set(userAnswers)

      val response = await(
        buildClient(pageUrl)
          .withFollowRedirects(false)
          .addCookies(wsSessionCookie)
          .get()
      )

      response.status mustBe OK
      response.body must include("What is the name of your business?")
    }
  }
  "POST / BusinessWithoutIdAddressController.onSubmit" must {
    val requestBody = Map("value" -> Seq("businessName"))

    behave like standardOnSubmit(pageUrl, requestBody)

    "should submit form" in {
      stubAuthorised(appId = None)

      repository.set(userAnswers)

      val response = await(
        buildClient(pageUrl)
          .addCookies(wsSessionCookie)
          .addHttpHeaders("Csrf-Token" -> "nocheck")
          .withFollowRedirects(false)
          .post(requestBody)
      )

      response.status mustBe SEE_OTHER
      response.header("Location").value must include("/register/without-id/have-trading-name")
    }
  }
}
