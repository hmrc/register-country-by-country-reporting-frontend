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
