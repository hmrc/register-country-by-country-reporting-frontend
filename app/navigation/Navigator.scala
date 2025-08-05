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

package navigation

import play.api.mvc.Call
import controllers.routes
import pages._
import models._
import play.api.libs.json.Reads

class Navigator {

  val normalRoutes: PartialFunction[Page, UserAnswers => Call] = PartialFunction.empty

  val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = PartialFunction.empty

  private def checkRoutes(mode: Mode): PartialFunction[Page, UserAnswers => Call] = mode match {
    case NormalMode =>
      normalRoutes orElse {
        case _ => _ => routes.IndexController.onPageLoad
      }
    case CheckMode =>
      checkRouteMap orElse {
        case _ => _ => routes.CheckYourAnswersController.onPageLoad()
      }
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = checkRoutes(mode)(page)(userAnswers)

  def checkNextPageForValueThenRoute[A](mode: Mode, ua: UserAnswers, page: QuestionPage[A], call: Call)(implicit rds: Reads[A]): Call =
    if (
      mode.equals(CheckMode) && ua
        .get(page)
        .fold(false)(
          _ => true
        )
    ) {
      routes.CheckYourAnswersController.onPageLoad()
    } else {
      call
    }

}
