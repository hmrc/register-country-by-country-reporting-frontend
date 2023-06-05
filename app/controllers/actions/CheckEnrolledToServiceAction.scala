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

package controllers.actions

import config.FrontendAppConfig
import models.requests.IdentifierRequest
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}

import javax.inject.Inject
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

class CheckEnrolledToServiceAction @Inject() (config: FrontendAppConfig)(implicit val executionContext: ExecutionContext)
    extends ActionFilter[IdentifierRequest]
    with Logging {

  override protected def filter[A](request: IdentifierRequest[A]): Future[Option[Result]] = {
    val validKeys: Set[String] = Set(config.enrolmentKey, config.nonUkEnrolmentKey)
    if (request.enrolments.exists(enrolment => validKeys.contains(enrolment.key))) {
      logger.info(s"User is enrolled to the CBC service")
      successful(Some(Redirect(config.countryByCountryReportingFrontendUrl)))
    } else {
      successful(None)
    }
  }
}

class CheckEnrolledToServiceActionProvider @Inject() (config: FrontendAppConfig)(implicit ec: ExecutionContext) {

  def apply[T](): ActionFilter[IdentifierRequest] =
    new CheckEnrolledToServiceAction(config)
}
