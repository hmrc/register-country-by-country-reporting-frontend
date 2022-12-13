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

package models.subscription.request

import models.subscription.request.RequestCommonForSubscription.createRequestCommonForSubscription
import models.{ApiError, SafeId, SubscriptionCreateInformationMissingError, UserAnswers}
import pages._
import play.api.libs.json.{Json, OFormat}

case class SubscriptionRequest(
  requestCommon: RequestCommonForSubscription,
  requestDetail: RequestDetail
)

object SubscriptionRequest {
  private val idType: String                        = "SAFE"
  implicit val format: OFormat[SubscriptionRequest] = Json.format[SubscriptionRequest]

  def createSubscriptionRequest(safeId: SafeId, userAnswers: UserAnswers): Either[ApiError, SubscriptionRequest] =
    getPrimaryContactInformation(userAnswers) match {
      case Some(pc) =>
        getSecondaryContactInformation(userAnswers) match {
          case Right(sc) =>
            Right(
              SubscriptionRequest(
                createRequestCommonForSubscription(),
                RequestDetail(
                  IDType = idType,
                  IDNumber = safeId.value,
                  tradingName = userAnswers.get(WhatIsTradingNamePage) orElse userAnswers.get(BusinessWithoutIDNamePage) orElse userAnswers.get(RegistrationInfoPage).map(_.name),
                  isGBUser = isGBUser(userAnswers: UserAnswers),
                  primaryContact = pc,
                  secondaryContact = sc
                )
              )
            )
          case Left(error) => Left(error)
        }
      case _ => Left(SubscriptionCreateInformationMissingError("Primary ContactInformation"))
    }

  def getPrimaryContactInformation(userAnswers: UserAnswers): Option[ContactInformation] =
    for {
      businessEmail       <- userAnswers.get(ContactEmailPage)
      businessContactInfo <- userAnswers.get(ContactNamePage).map(OrganisationDetails(_))
    } yield ContactInformation(organisation = businessContactInfo, email = businessEmail, phone = userAnswers.get(ContactPhonePage), mobile = None)

  def getSecondaryContactInformation(userAnswers: UserAnswers): Either[ApiError, Option[ContactInformation]] = {
    val doYouHaveSecondContact      = userAnswers.get(DoYouHaveSecondContactPage)
    val doYouHaveSecondContactPhone = userAnswers.get(SecondContactHavePhonePage)
    (doYouHaveSecondContact, doYouHaveSecondContactPhone) match {
      case (Some(false), _) => Right(None)
      case (Some(true), Some(_)) =>
        Right(
          for {
            businessSecondaryContactInfo <- userAnswers.get(SecondContactNamePage).map(OrganisationDetails(_))
            businessSecondaryEmail       <- userAnswers.get(SecondContactEmailPage)
          } yield ContactInformation(organisation = businessSecondaryContactInfo,
                                     email = businessSecondaryEmail,
                                     phone = userAnswers.get(SecondContactPhonePage),
                                     mobile = None
          )
        )
      case (Some(true), None) => Left(SubscriptionCreateInformationMissingError("DoYouHaveSecondContactPhone Page not answered"))
      case (None, _)          => Left(SubscriptionCreateInformationMissingError("DoYouHaveSecondContact Page not answered"))
    }

  }

  def isGBUser(userAnswers: UserAnswers): Boolean = !userAnswers.get(BusinessWithoutIdAddressPage).exists(_.isOtherCountry)

}
