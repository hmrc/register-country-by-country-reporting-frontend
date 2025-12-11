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
import config.FrontendAppConfig
import org.jsoup.Jsoup
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages}
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import play.twirl.api.HtmlFormat
import utils.ViewHelper
import views.html.UnauthorisedView

class UnauthorisedViewSpec extends SpecBase with GuiceOneAppPerSuite with Injecting with ViewHelper {

  val view1: UnauthorisedView                                           = app.injector.instanceOf[UnauthorisedView]
  val frontendAppConfig: FrontendAppConfig                              = app.injector.instanceOf[FrontendAppConfig]
  val messagesControllerComponentsForView: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]

  implicit private val request: FakeRequest[AnyContent] = FakeRequest()
  implicit private val messages: Messages               = messagesControllerComponentsForView.messagesApi.preferred(Seq(Lang("en")))
  private val renderedHtml: HtmlFormat.Appendable =
    view1(frontendAppConfig.emailEnquiries)
  private lazy val doc = Jsoup.parse(renderedHtml.body)

  "UnauthorisedView" - {
    "should render page components" in {

      getWindowTitle(doc) must include("You cannot access this page")
      getPageHeading(doc) mustEqual "You cannot access this page"
      getAllParagraph(doc).text() must include(
        "You can email your HMRC Customer Compliance Manager or msb.countrybycountryreportingmailbox@hmrc.gov.uk if you need support with using the service."
      )
    }
    "should have guidance link" in {
      val elem = doc.getElementById("guidance_link")
      elem.attr("href") mustEqual "https://www.gov.uk/guidance/send-a-country-by-country-report#how-to-create-your-report"
    }
  }

}
