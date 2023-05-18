/*
 * Copyright 2023 HM Revenue & Customs
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
import models.matching.RegistrationInfo
import models.register.response.details.AddressResponse
import models.{CheckMode, UserAnswers}
import pages.{IsThisYourBusinessPage, RegistrationInfoPage}
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.CountryListFactory
import viewmodels.govuk.summarylist._
import viewmodels.implicits._


object YourBusinessSummary {

  def row(userAnswers: UserAnswers, countryListFactory: CountryListFactory)(implicit messages: Messages): Option[SummaryListRow] = {
    val paragraphClass = """govuk-!-margin-0"""
    (userAnswers.get(IsThisYourBusinessPage), userAnswers.get(RegistrationInfoPage)) match {
      case (Some(true), Some(registrationInfo: RegistrationInfo)) =>
        val businessName: String = registrationInfo.name
        val address: AddressResponse = registrationInfo.address

        countryListFactory.getDescriptionFromCode(address.countryCode) match {
          case Some(countryDescription) =>

            val value = Html(s"""
                  <p>$businessName</p>
                  <p class=$paragraphClass>${address.addressLine1}</p>
                  ${address.addressLine2.fold("")(
              address => s"<p class=$paragraphClass>$address</p>"
            )}
                  ${address.addressLine3.fold("")(
              address => s"<p class=$paragraphClass>$address</p>"
            )}
                  ${address.addressLine4.fold("")(
              address => s"<p class=$paragraphClass>$address</p>"
            )}
                 <p class=$paragraphClass>${address.postCodeFormatter(address.postalCode).getOrElse("")}</p>
                 ${if (address.countryCode.toUpperCase != "GB") s"<p $paragraphClass>$countryDescription</p>" else ""}
                  """)

            Some(SummaryListRowViewModel(
              key     = "businessWithIDName.checkYourAnswersLabel",
              value   = ValueViewModel(HtmlContent(value)),
              actions = Seq(
                ActionItemViewModel(
                  content = HtmlContent(
                    s"""
                       |<span aria-hidden="true">${messages("site.change")}</span>
                       |<span class="govuk-visually-hidden">${messages("businessWithIDName.change.hidden")}</span>
                       |""".stripMargin
                  ),
                  href = routes.IsRegisteredAddressInUkController.onPageLoad(CheckMode).url
                ).withAttribute(("id","your-business-details"))
              )
            ))
          case _ => None
        }

      case _ => None
    }
  }


}
