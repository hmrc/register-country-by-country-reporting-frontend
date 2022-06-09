/*
 * Copyright 2022 HM Revenue & Customs
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

import base.SpecBase
import models.BusinessType.{LimitedCompany, LimitedPartnership}
import models.UserAnswers
import pages.BusinessTypePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.BusinessNotIdentifiedView

class BusinessNotIdentifiedControllerSpec extends SpecBase {

  "BusinessNotIdentified Controller" - {

    "must return OK and the correct view for a GET with link for corporation tax enquiries" in {

      val userAnswers = UserAnswers(userAnswersId).set(BusinessTypePage, LimitedCompany).success.value
      val  corporationTaxEnquiries = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/corporation-tax-enquiries"

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.BusinessNotIdentifiedController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[BusinessNotIdentifiedView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(corporationTaxEnquiries)(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET with link for self assessment enquiries" in {

      val userAnswers = UserAnswers(userAnswersId).set(BusinessTypePage, LimitedPartnership).success.value
      val selfAssessmentEnquiries = "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/self-assessment"

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.BusinessNotIdentifiedController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[BusinessNotIdentifiedView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(selfAssessmentEnquiries)(request, messages(application)).toString
      }
    }
  }
}
