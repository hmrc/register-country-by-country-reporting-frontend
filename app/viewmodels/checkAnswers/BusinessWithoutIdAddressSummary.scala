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

object BusinessWithoutIdAddressSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(BusinessWithoutIdAddressPage).map {
      answer =>

      val value = HtmlFormat.escape(s"""
        ${answer.addressLine1}<br>
        ${answer.addressLine2.fold("")(
        address => s"$address<br>"
      )}
        ${answer.addressLine3}<br>
        ${answer.addressLine4.fold("")(
        address => s"$address<br>"
      )}
        ${answer.postCode.fold("")(
        postcode => s"$postcode<br>"
      )}
        ${answer.country.description}
     """)

        SummaryListRowViewModel(
          key     = "businessWithoutIdAddress.checkYourAnswersLabel",
          value   = ValueViewModel(HtmlContent(value)),
          actions = Seq(
            ActionItemViewModel("site.change", routes.BusinessWithoutIdAddressController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("businessWithoutIdAddress.change.hidden"))
          )
        )
    }
}
