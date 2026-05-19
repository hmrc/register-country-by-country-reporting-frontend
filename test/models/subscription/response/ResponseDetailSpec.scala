/*
 * Copyright 2026 HM Revenue & Customs
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

package models.subscription.response

import base.SpecBase
import play.api.libs.json.Json

class ResponseDetailSpec extends SpecBase {

  "ResponseDetail" - {

    "must serialise and deserialise with a primary contact only" in {
      val responseDetail = ResponseDetail(
        subscriptionID = "subscription-id",
        tradingName = Some("Trading Name"),
        isGBUser = true,
        primaryContact = Seq(
          ContactInformation(
            organisationDetails = OrganisationDetails("Primary Organisation"),
            email = "primary@test.com",
            phone = Some("01234567890"),
            mobile = Some("07123456789")
          )
        ),
        secondaryContact = None
      )

      val jsonForWriting = Json.obj(
        "subscriptionID" -> "subscription-id",
        "tradingName"    -> "Trading Name",
        "isGBUser"       -> true,
        "primaryContact" -> Json.arr(
          Json.obj(
            "organisationDetails" -> Json.obj(
              "organisationName" -> "Primary Organisation"
            ),
            "email"  -> "primary@test.com",
            "phone"  -> "01234567890",
            "mobile" -> "07123456789"
          )
        )
      )

      val jsonForReading = Json.obj(
        "subscriptionID" -> "subscription-id",
        "tradingName"    -> "Trading Name",
        "isGBUser"       -> true,
        "primaryContact" -> Json.arr(
          Json.obj(
            "organisation" -> Json.obj(
              "organisationName" -> "Primary Organisation"
            ),
            "email"  -> "primary@test.com",
            "phone"  -> "01234567890",
            "mobile" -> "07123456789"
          )
        )
      )

      Json.toJson(responseDetail) mustBe jsonForWriting
      jsonForReading.as[ResponseDetail] mustBe responseDetail
    }

    "must serialise and deserialise with a secondary contact" in {
      val responseDetail = ResponseDetail(
        subscriptionID = "subscription-id",
        tradingName = None,
        isGBUser = false,
        primaryContact = Seq(
          ContactInformation(
            organisationDetails = OrganisationDetails("Primary Organisation"),
            email = "primary@test.com",
            phone = None,
            mobile = None
          )
        ),
        secondaryContact = Some(
          Seq(
            ContactInformation(
              organisationDetails = OrganisationDetails("Secondary Organisation"),
              email = "secondary@test.com",
              phone = Some("09876543210"),
              mobile = None
            )
          )
        )
      )

      val jsonForWriting = Json.obj(
        "subscriptionID" -> "subscription-id",
        "isGBUser"       -> false,
        "primaryContact" -> Json.arr(
          Json.obj(
            "organisationDetails" -> Json.obj(
              "organisationName" -> "Primary Organisation"
            ),
            "email" -> "primary@test.com"
          )
        ),
        "secondaryContact" -> Json.arr(
          Json.obj(
            "organisationDetails" -> Json.obj(
              "organisationName" -> "Secondary Organisation"
            ),
            "email" -> "secondary@test.com",
            "phone" -> "09876543210"
          )
        )
      )

      val jsonForReading = Json.obj(
        "subscriptionID" -> "subscription-id",
        "isGBUser"       -> false,
        "primaryContact" -> Json.arr(
          Json.obj(
            "organisation" -> Json.obj(
              "organisationName" -> "Primary Organisation"
            ),
            "email" -> "primary@test.com"
          )
        ),
        "secondaryContact" -> Json.arr(
          Json.obj(
            "organisation" -> Json.obj(
              "organisationName" -> "Secondary Organisation"
            ),
            "email" -> "secondary@test.com",
            "phone" -> "09876543210"
          )
        )
      )

      Json.toJson(responseDetail) mustBe jsonForWriting
      jsonForReading.as[ResponseDetail] mustBe responseDetail
    }
  }
}
