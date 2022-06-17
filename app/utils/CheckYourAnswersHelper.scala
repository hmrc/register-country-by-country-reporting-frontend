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

package utils

import models.UserAnswers
import pages.{DoYouHaveSecondContactPage, DoYouHaveUTRPage, HaveTelephonePage, SecondContactHavePhonePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers._

class CheckYourAnswersHelper(userAnswers: UserAnswers,
                             maxVisibleChars: Int = DisplayConstants.maxVisibleChars,
                             countryListFactory: CountryListFactory)(implicit val messages: Messages) {

  def businessSection: Seq[SummaryListRow] = userAnswers.get(DoYouHaveUTRPage) match {
    case Some(true) => businessWithIDSection
    case Some(false) => businessWithoutIDSection
    case _ => Seq.empty[SummaryListRow]
  }

  def businessWithoutIDSection: Seq[SummaryListRow] = Seq(
    DoYouHaveUTRSummary.row(userAnswers),
    BusinessWithoutIDNameSummary.row(userAnswers),
    WhatIsTradingNameSummary.row(userAnswers),
    BusinessWithoutIdAddressSummary.row(userAnswers)
  ).flatten

  def businessWithIDSection: Seq[SummaryListRow] =
    Seq(YourBusinessSummary.row(userAnswers, countryListFactory)).flatten

  def contactPhoneSummary: Option[SummaryListRow] = userAnswers.get(HaveTelephonePage) match {
    case Some(true) => ContactPhoneSummary.row(userAnswers)
    case _ => None
  }

  def firstContactSection: Seq[SummaryListRow] = Seq(ContactNameSummary.row(userAnswers),
    ContactEmailSummary.row(userAnswers), contactPhoneSummary).flatten

  def secondContactPhoneSummary: Option[SummaryListRow] = userAnswers.get(SecondContactHavePhonePage) match {
    case Some(true) => SecondContactPhoneSummary.row(userAnswers)
    case _ => None
  }

  def secondContactSection: Seq[SummaryListRow] = userAnswers.get(DoYouHaveSecondContactPage) match {
    case Some(true) => Seq(DoYouHaveSecondContactSummary.row(userAnswers),
      SecondContactNameSummary.row(userAnswers), SecondContactEmailSummary.row(userAnswers), secondContactPhoneSummary).flatten
    case _ => Seq(DoYouHaveSecondContactSummary.row(userAnswers)).flatten
  }
}
