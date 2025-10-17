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
import models.{UniqueTaxpayerReference, UserAnswers}
import pages.{BusinessNamePage, BusinessTypePage, UTRPage}
import play.api.http.Status.OK
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ISpecBehaviours

class IsThisYourBusinessControllerISpec extends ISpecBehaviours {

  val requestBody: Map[String, Seq[String]] = Map("value" -> Seq("true"))
  val pageUrl: Option[String]               = Some("/register/is-this-your-business")

  "IsThisYourBusinessController" must {
    val ua: UserAnswers = UserAnswers("internalId")
      .withPage(BusinessTypePage, LimitedCompany)
      .withPage(UTRPage, UniqueTaxpayerReference("testUtr"))
      .withPage(BusinessNamePage, "Business Name")

    "load relative page" in {
      stubAuthorised(appId = None)
      stubRegisterCBCwithUtr()
      await(repository.set(ua))

      val response = await(
        buildClient(pageUrl)
          .withFollowRedirects(false)
          .addCookies(wsSessionCookie)
          .get()
      )
      val loc = response.header("Location").getOrElse("NO REDIRECT")
      if (loc != "NO REDIRECT") loc must include("/send-a-country-by-country-report")
      response.status mustBe OK

    }

    behave like standardOnPageLoadRedirects(pageUrl)

    behave like standardOnSubmit(pageUrl, requestBody)
  }
}
