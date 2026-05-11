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

package controllers.actions

import config.FrontendAppConfig
import controllers.routes
import models.requests.IdentifierRequest
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}
import services.TaxEnrolmentService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

class CheckEnrolmentForGroupAction @Inject() (config: FrontendAppConfig, taxEnrolmentService: TaxEnrolmentService)(implicit
  val executionContext: ExecutionContext
) extends ActionFilter[IdentifierRequest] {

  override protected def filter[A](request: IdentifierRequest[A]): Future[Option[Result]] =
    request.groupId match {
      case Some(groupId) =>
        implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
        taxEnrolmentService
          .checkGroupIdHasExistingEnrolment(groupId)
          .map(haveEnrolment => navigate(haveEnrolment))
      case None => successful(None)
    }

  private def navigate(haveEnrolment: Boolean): Option[Result] =
    if haveEnrolment then Some(Redirect(routes.OrganisationAlreadyRegisteredController.onPageLoad().url))
    else None

}

class CheckEnrolmentForGroupActionProvider @Inject() (config: FrontendAppConfig, taxEnrolmentService: TaxEnrolmentService)(implicit ec: ExecutionContext) {

  def apply[T](): ActionFilter[IdentifierRequest] =
    new CheckEnrolmentForGroupAction(config, taxEnrolmentService)
}
