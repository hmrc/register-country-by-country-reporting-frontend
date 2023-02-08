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

import base.SpecBase
import controllers.routes
import models.requests.DataRequest
import org.scalatest.EitherValues
import pages.ContactNamePage
import play.api.http.Status.SEE_OTHER
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CheckForSubmissionActionSpec extends SpecBase with EitherValues {

  class Harness extends CheckForSubmissionActionImpl {
    def callRefine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]] = super.refine(request)
  }

  "CheckForSubmission Action" - {

    "when userAnswers is empty" - {

      "must redirect to already submitted page" in {

        val action = new Harness

        val result = action.callRefine(DataRequest(FakeRequest(), "id", emptyUserAnswers)).map(_.left.value)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustEqual routes.InformationSentController.onPageLoad().url
      }
    }

    "when userAnswers is not empty" - {

      "must allow the user to continue" in {

        val action = new Harness

        val userAnswers = emptyUserAnswers.set(ContactNamePage, "Name").success.value
        val result      = action.callRefine(DataRequest(FakeRequest(), "id", userAnswers)).futureValue

        result.isRight mustBe true
      }
    }
  }
}
