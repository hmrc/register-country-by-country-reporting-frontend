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

package controllers.organisation


import models.UserAnswers
import models.matching.RegistrationInfo
import models.register.response.details.AddressResponse
import pages.RegistrationInfoPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.i18n.Messages
import views.html.DifferentBusinessView
import base.SpecBase
import models.SafeId
class DifferentBusinessControllerSpec extends SpecBase {

  lazy val loginURL: String = "http://localhost:9949/auth-login-stub/gg-sign-in"
  val SafeIdValue = "XCBC0000123456789"
  val safeId: SafeId = SafeId(SafeIdValue)
  val OrgName             = "Some Test Org"

  val userAnswers: UserAnswers =
    emptyUserAnswers
      .set(RegistrationInfoPage, RegistrationInfo(safeId, OrgName, AddressResponse("line1", None, None, None, None, "")))
      .success
      .value

  "DifferentBusiness Controller" - {

    "must return OK and the correct view for a GET" in {

      implicit val request = FakeRequest(GET, controllers.routes.DifferentBusinessController.onPageLoad().url)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val result = route(application, request).value

        val view = application.injector.instanceOf[DifferentBusinessView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(loginURL, Some(OrgName), Some(List("line1", "")))(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET with no registrationInfo set" in {

      implicit val request = FakeRequest(GET, controllers.routes.DifferentBusinessController.onPageLoad().url)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val result = route(application, request).value

        val view = application.injector.instanceOf[DifferentBusinessView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(loginURL, None, None)(request, messages(application)).toString
      }
    }
  }

}
