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

package viewmodels.checkAnswers

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.BusinessWithoutIdAddressPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object BusinessWithoutIdAddressSummary {


  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(BusinessWithoutIdAddressPage).map {
      answer =>

        def formatLine(line: String) = s"""<div class="govuk-margin-bottom-0">${HtmlFormat.escape(line)}</div>"""

        val value =
          s"""<div class=govuk-margin-bottom-0>${HtmlFormat.escape(answer.addressLine1)}</div>""" concat {
              answer.addressLine2.fold("")(formatLine)
          } concat {
            formatLine(answer.addressLine3)
          } concat {
            answer.addressLine4.fold("")(formatLine)
          } concat {
            answer.postCode.fold("")(formatLine)
          } concat {
            s"""<div class=govuk-margin-bottom-0>${HtmlFormat.escape(answer.country.description)}</div>"""
            }


        SummaryListRowViewModel(
          key = "businessWithoutIdAddress.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent(value)),
          actions = Seq(
            ActionItemViewModel("site.change", routes.BusinessWithoutIdAddressController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("businessWithoutIdAddress.change.hidden"))
          )
        )
    }
}
