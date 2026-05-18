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

import base.SpecBase
import controllers.routes
import models.requests.IdentifierRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.EitherValues
import play.api.http.Status.SEE_OTHER
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import services.TaxEnrolmentService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CheckEnrolmentForGroupActionSpec extends SpecBase with EitherValues {

  val fakeService: TaxEnrolmentService = mock[TaxEnrolmentService]

  class Harness extends CheckEnrolmentForGroupAction(fakeService) {
    def callFilter[A](request: IdentifierRequest[A]): Future[Option[Result]] = super.filter(request)
  }

  "CheckEnrolmentForGroupAction Action" - {

    "must return None When GroupId is None" in {

      val action = new Harness

      val result = action.callFilter(IdentifierRequest(FakeRequest(), "id", groupId = None))

      result.futureValue mustBe None
    }

    "must return None When Service returns false" in {

      val action = new Harness
      when(fakeService.checkGroupIdHasExistingEnrolment(any())(any(), any())).thenReturn(Future.successful(false))

      val result = action.callFilter(IdentifierRequest(FakeRequest(), "id", groupId = Some("test-group-id")))

      result.futureValue mustBe None
    }

    "must Redirect to There is a problem page When Service have failed future" in {

      val action = new Harness
      when(fakeService.checkGroupIdHasExistingEnrolment(any())(any(), any())).thenReturn(Future.failed(RuntimeException("Failed")))

      val result = action.callFilter(IdentifierRequest(FakeRequest(), "id", groupId = Some("test-group-id"))).map(_.value)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual routes.ThereIsAProblemController.onPageLoad().url
    }

    "must Redirect When Service returns true" in {

      val action = new Harness
      when(fakeService.checkGroupIdHasExistingEnrolment(any())(any(), any())).thenReturn(Future.successful(true))

      val result = action.callFilter(IdentifierRequest(FakeRequest(), "id", groupId = Some("test-group-id"))).map(_.value)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual routes.OrganisationAlreadyRegisteredController.onPageLoad().url
    }

  }
}
