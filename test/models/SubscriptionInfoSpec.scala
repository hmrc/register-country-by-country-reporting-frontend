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

package models

import base.SpecBase
import play.api.libs.json.Json

class SubscriptionInfoSpec extends SpecBase {

  "SubscriptionInfo" - {
    "must create correct EnrolmentRequest for UK users" in {
      val json = Json.parse("""{"identifiers":[{"key":"cbcId","value":"222222"},{"key":"UTR","value":"11111"}],"verifiers":[]}""".stripMargin)

      val enrolmentInfo = SubscriptionInfo(safeID = "safeId", utr = Some("11111"), None, cbcId = "222222")

      val enrolmentRequest = enrolmentInfo.convertToEnrolmentRequest
      Json.toJson(enrolmentRequest) mustBe json
    }
    "must create correct EnrolmentRequest for NON-UK users" in {
      val json = Json.parse("""{"identifiers":[{"key":"cbcId","value":"222222"}],"verifiers":[{"key":"NonUKPostalCode","value":"123 222"}]}""".stripMargin)

      val enrolmentInfo = SubscriptionInfo(safeID = "safeId", None, nonUkPostcode = Some("123 222"), cbcId = "222222")

      val enrolmentRequest = enrolmentInfo.convertToEnrolmentRequest
      Json.toJson(enrolmentRequest) mustBe json
    }
  }
}
