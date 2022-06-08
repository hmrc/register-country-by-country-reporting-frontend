package controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.UnauthorisedIndividualView

class UnauthorisedIndividualControllerSpec extends SpecBase {

  "UnauthorisedIndividual Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.UnauthorisedIndividualController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[UnauthorisedIndividualView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }
  }
}
