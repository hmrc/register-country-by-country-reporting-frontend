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

package utils

object RegisterationCBCStubs {

  val cbcReg           = "/register-country-by-country-reporting"
  val registerUrl      = "/registration"
  val subscriptionUrl  = "/subscription"
  val noID             = s"$cbcReg$registerUrl/noId"
  val registerWithUtr  = s"$cbcReg$registerUrl/utr"
  val readSubscription = s"$cbcReg$subscriptionUrl/read-subscription/XE0000123456789"

  val OK_NoID_Response = """
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

  val OK_withUtr_Response =
    """{
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
      |     "phoneNumber": "020947376",
      |     "mobileNumber": "07634527721",
      |     "faxNumber": "02073648933",
      |     "emailAddress": "test@email.com"
      |   }
      |  }
      | }
      |}""".stripMargin

  val OK_ReadSubscription_Response =
    """
      |{"displaySubscriptionForCBCResponse":
      |{"responseDetail":{"subscriptionID": "id"}}}""".stripMargin

}
