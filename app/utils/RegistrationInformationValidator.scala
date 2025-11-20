/*
 * Copyright 2025 HM Revenue & Customs
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

package utils

import models.UserAnswers
import pages._
import play.api.libs.json.Reads

class RegistrationInformationValidator(userAnswers: UserAnswers) {

  private def checkPage[A](page: QuestionPage[A])(implicit rds: Reads[A]): Option[Page] =
    userAnswers.get(page) match {
      case None => Some(page)
      case _    => None
    }

  private def checkBusinessWithoutIDName: Seq[Page] = Seq(checkPage(BusinessWithoutIDNamePage)).flatten

  private def checkBusinessWithoutIdAddress: Seq[Page] = Seq(checkPage(BusinessWithoutIdAddressPage)).flatten

  private def checkPrimaryContactDetails: Seq[Page] = Seq(
    checkPage(ContactNamePage),
    checkPage(ContactEmailPage)
  ).flatten ++ checkPrimaryContactNumber

  private def checkPrimaryContactNumber: Seq[Page] = (userAnswers.get(HaveTelephonePage) match {
    case Some(true)  => checkPage(ContactPhonePage)
    case Some(false) => None
    case _           => Some(HaveTelephonePage)
  }).toSeq

  private def checkSecondaryContactPhone: Seq[Page] = (userAnswers.get(SecondContactHavePhonePage) match {
    case Some(true)  => checkPage(SecondContactPhonePage)
    case Some(false) => None
    case _           => Some(SecondContactHavePhonePage)
  }).toSeq

  private def checkSecondaryContactDetails: Seq[Page] =
    userAnswers.get(DoYouHaveSecondContactPage) match {
      case Some(true) =>
        Seq(
          checkPage(SecondContactNamePage),
          checkPage(SecondContactEmailPage)
        ).flatten ++ checkSecondaryContactPhone
      case Some(false) => Seq.empty
      case _           => Seq(DoYouHaveSecondContactPage)
    }

  private def checkHaveDifferentName: Seq[Page] =
    userAnswers.get(BusinessHaveDifferentNamePage) match {
      case Some(true) =>
        Seq(checkPage(WhatIsTradingNamePage)).flatten
      case Some(false) => Seq.empty
      case _           => Seq(BusinessHaveDifferentNamePage)
    }

  private def checkIsThisYourBusiness: Seq[Page] =
    userAnswers.get(IsThisYourBusinessPage) match {
      case Some(true) => Seq.empty
      case _          => Seq(IsThisYourBusinessPage)
    }

  private def checkRegistrationInfo: Seq[Page] =
    userAnswers.get(RegistrationInfoPage) match {
      case Some(_) => Seq.empty
      case _       => Seq(RegistrationInfoPage)
    }

  private def checkBusinessType: Seq[Page] =
    userAnswers.get(BusinessTypePage) match {
      case Some(_) => Seq.empty
      case _       => Seq(BusinessTypePage)
    }
  private def checkUTR: Seq[Page] =
    userAnswers.get(UTRPage) match {
      case Some(_) => Seq.empty
      case _       => Seq(UTRPage)
    }

  private def checkBusinessName: Seq[Page] =
    userAnswers.get(BusinessNamePage) match {
      case Some(_) => Seq.empty
      case _       => Seq(BusinessNamePage)
    }

  private def checkRegistrationInformationForWithoutIDFlow: Seq[Page] =
    checkBusinessWithoutIDName ++ checkBusinessWithoutIdAddress ++
      checkHaveDifferentName ++ checkContactDetails

  private def checkRegistrationInformationForWithIDFlow: Seq[Page] =
    checkIsThisYourBusiness ++ checkRegistrationInfo ++ checkContactDetails

  private def checkContactDetails: Seq[Page] = checkPrimaryContactDetails ++
    checkSecondaryContactDetails

  private def checkPreRequiredDataRegisterWithID = checkBusinessType ++ checkUTR ++ checkBusinessName

  def isInformationMissing: Seq[Page] =
    userAnswers.get(AutoMatchedUTRPage) match {
      case Some(_) => checkRegistrationInformationForWithIDFlow
      case None    => checkIsRegisteredAddressInUkFlow
    }

  private def checkIsRegisteredAddressInUkFlow =
    userAnswers.get(IsRegisteredAddressInUkPage) match {
      case Some(true)  => checkPreRequiredDataRegisterWithID ++ checkRegistrationInformationForWithIDFlow
      case Some(false) => checkHaveUTRFlow
      case None        => Seq(IsRegisteredAddressInUkPage)
    }

  private def checkHaveUTRFlow: Seq[Page] =
    userAnswers.get(DoYouHaveUTRPage) match {
      case Some(true)  => checkPreRequiredDataRegisterWithID ++ checkRegistrationInformationForWithIDFlow
      case Some(false) => checkRegistrationInformationForWithoutIDFlow
      case _           => Seq(DoYouHaveUTRPage)
    }
}

object RegistrationInformationValidator {

  def apply(userAnswers: UserAnswers) =
    new RegistrationInformationValidator(userAnswers)
}
