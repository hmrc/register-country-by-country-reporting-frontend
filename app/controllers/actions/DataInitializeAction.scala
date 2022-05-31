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

package controllers.actions

import models.UserAnswers
import models.requests.{DataRequest, OptionalDataRequest}
import play.api.mvc.{ActionRefiner, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataInitializeActionWithRegime()(implicit val executionContext: ExecutionContext) extends ActionRefiner[OptionalDataRequest, DataRequest] {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] =
    request.userAnswers match {
      case None =>
        Future.successful(Right(DataRequest(request.request, request.userId, UserAnswers(request.userId))))
      case Some(data) =>
        Future.successful(Right(DataRequest(request.request, request.userId, data)))
    }
}

class DataInitializeActionImpl @Inject() (implicit val executionContext: ExecutionContext) extends DataInitializeAction {
  override def apply(): ActionRefiner[OptionalDataRequest, DataRequest] = new DataInitializeActionWithRegime()
}

trait DataInitializeAction {
  def apply(): ActionRefiner[OptionalDataRequest, DataRequest]
}
