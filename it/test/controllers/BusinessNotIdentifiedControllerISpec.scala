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

import models.BusinessType.LimitedCompany
import models.UserAnswers
import org.scalatestplus.play.PlaySpec
import pages.BusinessTypePage
import play.api.http.Status._
import play.api.libs.ws.{DefaultWSCookie, WSClient}
import play.api.mvc.{Session, SessionCookieBaker}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ISpecBase;

class BusinessNotIdentifiedControllerISpec extends PlaySpec with ISpecBase {

  lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]
  val session                 = Session(Map("authToken" -> "abc123"))
  val sessionCookieBaker      = app.injector.instanceOf[SessionCookieBaker]
  val sessionCookie           = sessionCookieBaker.encodeAsCookie(session)
  val wsSessionCookie         = DefaultWSCookie(sessionCookie.name, sessionCookie.value)

  private val userAnswers = UserAnswers("internalId").set(BusinessTypePage, LimitedCompany).get
  "GET / BusinessNotIdentifiedController.onPageLoad" must {
    "should load page" in {
      stubAuthorised(appId = None)

      repository.set(userAnswers)

      val response = await(
        buildClient(Some("/register/problem/business-not-identified"))
          .withFollowRedirects(false)
          .addCookies(wsSessionCookie)
          .get()
      )

      response.status mustBe OK
      response.body must include("The details you entered did not match our records")
    }

    "redirect to login when there is no active session" in {
      val response = await(
        buildClient(Some("/register/problem/business-not-identified"))
          .withFollowRedirects(false)
          .get()
      )

      response.status mustBe SEE_OTHER
      response.header("Location").value must include("gg-sign-in")
    }

    "redirect to /individual-sign-in-problem" in {
      stubAuthorisedIndividual("cbc12345")
      val response = await(
        buildClient(Some("/register/problem/business-not-identified"))
          .withFollowRedirects(false)
          .addCookies(wsSessionCookie)
          .get()
      )

      response.status mustBe SEE_OTHER
      response.header("Location").value must include("register/problem/individual-sign-in-problem")
      verifyPost(authUrl)
    }
  }
}
