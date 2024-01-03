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

package models.register.response

import models.SafeId
import models.register.response.details.{AddressResponse, OrganisationResponse}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsValue, Json}

class RegisterWithIDResponseSpec extends AnyFreeSpec with Matchers {

  "RegisterWithIDResponse" - {

    "must read json as RegisterWithIDResponse" in {
      val json: JsValue = Json.parse(
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
          |""".stripMargin)

      json.as[RegisterWithIDResponse] mustBe RegisterWithIDResponse(SafeId("XE0000123456789"),
          OrganisationResponse("Org Name",true,Some("LLP"),Some("0002")),
          AddressResponse("addressLine1",Some("addressLine2"),Some("addressLine3"),Some("addressLine4"),Some("AA1 1AA"),"GB"))

    }

  }
}