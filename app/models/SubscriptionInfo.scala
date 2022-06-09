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

import play.api.libs.json.{Json, OFormat}

case class SubscriptionInfo(safeID: String,
                            saUtr: Option[String] = None,
                            ctUtr: Option[String] = None,
                            nonUkPostcode: Option[String] = None,
                            mdrId: String
                           ){
  def convertToEnrolmentRequest: EnrolmentRequest =
    EnrolmentRequest(identifiers = Seq(Identifier("MDRID", mdrId)), verifiers = buildVerifiers)

  def buildVerifiers: Seq[Verifier] = {

    val mandatoryVerifiers = Seq(Verifier("SAFEID", safeID))

    mandatoryVerifiers ++
      buildOptionalVerifier(saUtr, "SAUTR") ++
      buildOptionalVerifier(ctUtr, "CTUTR") ++
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
}


