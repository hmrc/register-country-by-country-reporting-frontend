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

package controllers

import base.SpecBase
import config.FrontendAppConfig
import forms.BusinessWithoutIdAddressFormProvider
import models.{Address, Country, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import pages.BusinessWithoutIdAddressPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CountryListFactory
import views.html.BusinessWithoutIdAddressView

import scala.concurrent.Future

class BusinessWithoutIdAddressControllerSpec extends SpecBase {

  val testCountryList     = Seq(Country("valid", "GG", "Guernsey"))
  val formProvider        = new BusinessWithoutIdAddressFormProvider()
  val form: Form[Address] = formProvider(testCountryList)
  val address: Address    = Address("value 1", Some("value 2"), "value 3", Some("value 4"), Some("XX9 9XX"), Country("valid", "GG", "Guernsey"))

  val mockAppConfig = mock[FrontendAppConfig]

  val countryListFactory = new CountryListFactory(app.environment, mockAppConfig) {
    override lazy val countryList: Option[Seq[Country]] = Some(testCountryList)
  }

  lazy val businessWithoutIdAddressRoute = routes.BusinessWithoutIdAddressController.onPageLoad(NormalMode).url

  val userAnswers = UserAnswers(userAnswersId).set(BusinessWithoutIdAddressPage, address).success.value

  "BusinessWithoutIdAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[CountryListFactory].to(countryListFactory)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, businessWithoutIdAddressRoute)

        val view = application.injector.instanceOf[BusinessWithoutIdAddressView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, countryListFactory.countrySelectList(form.data, testCountryList), NormalMode)(request,
                                                                                                                                   messages(application)
        ).toString
        contentAsString(result) must include("Select a country")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[CountryListFactory].to(countryListFactory)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, businessWithoutIdAddressRoute)

        val view = application.injector.instanceOf[BusinessWithoutIdAddressView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(address), countryListFactory.countrySelectList(form.data, testCountryList), NormalMode)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, businessWithoutIdAddressRoute)
            .withFormUrlEncodedBody(("addressLine1", "value 1"),
                                    ("addressLine2", "value 2"),
                                    ("addressLine3", "value 2"),
                                    ("addressLine4", "value 2"),
                                    ("postCode", "NE98 1ZZ"),
                                    ("country", "GG")
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[CountryListFactory].to(countryListFactory)
        )
        .build()

      running(application) {
        val request =
          FakeRequest(POST, businessWithoutIdAddressRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[BusinessWithoutIdAddressView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, countryListFactory.countrySelectList(form.data, testCountryList), NormalMode)(request,
                                                                                                                                        messages(application)
        ).toString
      }
    }

    "must redirect to sign out page for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, businessWithoutIdAddressRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.auth.routes.AuthController.signOutNoSurvey().url
      }
    }

    "must redirect to sign out page for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, businessWithoutIdAddressRoute)
            .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.auth.routes.AuthController.signOutNoSurvey().url
      }
    }
  }
}
