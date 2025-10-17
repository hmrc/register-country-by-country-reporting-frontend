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
import pages._
import play.api.http.Status._
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ISpecBehaviours;

class CheckYourAnswersControllerISpec extends ISpecBehaviours {

  private val getUserAnswers = UserAnswers("internalId")
    .set(IsRegisteredAddressInUkPage, false)
    .get
    .set(DoYouHaveUTRPage, false)
    .get
    .set(ContactNamePage, "testFirstName")
    .get
    .set(ContactEmailPage, "testEmail@test.com")
    .get
    .set(HaveTelephonePage, false)
    .get
    .set(DoYouHaveSecondContactPage, false)
    .get

  private val pageUrl = Some("/check-answers")

  "GET / CheckYourAnswersController.onPageLoad" must {

    behave like standardOnPageLoad(pageUrl)

    "should load page" in {
      stubAuthorised(appId = None)

      repository.set(getUserAnswers)

      val response = await(
        buildClient(pageUrl)
          .withFollowRedirects(false)
          .addCookies(wsSessionCookie)
          .get()
      )

      response.status mustBe OK
      response.body must include("Check your details before you register")
    }
  }
  "POST / BusinessWithoutIdAddressController.onSubmit" must {
    val address = arbitraryBusinessWithoutIdAddress.arbitrary.sample.get
    val userAnswers = UserAnswers("internalId")
      .set(BusinessWithoutIDNamePage, "testBusiness")
      .get
      .set(BusinessWithoutIdAddressPage, address)
      .get
      .set(IsRegisteredAddressInUkPage, false)
      .get
      .set(DoYouHaveUTRPage, false)
      .get
      .set(ContactNamePage, "testFirstName")
      .get
      .set(ContactEmailPage, "testEmail@test.com")
      .get
      .set(HaveTelephonePage, false)
      .get
      .set(DoYouHaveSecondContactPage, false)
      .get

    val requestBody = Map("value" -> Seq("businessName"))

    behave like standardOnSubmit(pageUrl, requestBody)

    "should submit form" in {
      stubAuthorised(appId = None)
      stubRegisterCBC()
      stubRegisterationReadSubscription()
      stubEnrolmentGetEnrolment()
      stubCreateEnrolment()

      repository.set(userAnswers)

      val response = await(
        buildClient(pageUrl)
          .addCookies(wsSessionCookie)
          .addHttpHeaders("Csrf-Token" -> "nocheck")
          .withFollowRedirects(false)
          .post(requestBody)
      )

      response.status mustBe SEE_OTHER
      response.header("Location").value must include("/register-to-send-a-country-by-country-report/register/confirm-registration")
    }
  }
}
