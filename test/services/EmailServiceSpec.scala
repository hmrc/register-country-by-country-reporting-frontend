/*
 * Copyright 2026 HM Revenue & Customs
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
import models.email.EmailRecipient
import models.matching.RegistrationInfo
import models.register.response.details.AddressResponse
import models.{SafeId, SubscriptionID}
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{ContactEmailPage, ContactNamePage, RegistrationInfoPage, SecondContactEmailPage}
import play.api.Application
import play.api.http.Status.{ACCEPTED, INTERNAL_SERVER_ERROR}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HttpResponse
import scala.concurrent.Future

class EmailServiceSpec extends SpecBase with BeforeAndAfterEach with Generators with ScalaCheckPropertyChecks {

  override def beforeEach(): Unit =
    reset(
      mockEmailConnector,
      mockSubscriptionService
    )

  private val mockEmailConnector: EmailConnector           = mock[EmailConnector]
  private val mockSubscriptionService: SubscriptionService = mock[SubscriptionService]

  override lazy val app: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[EmailConnector].toInstance(mockEmailConnector),
      bind[SubscriptionService].toInstance(mockSubscriptionService)
    )
    .build()

  val emailService: EmailService = app.injector.instanceOf[EmailService]

  val subscriptionID: SubscriptionID = SubscriptionID("XACBC0000123456")

  private val registrationInfo: RegistrationInfo =
    RegistrationInfo(
      SafeId("safe-id"),
      "Company Name",
      AddressResponse("", None, None, None, None, "GB")
    )

  "EmailService" - {

    "return status 'ACCEPTED' on Sending email successfully for valid input" in {

      when(mockEmailConnector.sendEmail(any())(any()))
        .thenReturn(Future.successful(HttpResponse(ACCEPTED, "")))

      val userAnswers = emptyUserAnswers
        .set(ContactEmailPage, "test@gmail.com")
        .success
        .value
        .set(ContactNamePage, "Test Name!")
        .success
        .value
        .set(SecondContactEmailPage, "test@gmail.com")
        .success
        .value

      val result = emailService.sendEmail(userAnswers, SubscriptionID("Id"))

      result.futureValue mustBe Seq(ACCEPTED, ACCEPTED)

      verify(mockEmailConnector, times(2)).sendEmail(any())(any())
      verify(mockSubscriptionService, never()).getSubscriptionEmailRecipients(any())(any())
    }

    "return status 'INTERNAL_SERVER_ERROR' on failing to Send email" in {

      when(mockEmailConnector.sendEmail(any())(any()))
        .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

      val userAnswers = emptyUserAnswers
        .set(ContactEmailPage, "test@gmail.com")
        .success
        .value
        .set(ContactNamePage, "Test name!")
        .success
        .value

      val result = emailService.sendEmail(userAnswers, SubscriptionID("Id"))

      result.futureValue mustBe Seq(INTERNAL_SERVER_ERROR)

      verify(mockEmailConnector, times(1)).sendEmail(any())(any())
      verify(mockSubscriptionService, never()).getSubscriptionEmailRecipients(any())(any())
    }

    "send emails using subscription recipients when user answer email recipients are missing" in {

      when(mockSubscriptionService.getSubscriptionEmailRecipients(any())(any()))
        .thenReturn(
          Future.successful(
            Seq(
              EmailRecipient(
                email = "primary@test.com",
                name = "Primary Organisation"
              ),
              EmailRecipient(
                email = "secondary@test.com",
                name = "Secondary Organisation"
              )
            )
          )
        )

      when(mockEmailConnector.sendEmail(any())(any()))
        .thenReturn(Future.successful(HttpResponse(ACCEPTED, "")))

      val userAnswers = emptyUserAnswers
        .set(RegistrationInfoPage, registrationInfo)
        .success
        .value

      val result = emailService.sendEmail(userAnswers, subscriptionID)

      result.futureValue mustBe Seq(ACCEPTED, ACCEPTED)

      verify(mockSubscriptionService, times(1)).getSubscriptionEmailRecipients(registrationInfo.safeId)
      verify(mockEmailConnector, times(2)).sendEmail(any())(any())
    }

    "return empty sequence and not send emails when no recipients are found from subscription" in {

      when(mockSubscriptionService.getSubscriptionEmailRecipients(any())(any()))
        .thenReturn(Future.successful(Seq.empty[EmailRecipient]))

      val userAnswers = emptyUserAnswers
        .set(RegistrationInfoPage, registrationInfo)
        .success
        .value

      val result = emailService.sendEmail(userAnswers, subscriptionID)

      result.futureValue mustBe Seq.empty[Int]

      verify(mockSubscriptionService, times(1)).getSubscriptionEmailRecipients(registrationInfo.safeId)
      verify(mockEmailConnector, never()).sendEmail(any())(any())
    }

    "return empty sequence and not send emails when no user answer or subscription recipients are available" in {

      val result = emailService.sendEmail(emptyUserAnswers, subscriptionID)

      result.futureValue mustBe Seq.empty[Int]

      verify(mockSubscriptionService, never()).getSubscriptionEmailRecipients(any())(any())
      verify(mockEmailConnector, never()).sendEmail(any())(any())
    }

    "use user answer recipients instead of subscription recipients when user answer recipients are available" in {

      when(mockEmailConnector.sendEmail(any())(any()))
        .thenReturn(Future.successful(HttpResponse(ACCEPTED, "")))

      val userAnswers = emptyUserAnswers
        .set(ContactEmailPage, "test@gmail.com")
        .success
        .value
        .set(ContactNamePage, "Test Name!")
        .success
        .value
        .set(RegistrationInfoPage, registrationInfo)
        .success
        .value

      val result = emailService.sendEmail(userAnswers, subscriptionID)

      result.futureValue mustBe Seq(ACCEPTED)

      verify(mockEmailConnector, times(1)).sendEmail(any())(any())
      verify(mockSubscriptionService, never()).getSubscriptionEmailRecipients(any())(any())
    }
  }
}
