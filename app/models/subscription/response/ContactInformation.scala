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

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class ContactInformation(
  organisationDetails: OrganisationDetails,
  email: String,
  phone: Option[String],
  mobile: Option[String]
)

object ContactInformation {

  given Reads[ContactInformation] = (
    (__ \ "organisation").read[OrganisationDetails] and
      (__ \ "email").read[String] and
      (__ \ "phone").readNullable[String] and
      (__ \ "mobile").readNullable[String]
  )(ContactInformation.apply)

  given OWrites[ContactInformation] = Json.writes[ContactInformation]
}
