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

package models

import models.BusinessType.{LimitedCompany, LimitedPartnership, Partnership, UnincorporatedAssociation}
import pages.{BusinessTypePage, BusinessWithoutIdAddressPage, UTRPage}
import play.api.libs.json.{Json, OFormat}

case class SubscriptionInfo(safeID: String,
                            utr: Option[String] = None,
                            nonUkPostcode: Option[String] = None,
                            cbcId: String
                           ){
  def convertToEnrolmentRequest: EnrolmentRequest =
    EnrolmentRequest(identifiers = Seq(Identifier("cbcId", cbcId)), verifiers = buildVerifiers)

  def buildVerifiers: Seq[Verifier] = {

    val mandatoryVerifiers = Seq(Verifier("SAFEID", safeID))

    mandatoryVerifiers ++
      buildOptionalVerifier(utr, "UTR") ++
      buildOptionalVerifier(nonUkPostcode, "NonUKPostalCode")
  }

  def buildOptionalVerifier(optionalInfo: Option[String], key: String): Seq[Verifier] =
    optionalInfo
      .map(
        info => Verifier(key, info)
      )
      .toSeq

}

object SubscriptionInfo {
  implicit val format: OFormat[SubscriptionInfo] = Json.format[SubscriptionInfo]

  private def getNonUkPostCodeIfProvided(userAnswers: UserAnswers): Option[String] =
    userAnswers.get(BusinessWithoutIdAddressPage) match {
      case Some(address) => address.postCode
      case _             => None
    }

  def apply(userAnswers: UserAnswers,safeId: SafeId, subscriptionId: SubscriptionID): SubscriptionInfo =
    SubscriptionInfo(safeID = safeId.value,
      utr = userAnswers.get(UTRPage),
      nonUkPostcode = getNonUkPostCodeIfProvided(userAnswers),
      cbcId = subscriptionId.value)
}


