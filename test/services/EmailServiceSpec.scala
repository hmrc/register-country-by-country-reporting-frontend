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

package services

import base.SpecBase
import config.FrontendAppConfig
import connectors.EmailConnector
import generators.Generators
import models.SubscriptionID
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{ContactEmailPage, SecondContactEmailPage}
import play.api.Application
import play.api.http.Status.{ACCEPTED, INTERNAL_SERVER_ERROR}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmailServiceSpec extends SpecBase with BeforeAndAfterEach with Generators with ScalaCheckPropertyChecks {

  override def beforeEach(): Unit =
    reset(
      mockEmailConnector
    )

  private val mockEmailConnector: EmailConnector = mock[EmailConnector]
  private val mockAppConfig: FrontendAppConfig   = mock[FrontendAppConfig]

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .overrides(
      bind[EmailConnector].toInstance(mockEmailConnector)
    )
    .build()

  val mockEmailService: EmailService = new EmailService(mockEmailConnector, mockAppConfig)

  val subscriptionID: SubscriptionID = SubscriptionID("XACBC0000123456")

  "EmailService" - {
    "return status 'ACCEPTED' on Sending email successfully for valid input" in {

      when(mockEmailConnector.sendEmail(any())(any())).thenReturn(Future.successful(HttpResponse(ACCEPTED, "")))

      val userAnswers = emptyUserAnswers
        .set(ContactEmailPage, "test@gmail.com")
        .success
        .value
        .set(SecondContactEmailPage, "test@gmail.com")
        .success
        .value

      val result = mockEmailService.sendEmail(userAnswers, SubscriptionID("Id"))

      result.futureValue.value mustBe ACCEPTED

      verify(mockEmailConnector, times(2)).sendEmail(any())(any())
    }

    "return status 'INTERNAL_SERVER_ERROR' on failing to Send email" in {

      when(mockEmailConnector.sendEmail(any())(any())).thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

      val userAnswers = emptyUserAnswers.set(ContactEmailPage, "test@gmail.com").success.value

      val result = mockEmailService.sendEmail(userAnswers, SubscriptionID("Id"))

      result.futureValue.value mustBe INTERNAL_SERVER_ERROR
      verify(mockEmailConnector, times(1)).sendEmail(any())(any())
    }
  }

}
