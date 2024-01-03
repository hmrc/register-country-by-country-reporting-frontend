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

package utils

import models.UserAnswers
import pages.{BusinessHaveDifferentNamePage, DoYouHaveSecondContactPage, DoYouHaveUTRPage, HaveTelephonePage, IsRegisteredAddressInUkPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers._
import pages.AutoMatchedUTRPage

class CheckYourAnswersHelper(userAnswers: UserAnswers,
                             countryListFactory: CountryListFactory)(implicit val messages: Messages) {

  def businessSection: Seq[SummaryListRow] = {
    Console.println("Address :: 86768 :: "+ userAnswers)
    (userAnswers.get(IsRegisteredAddressInUkPage), userAnswers.get(DoYouHaveUTRPage) , userAnswers.get(AutoMatchedUTRPage).isEmpty) match {
      case (_, _ , false) => businessWithIDSection
      case (Some(true), _ , _) => businessWithIDSection
      case (Some(false), Some(true) ,_) => businessWithIDSection
      case (Some(false), _ , _) => businessWithoutIDSection
      case _ => {
      Console.println("Address :: 576586 :: "+ userAnswers.get(IsRegisteredAddressInUkPage)+ userAnswers.get(DoYouHaveUTRPage) + userAnswers.get(AutoMatchedUTRPage).isEmpty)
      Seq.empty[SummaryListRow]}
  }
}

  def tradingNameSummary: Option[SummaryListRow] = userAnswers.get(BusinessHaveDifferentNamePage) flatMap (_ => WhatIsTradingNameSummary.row(userAnswers))

  def businessWithoutIDSection: Seq[SummaryListRow] = Seq(
    IsRegisteredAddressInUkSummary.row(userAnswers),
    DoYouHaveUTRSummary.row(userAnswers),
    BusinessWithoutIDNameSummary.row(userAnswers),
    tradingNameSummary,
    BusinessWithoutIdAddressSummary.row(userAnswers)
  ).flatten

  def businessWithIDSection: Seq[SummaryListRow] =
    Seq(YourBusinessSummary.row(userAnswers, countryListFactory)).flatten

  def contactPhoneSummary: Option[SummaryListRow] = userAnswers.get(HaveTelephonePage) flatMap (_ => ContactPhoneSummary.row(userAnswers))

  def firstContactSection: Seq[SummaryListRow] = Seq(ContactNameSummary.row(userAnswers),
    ContactEmailSummary.row(userAnswers), contactPhoneSummary).flatten

  def secondContactPhoneSummary: Option[SummaryListRow] = userAnswers.get(HaveTelephonePage) flatMap (_ => SecondContactPhoneSummary.row(userAnswers))

  def secondContactSection: Seq[SummaryListRow] = userAnswers.get(DoYouHaveSecondContactPage) match {
    case Some(true) => Seq(DoYouHaveSecondContactSummary.row(userAnswers),
      SecondContactNameSummary.row(userAnswers), SecondContactEmailSummary.row(userAnswers), secondContactPhoneSummary).flatten
    case _ => Seq(DoYouHaveSecondContactSummary.row(userAnswers)).flatten
  }
}
