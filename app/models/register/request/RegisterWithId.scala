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

package models.register.request

import models.Regime.CBC
import models.UUIDGen
import models.matching.{AutoMatchedRegistrationRequest, RegistrationRequest}
import play.api.libs.json._

import java.time.Clock

case class RegisterWithID(registerWithIDRequest: RegisterWithIDRequest)

object RegisterWithID {
  implicit val format: Format[RegisterWithID] = Json.format[RegisterWithID]

  def apply(registrationRequest: RegistrationRequest)(implicit uuidGenerator: UUIDGen, clock: Clock): RegisterWithID =
    RegisterWithID(
      RegisterWithIDRequest(
        RequestCommon(CBC.toString),
        RequestWithIDDetails(registrationRequest)
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
  implicit val format: OFormat[WithIDOrganisation] = Json.format[WithIDOrganisation]
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
        (__ \ "organisation").readNullable[WithIDOrganisation]
    )((idType, idNumber, requiresNameMatch, isAnAgent, organisation) => RequestWithIDDetails(idType, idNumber, requiresNameMatch, isAnAgent, organisation))
  }

  implicit lazy val requestWithIDDetailsWrites: OWrites[RequestWithIDDetails] = {
    case RequestWithIDDetails(idType, idNumber, requiresNameMatch, isAnAgent, Some(organisation @ WithIDOrganisation(_, _))) =>
      Json.obj(
        "IDType"            -> idType,
        "IDNumber"          -> idNumber,
        "requiresNameMatch" -> requiresNameMatch,
        "isAnAgent"         -> isAnAgent,
        "organisation"      -> organisation
      )
    case RequestWithIDDetails(idType, idNumber, requiresNameMatch, isAnAgent, None) =>
      Json.obj(
        "IDType"            -> idType,
        "IDNumber"          -> idNumber,
        "requiresNameMatch" -> requiresNameMatch,
        "isAnAgent"         -> isAnAgent
      )
  }

  def apply(registrationRequest: RegistrationRequest): RequestWithIDDetails =
    RequestWithIDDetails(
      registrationRequest.identifierType,
      registrationRequest.identifier,
      requiresNameMatch = true,
      isAnAgent = false,
      Option(WithIDOrganisation(registrationRequest.name, registrationRequest.businessType.map(_.code).getOrElse("")))
    )

  def apply(registrationRequest: AutoMatchedRegistrationRequest): RequestWithIDDetails =
    RequestWithIDDetails(
      registrationRequest.identifierType,
      registrationRequest.identifier,
      requiresNameMatch = false,
      isAnAgent = false,
      partnerDetails = None
    )
}
