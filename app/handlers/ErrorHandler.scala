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

package handlers

import config.FrontendAppConfig
import controllers.routes
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.{NotFound, Redirect}
import play.api.mvc.{RequestHeader, Result}
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import views.html.{ErrorTemplate, PageNotFoundView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ErrorHandler @Inject() (
  val messagesApi: MessagesApi,
  view: ErrorTemplate,
  notFoundView: PageNotFoundView,
  frontendAppConfig: FrontendAppConfig
)(implicit override val ec: ExecutionContext)
    extends FrontendErrorHandler
    with I18nSupport {

  override def notFoundTemplate(implicit request: RequestHeader): Future[Html] = Future.successful(notFoundView(frontendAppConfig.emailEnquiries))

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    statusCode match {
      case play.mvc.Http.Status.NOT_FOUND => notFoundTemplate(request).map(NotFound(_))
      case _                              => Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
    }

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit request: RequestHeader): Future[Html] =
    Future.successful(view(pageTitle, heading, message))
}
