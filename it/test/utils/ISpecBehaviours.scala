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

package utils

import org.scalatestplus.play.PlaySpec
import play.api.http.Status._
import play.api.libs.ws.{DefaultWSCookie, WSClient}
import play.api.mvc.{Cookie, Session, SessionCookieBaker}
import play.api.test.Helpers.{await, defaultAwaitTimeout}

trait ISpecBehaviours extends PlaySpec with ISpecBase {

  lazy val wsClient: WSClient                = app.injector.instanceOf[WSClient]
  val session: Session                       = Session(Map("authToken" -> "abc123"))
  val sessionCookieBaker: SessionCookieBaker = app.injector.instanceOf[SessionCookieBaker]
  val sessionCookie: Cookie                  = sessionCookieBaker.encodeAsCookie(session)
  val wsSessionCookie: DefaultWSCookie       = DefaultWSCookie(sessionCookie.name, sessionCookie.value)

  def standardOnPageLoad(pageUrl: Option[String]): Unit = {

    "redirect to cbc reporting when the user is automatched" in {
      stubAuthorised(Some("cbc12345"))

      val response = await(
        buildClient(pageUrl)
          .withFollowRedirects(false)
          .addCookies(wsSessionCookie)
          .get()
      )

      val loc = response.header("Location").value
      response.status mustBe SEE_OTHER
      loc must include("/send-a-country-by-country-report")
      verifyPost(authUrl)
    }

    "redirect to login when there is no active session" in {
      val response = await(
        buildClient(pageUrl)
          .withFollowRedirects(false)
          .get()
      )

      response.status mustBe SEE_OTHER
      response.header("Location").value must include("gg-sign-in")
    }

    "redirect to /individual-sign-in-problem" in {
      stubAuthorisedIndividual("cbc12345")
      val response = await(
        buildClient(pageUrl)
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
