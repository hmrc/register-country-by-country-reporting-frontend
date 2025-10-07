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
import org.scalatestplus.play.PlaySpec
import play.api.http.Status._
import play.api.libs.ws.{DefaultWSCookie, WSClient}
import play.api.mvc.{Session, SessionCookieBaker}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ISpecBase;

class BusinessHaveDifferentNameControllerISpec extends PlaySpec with ISpecBase {

  lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]
  val session                 = Session(Map("authToken" -> "abc123"))
  val sessionCookieBaker      = app.injector.instanceOf[SessionCookieBaker]
  val sessionCookie           = sessionCookieBaker.encodeAsCookie(session)
  val wsSessionCookie         = DefaultWSCookie(sessionCookie.name, sessionCookie.value)

  "GET / BusinessHaveDifferentNameController.onPageLoad" must {
    "should load page" in {
      stubAuthorised(appId = None)

      repository.set(UserAnswers("internalId"))

      val response = await(
        buildClient(Some("/register/without-id/have-trading-name"))
          .withFollowRedirects(false)
          .addCookies(wsSessionCookie)
          .get()
      )

      response.status mustBe OK
      response.body must include("Does your business trade under a different name?")
    }

    "redirect to login when there is no active session" in {
      val response = await(
        buildClient(Some("/register/without-id/have-trading-name"))
          .withFollowRedirects(false)
          .get()
      )

      response.status mustBe SEE_OTHER
      response.header("Location").value must include("gg-sign-in")
    }

    "redirect to /individual-sign-in-problem" in {
      stubAuthorisedIndividual("cbc12345")
      val response = await(
        buildClient(Some("/register/without-id/have-trading-name"))
          .withFollowRedirects(false)
          .addCookies(wsSessionCookie)
          .get()
      )

      response.status mustBe SEE_OTHER
      response.header("Location").value must include("register/problem/individual-sign-in-problem")
      verifyPost(authUrl)
    }
  }
  "POST / BusinessHaveDifferentNameController.onSubmit" must {
    "should submit form" in {
      stubAuthorised(appId = None)

      repository.set(UserAnswers("internalId"))

      val response = await(
        buildClient(Some("/register/without-id/have-trading-name"))
          .addCookies(wsSessionCookie)
          .addHttpHeaders("Csrf-Token" -> "nocheck")
          .withFollowRedirects(true)
          .post(Map("value" -> Seq("false")))
      )

      response.status mustBe OK
    }

    "redirect to login when there is no active session" in {

      val response = await(
        buildClient(Some("/register/without-id/have-trading-name"))
          .addHttpHeaders("Csrf-Token" -> "nocheck")
          .withFollowRedirects(true)
          .post(Map("value" -> Seq("false")))
      )

      response.status mustBe OK
      response.body must include("gg-sign-in")
    }

    "redirect to /individual-sign-in-problem" in {

      stubAuthorisedIndividual("cbc12345")

      val response = await(
        buildClient(Some("/register/without-id/have-trading-name"))
          .addHttpHeaders("Csrf-Token" -> "nocheck")
          .withFollowRedirects(true)
          .addCookies(wsSessionCookie)
          .post(Map("value" -> Seq("false")))
      )

      response.status mustBe OK
      response.body must include("You’ve signed in as an individual. Only organisations can send reports.")
    }
  }
}
