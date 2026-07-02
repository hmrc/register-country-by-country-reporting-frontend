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

package views

import base.SpecBase
import org.jsoup.Jsoup
import play.api.i18n.{Lang, Messages}
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import play.twirl.api.HtmlFormat
import utils.ViewHelper
import views.html.{InterruptPageView, OrganisationAlreadyRegisteredView}

class OrganisationAlreadyRegisteredViewSpec extends SpecBase with Injecting with ViewHelper {

  implicit private val request: FakeRequest[AnyContent] = FakeRequest()

  "OrganisationAlreadyRegisteredView" - {
    "should render page components" in {
      val view1: OrganisationAlreadyRegisteredView                          = inject[OrganisationAlreadyRegisteredView]
      val messagesControllerComponentsForView: MessagesControllerComponents = inject[MessagesControllerComponents]
      implicit val messages: Messages                                       = messagesControllerComponentsForView.messagesApi.preferred(Seq(Lang("en")))

      val renderedHtml: HtmlFormat.Appendable =
        view1()
      lazy val doc = Jsoup.parse(renderedHtml.body)

      getWindowTitle(doc) must include("Your organisation is already registered to use this service")
      val pageHeadings = getPageHeading(doc)
      pageHeadings must include("Your organisation is already registered to use this service")
      val paragraphValues = getAllParagraph(doc).text()
      paragraphValues must include("You have signed in with a Government Gateway user ID that does not have permission to use this service.")
      paragraphValues must include("To access the country-by-country reporting service, you must either:")
      paragraphValues must include(
        "You can email your HMRC Customer Compliance Manager or msb.countrybycountryreportingmailbox@hmrc.gov.uk if you need support with accessing this service."
      )
      elementText(doc, "li") must include("sign in with the Government Gateway user ID used to register for this service")
      elementText(doc, "li") must include("give yourself permission to use this service through the")
      elementText(doc, "a") must include("tax and scheme management service")
    }
  }

}
