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

package models.matching

import models.SafeId
import models.register.response.RegisterWithIDResponse
import models.register.response.details.AddressResponse
import play.api.libs.json._

case class RegistrationInfo(safeId: SafeId, name: String, address: AddressResponse)

object RegistrationInfo {

  implicit val format: OFormat[RegistrationInfo] = Json.format[RegistrationInfo]

  def apply(response: RegisterWithIDResponse): RegistrationInfo = {
    RegistrationInfo(response.safeId ,response.organisation.organisationName, response.address)
  }
}

