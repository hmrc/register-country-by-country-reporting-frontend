/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.libs.json.{Reads, __}

case class RegisterWithIDResponse(
                                   safeId: SafeId,
                                   organisation: OrganisationResponse,
                                   address: AddressResponse
                                 )

object RegisterWithIDResponse {

  import play.api.libs.functional.syntax._
  implicit val reads: Reads[RegisterWithIDResponse] = (
    (__ \ "registerWithIDResponse" \ "responseDetail" \ "SAFEID").read[SafeId] and
      (__ \ "registerWithIDResponse"\ "responseDetail" \ "organisation").read[OrganisationResponse] and
      (__ \ "registerWithIDResponse" \ "responseDetail" \ "address").read[AddressResponse]
    )(RegisterWithIDResponse.apply _)

}
