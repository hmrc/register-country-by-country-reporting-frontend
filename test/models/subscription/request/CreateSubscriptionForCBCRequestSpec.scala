/*
 * Copyright 2023 HM Revenue & Customs
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

package models.subscription.request

import base.SpecBase
import generators.Generators
import org.scalacheck.Arbitrary
import play.api.libs.json.Json

class CreateSubscriptionForCBCRequestSpec extends SpecBase with Generators {

  "CreateSubscriptionForCBCRequest" - {
    "serialise and de-serialise to and from json" in {

      val cbcRequest =  Arbitrary.arbitrary[CreateSubscriptionForCBCRequest].sample.value
      Json.toJson(cbcRequest).as[CreateSubscriptionForCBCRequest] mustBe cbcRequest

    }

    "serialise to json" in {
      val contactInformation =  ContactInformation(OrganisationDetails("orgName"), "test@email.com", None, None)
      val requestDetail = RequestDetail("SAFE", "number", Some("tradingName"), true, contactInformation, None)
      val requestCommon = RequestCommonForSubscription("CBC", None, "date", "ref", "MDTP", None)
      val subscriptionRequest = SubscriptionRequest(requestCommon, requestDetail)
      val cbcRequest = CreateSubscriptionForCBCRequest(subscriptionRequest)

      val expectedJson = Json.parse("""
          |{
          |  "createSubscriptionForCBCRequest": {
          |    "requestCommon": {
          |      "regime": "CBC",
          |      "receiptDate": "date",
          |      "acknowledgementReference": "ref",
          |      "originatingSystem": "MDTP"
          |    },
          |    "requestDetail": {
          |      "IDType": "SAFE",
          |      "IDNumber": "number",
          |      "tradingName": "tradingName",
          |      "isGBUser": true,
          |      "primaryContact": {
          |        "organisation": {
          |          "organisationName": "orgName"
          |        },
          |        "email": "test@email.com"
          |      }
          |    }
          |  }
          |}
         """.stripMargin)

      Json.toJson(cbcRequest) mustBe expectedJson

    }
  }

}
