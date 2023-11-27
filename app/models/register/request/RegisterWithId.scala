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

package models.register.request

import models.Regime.CBC
import models.{Name, UUIDGen}
import models.matching.AutoMatchedRegistrationRequest
import play.api.libs.json._

import java.time.{Clock, LocalDate}

case class RegisterWithID(registerWithIDRequest: RegisterWithIDRequest)

object RegisterWithID {
  implicit val format: Format[RegisterWithID] = Json.format[RegisterWithID]

  def apply(name: Name, dob: Option[LocalDate], identifierName: String, identifierValue: String)(implicit
                                                                                                 uuidGenerator: UUIDGen,
                                                                                                 clock: Clock
  ): RegisterWithID =
    RegisterWithID(
      RegisterWithIDRequest(
        RequestCommon(CBC.toString),
        RequestWithIDDetails(name, dob, identifierName, identifierValue)
      )
    )

  def apply(registrationRequest: AutoMatchedRegistrationRequest)(implicit uuidGenerator: UUIDGen, clock: Clock): RegisterWithID =
    RegisterWithID(
      RegisterWithIDRequest(
        RequestCommon(CBC.toString),
        RequestWithIDDetails(registrationRequest)
      )
    )
}

case class RegisterWithIDRequest(
    requestCommon: RequestCommon,
    requestDetail: RequestWithIDDetails
)

object RegisterWithIDRequest {
  implicit val format: Format[RegisterWithIDRequest] =
    Json.format[RegisterWithIDRequest]
}

case class WithIDOrganisation(
    organisationName: String,
    organisationType: String
)

object WithIDOrganisation {
  implicit val format = Json.format[WithIDOrganisation]
}

case class RequestWithIDDetails(
    IDType: String,
    IDNumber: String,
    requiresNameMatch: Boolean,
    isAnAgent: Boolean,
    partnerDetails: Option[WithIDOrganisation] = None
)

object RequestWithIDDetails {

  implicit lazy val requestWithIDDetailsReads: Reads[RequestWithIDDetails] = {
    import play.api.libs.functional.syntax._
    (
      (__ \ "IDType").read[String] and
        (__ \ "IDNumber").read[String] and
        (__ \ "requiresNameMatch").read[Boolean] and
        (__ \ "isAnAgent").read[Boolean] and
        (__ \ "organisation").read[WithIDOrganisation]
    )((idType, idNumber, requiresNameMatch, isAnAgent, organisation) =>
      RequestWithIDDetails(
        idType,
        idNumber,
        requiresNameMatch,
        isAnAgent,
        Some(organisation)
      )
    )
  }

  implicit lazy val requestWithIDDetailsWrites: OWrites[RequestWithIDDetails] =
    OWrites[RequestWithIDDetails] { idDetails =>
      Json.obj(
        "IDType" -> idDetails.IDType,
        "IDNumber" -> idDetails.IDNumber,
        "requiresNameMatch" -> idDetails.requiresNameMatch,
        "isAnAgent" -> idDetails.isAnAgent,
        "organisation" -> idDetails.partnerDetails
      )
    }
}
