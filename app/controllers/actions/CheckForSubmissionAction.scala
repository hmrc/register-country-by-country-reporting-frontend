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

import controllers.routes
import models.UserAnswers
import models.requests.DataRequest
import pages.RegistrationInfoPage
import play.api.libs.json.Json
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import utils.RegistrationInformationValidator

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckForSubmissionActionImpl @Inject() (implicit val executionContext: ExecutionContext) extends CheckForSubmissionAction {

  override protected def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]] =
    if (request.userAnswers.data == Json.obj()) {
      Future.successful(Left(Redirect(routes.InformationSentController.onPageLoad())))
    } else if (isRegistrationInformationMissing(request.userAnswers)) {
      Future.successful(Left(Redirect(routes.MissingInformationController.onPageLoad())))
    } else {
      Future.successful(Right(request))
    }

  private def isRegistrationInformationMissing(userAnswers: UserAnswers) =
    userAnswers.get(RegistrationInfoPage) match {
      case Some(_) => false
      case None =>
        val validator = RegistrationInformationValidator(userAnswers)
        validator.isInformationMissing
    }
}

trait CheckForSubmissionAction extends ActionRefiner[DataRequest, DataRequest]
