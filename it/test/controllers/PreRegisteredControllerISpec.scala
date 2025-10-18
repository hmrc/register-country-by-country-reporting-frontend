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
import play.api.http.Status.OK
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ISpecBehaviours

class PreRegisteredControllerISpec extends ISpecBehaviours {

  "load without utr page" in {
    stubAuthorised(appId = None)

    await(repository.set(UserAnswers("internalId")))

    val response = await(
      buildClient(Some("/register/problem/organisation-without-utr-pre-registered"))
        .addCookies(wsSessionCookie)
        .get()
    )
    response.status mustBe OK
    response.body must include(messages("preRegistered.title"))

  }
  "load with-utr page" in {
    stubAuthorised(appId = None)

    await(repository.set(UserAnswers("internalId")))

    val response = await(
      buildClient(Some("/register/problem/organisation-with-utr-pre-registered"))
        .addCookies(wsSessionCookie)
        .get()
    )
    response.status mustBe OK
    response.body must include(messages("preRegistered.title"))

  }
}
