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

package services

import base.SpecBase
import connectors.EmailConnector
import generators.Generators
import models.SubscriptionID
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.ContactEmailPage
import play.api.http.Status.{ACCEPTED, BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, inject}
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class EmailServiceSpec extends SpecBase with BeforeAndAfterEach with Generators with ScalaCheckPropertyChecks {

  private val mockEmailConnector: EmailConnector = mock[EmailConnector]

  lazy override val app: Application = new GuiceApplicationBuilder()
    .overrides(
      inject.bind[EmailConnector].toInstance(mockEmailConnector)
    ).build()

  override def beforeEach: Unit =
    reset(
      mockEmailConnector
    )

  val emailService: EmailService = app.injector.instanceOf[EmailService]

  val subscriptionID: SubscriptionID = SubscriptionID("XACBC0000123456")

  "EmailService" - {
    "return status 'ACCEPTED' on Sending email successfully for valid input" in {

      when(mockEmailConnector.sendEmail(any())(any())).thenReturn(Future.successful(HttpResponse(ACCEPTED, "")))

      val userAnswers = emptyUserAnswers.set(ContactEmailPage, "test@gmail.com").success.value

      val result: Future[Int] = emailService.sendEmail(userAnswers, SubscriptionID("Id"))

      result.futureValue mustBe ACCEPTED
    }

    "return status 'NOT_FOUND' on failing to Send email" in {

      when(mockEmailConnector.sendEmail(any())(any())).thenReturn(Future.successful(HttpResponse(NOT_FOUND, "")))

      val userAnswers = emptyUserAnswers.set(ContactEmailPage, "test@gmail.com").success.value

      val result: Future[Int] = emailService.sendEmail(userAnswers, SubscriptionID("Id"))

      result.futureValue mustBe NOT_FOUND
    }

    "return status 'BAD_REQUEST' on failing to Send email" in {

      when(mockEmailConnector.sendEmail(any())(any())).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))

      val userAnswers = emptyUserAnswers.set(ContactEmailPage, "test@gmail.com").success.value

      val result: Future[Int] = emailService.sendEmail(userAnswers, SubscriptionID("Id"))

      result.futureValue mustBe BAD_REQUEST
    }

    "return status 'INTERNAL_SERVER_ERROR' on failing to Send email" in {

      when(mockEmailConnector.sendEmail(any())(any())).thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

      val userAnswers = emptyUserAnswers.set(ContactEmailPage, "test@gmail.com").success.value

      val result: Future[Int] = emailService.sendEmail(userAnswers, SubscriptionID("Id"))

      result.futureValue mustBe INTERNAL_SERVER_ERROR
    }
  }

}
