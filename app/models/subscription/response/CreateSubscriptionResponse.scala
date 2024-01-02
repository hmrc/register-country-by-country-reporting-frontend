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

package models.subscription.response

import models.SubscriptionID
import play.api.libs.json.{Reads, __}

case class CreateSubscriptionResponse(subscriptionID: SubscriptionID)

object CreateSubscriptionResponse {

  implicit val reads: Reads[CreateSubscriptionResponse] = {
    import play.api.libs.functional.syntax._
    (__ \ "createSubscriptionForCBCResponse" \ "responseDetail" \ "subscriptionID").read[SubscriptionID] fmap (id => CreateSubscriptionResponse(id))
  }
}


