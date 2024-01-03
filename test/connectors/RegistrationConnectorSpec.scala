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

package connectors

import base.{SpecBase, WireMockServerHandler}
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.register.request._
import models.register.response.RegisterWithIDResponse
import models.register.response.details.{AddressResponse, OrganisationResponse}
import models.{InternalServerError, NotFoundError, SafeId}
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global

class RegistrationConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks with JsonFixture {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.register-country-by-country.port" -> server.port()
    )
    .build()

  lazy val connector: RegistrationConnector = app.injector.instanceOf[RegistrationConnector]
  private val registrationUrl               = "/register-country-by-country-reporting/registration"
  private val errorCodes: Gen[Int]          = Gen.oneOf(Seq(400, 403, 500, 501, 502, 503, 504))

  private val requestCommon: RequestCommon =
    RequestCommon("2016-08-16T15:55:30Z", "CBC", "ec031b045855445e96f98a569ds56cd2", Some(Seq(RequestParameter("REGIME", "CBC"))))

  private val registrationWithOrganisationIDPayload: RegisterWithID = RegisterWithID(
    RegisterWithIDRequest(
      requestCommon,
      RequestWithIDDetails("UTR", "utr", requiresNameMatch = true, isAnAgent = false, Some(WithIDOrganisation("name", "0001")))
    )
  )

  private val addressRequest: Address        = Address("100 Parliament Street", None, "", Some("London"), Some("SW1A 2BQ"), "GB")
  private val contactDetails: ContactDetails = ContactDetails(Some("1111111"), Some("2222222"), Some("1111111"), Some("test@test.org"))

  private val registrationWithoutIDPayload: RegisterWithoutId = RegisterWithoutId(
    RegisterWithoutIDRequest(
      requestCommon,
      RequestDetails(NoIdOrganisation("name"), addressRequest, contactDetails, None)
    )
  )

  "RegistrationConnector" - {
    "registerWithId" - {
      "must return 'RegisterWithIDResponse' for the valid input request" in {

        val expectedResponse = RegisterWithIDResponse(
          SafeId("XE0000123456789"),
          OrganisationResponse("Org Name", true, Some("LLP"), Some("0002")),
          AddressResponse("addressLine1", Some("addressLine2"), Some("addressLine3"), Some("addressLine4"), Some("AA1 1AA"), "GB")
        )

        stubResponse(s"$registrationUrl/utr", OK, businessWithIdJsonResponse)

        val result = connector.registerWithID(registrationWithOrganisationIDPayload)
        result.futureValue mustBe Right(expectedResponse)
      }

      "must return 'InternalServerError' when safeId is missing in the businessWithIdResponse" in {
        stubResponse(s"$registrationUrl/utr", OK, businessWithIdMissingSafeIdJson)

        val result = connector.registerWithID(registrationWithOrganisationIDPayload)
        result.futureValue mustBe Left(InternalServerError)
      }

      "must return 'InternalServerError' when EIS returns Http status other than NOT_FOUND and OK status" in {
        val errorStatus: Int = errorCodes.sample.value
        stubResponse(s"$registrationUrl/utr", errorStatus, businessWithIdMissingSafeIdJson)

        val result = connector.registerWithID(registrationWithOrganisationIDPayload)
        result.futureValue mustBe Left(InternalServerError)
      }

      "must return 'NotFoundError' when EIS returns NOT_FOUND status" in {
        stubResponse(s"$registrationUrl/utr", NOT_FOUND, businessWithIdMissingSafeIdJson)

        val result = connector.registerWithID(registrationWithOrganisationIDPayload)
        result.futureValue mustBe Left(NotFoundError)
      }
    }

    "registerWithoutId" - {

      "must return 'SafeId' for the valid input request" in {

        stubResponse(s"$registrationUrl/noId", OK, businessWithoutIdJsonResponse)

        val result = connector.registerWithoutID(registrationWithoutIDPayload)
        result.futureValue mustBe Right(Some(SafeId("XE0000123456789")))
      }

      "must return 'None' when safeId is missing in the businessWithIdResponse" in {
        stubResponse(s"$registrationUrl/noId", OK, businessWithoutIdMissingSafeIdJson)

        val result = connector.registerWithoutID(registrationWithoutIDPayload)
        result.futureValue mustBe Right(None)
      }

      "must return 'None' when EIS returns Error status" in {
        val errorStatus: Int = errorCodes.sample.value
        stubResponse(s"$registrationUrl/noId", errorStatus, businessWithoutIdJsonResponse)

        val result = connector.registerWithoutID(registrationWithoutIDPayload)
        result.futureValue mustBe Left(InternalServerError)
      }

    }
  }

  private def stubResponse(expectedEndpoint: String, expectedStatus: Int, expectedBody: String): StubMapping =
    server.stubFor(
      post(urlEqualTo(s"$expectedEndpoint"))
        .willReturn(
          aResponse()
            .withStatus(expectedStatus)
            .withBody(expectedBody)
        )
    )

}

trait JsonFixture {

  val businessWithIdJsonResponse: String =
    """
      |{
      | "registerWithIDResponse": {
      |  "responseDetail": {
      |   "SAFEID": "XE0000123456789",
      |   "ARN": "QARN6587851",
      |   "isEditable": true,
      |   "isAnAgent": false,
      |   "isAnIndividual": false,
      |   "organisation": {
      |    "organisationName": "Org Name",
      |    "isAGroup": true,
      |    "organisationType": "LLP",
      |    "code": "0002"
      |   },
      |   "address": {
      |    "addressLine1": "addressLine1",
      |    "addressLine2": "addressLine2",
      |    "addressLine3": "addressLine3",
      |    "addressLine4": "addressLine4",
      |    "postalCode": "AA1 1AA",
      |    "countryCode": "GB"
      |   },
      |   "contactDetails": {
      |"phoneNumber": "020947376", "mobileNumber": "07634527721", "faxNumber": "02073648933", "emailAddress": "test@email.com"
      |} }
      |} }
      |""".stripMargin

  val businessWithIdMissingSafeIdJson: String =
    """
      |{
      | "registerWithIDResponse": {
      |  "responseDetail": {
      |   "ARN": "QARN6587851",
      |   "isEditable": true,
      |   "isAnAgent": false,
      |   "isAnIndividual": false,
      |   "organisation": {
      |    "organisationName": "Org Name",
      |    "isAGroup": true,
      |    "organisationType": "LLP",
      |    "code": "0002"
      |   },
      |   "address": {
      |    "addressLine1": "addressLine1",
      |    "addressLine2": "addressLine2",
      |    "addressLine3": "addressLine3",
      |    "addressLine4": "addressLine4",
      |    "postalCode": "AA1 1AA",
      |    "countryCode": "GB"
      |   },
      |   "contactDetails": {
      |"phoneNumber": "020947376", "mobileNumber": "07634527721", "faxNumber": "02073648933", "emailAddress": "test@email.com"
      |} }
      |} }
      |""".stripMargin

  val businessWithoutIdJsonResponse: String =
    """
      |{
      |"registerWithoutIDResponse": {
      |    "responseCommon": {
      |"status": "OK",
      |"processingDate": "2001-12-17T09:30:47Z",
      |"returnParameters": [
      |{
      | "paramName": "SAP_NUMBER", "paramValue": "0123456789"
      |} ]
      |},
      |"responseDetail": {
      |"SAFEID": "XE0000123456789",
      |"ARN": "ZARN1234567"
      |}}}""".stripMargin

  val businessWithoutIdMissingSafeIdJson: String =
    """
      |{
      |"registerWithoutIDResponse": {
      |    "responseCommon": {
      |"status": "OK",
      |"processingDate": "2001-12-17T09:30:47Z",
      |"returnParameters": [
      |{
      | "paramName": "SAP_NUMBER", "paramValue": "0123456789"
      |} ]
      |},
      |"responseDetail": {
      |"ARN": "ZARN1234567"
      |}}}""".stripMargin

}
