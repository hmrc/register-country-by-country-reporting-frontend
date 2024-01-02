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

package viewmodels.checkAnswers

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.SecondContactEmailPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import pages.AutoMatchedUTRPage

object SecondContactEmailSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(SecondContactEmailPage).map {
      answer =>

        SummaryListRowViewModel(
          key     = "secondContactEmail.checkYourAnswersLabel",
          value   = ValueViewModel(HtmlFormat.escape(answer).toString),
          actions = Seq(
            ActionItemViewModel(
              content = HtmlContent(
                s"""
                   |<span aria-hidden="true">${messages("site.change")}</span>
                   |<span class="govuk-visually-hidden">${messages("secondContactEmail.change.hidden")}</span>
                   |""".stripMargin
              ),
              href = if (answers.get(AutoMatchedUTRPage).isEmpty) {
                routes.SecondContactEmailController.onPageLoad(CheckMode).url
              } else {
                routes.UnableToChangeBusinessController.onPageLoad().url
              }
            ).withAttribute(("id","second-contact-email"))
          )
        )
    }
}
