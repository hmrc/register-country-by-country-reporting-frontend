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

import controllers.routes
import models.requests.DataRequest
import play.api.libs.json.Reads
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}
import queries.Gettable

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DependantAnswerAction[T] @Inject() (answer: Gettable[T])(implicit val executionContext: ExecutionContext, val reads: Reads[T])
    extends ActionFilter[DataRequest] {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] =
    request.userAnswers.get(answer) match {
      case None =>
        Future.successful(Some(Redirect(routes.JourneyRecoveryController.onPageLoad()))) // TODO - change to Some Info Missing page when built
      case Some(_) =>
        Future.successful(None)
    }
}

class DependantAnswerProvider @Inject() (implicit ec: ExecutionContext) {

  def apply[T](answer: Gettable[T])(implicit reads: Reads[T]): ActionFilter[DataRequest] =
    new DependantAnswerAction(answer)
}
