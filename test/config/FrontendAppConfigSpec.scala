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

package config

import base.SpecBase
import controllers.routes
import play.api.test.FakeRequest
import play.api.test.Helpers.GET

class FrontendAppConfigSpec extends SpecBase {

  "FrontendAppConfig" - {
    "feedbackUrl should include backUrl when request uri does not contain 'there-is-a-problem'" in {

      val application = applicationBuilder(None).build()
      val appConfig   = application.injector.instanceOf[FrontendAppConfig]
      val request     = FakeRequest(GET, routes.RegistrationConfirmationController.onPageLoad().url)
      appConfig.feedbackUrl(request) must include("&backUrl=")
    }

    "feedbackUrl should not include backUrl when request uri does contain 'there-is-a-problem'" in {

      val application = applicationBuilder(None).build()
      val appConfig   = application.injector.instanceOf[FrontendAppConfig]
      val request     = FakeRequest(GET, routes.ThereIsAProblemController.onPageLoad().url)
      appConfig.feedbackUrl(request) mustNot include("&backUrl=")
    }
  }
}
