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
import connectors.SubscriptionConnector
import models.{Address, Country, SafeId, SubscriptionCreateError, SubscriptionCreateInformationMissingError, SubscriptionID, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubscriptionServiceSpec extends SpecBase with MockitoSugar with ScalaCheckPropertyChecks {

  val mockSubscriptionConnector: SubscriptionConnector = mock[SubscriptionConnector]

  val service: SubscriptionService = app.injector.instanceOf[SubscriptionService]

  override lazy val app: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[SubscriptionConnector].toInstance(mockSubscriptionConnector)
    )
    .build()

  override def beforeEach: Unit = {
    reset(mockSubscriptionConnector)
    super.beforeEach()
  }

  "SubscriptionService" - {
    "must return 'SubscriptionID' on creating subscription" in {
      val subscriptionID                                             = SubscriptionID("id")
      val responseCreateSubscription: Future[Option[SubscriptionID]] = Future.successful(Some(subscriptionID))
      val safeId                                                     = SafeId("CBC12345678")
      when(mockSubscriptionConnector.readSubscription(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockSubscriptionConnector.createSubscription(any())(any(), any())).thenReturn(responseCreateSubscription)

      val userAnswers = UserAnswers("")
        .set(DoYouHaveUTRPage, false)
        .success
        .value
        .set(ContactNamePage, "TestName")
        .success
        .value
        .set(ContactEmailPage, "test@gmail.com")
        .success
        .value
        .set(ContactPhonePage, "000000000")
        .success
        .value

      val result = service.checkAndCreateSubscription(safeId, userAnswers)
      result.futureValue mustBe Right(SubscriptionID("id"))

      verify(mockSubscriptionConnector, times(1)).readSubscription(any())(any(), any())
      verify(mockSubscriptionConnector, times(1)).createSubscription(any())(any(), any())
    }

    "must return 'SubscriptionID' when there is already a subscription exists" in {
      val subscriptionID = SubscriptionID("id")
      val safeId         = SafeId("CBC12345678")

      when(mockSubscriptionConnector.readSubscription(any())(any(), any())).thenReturn(Future.successful(Some(subscriptionID)))

      val result = service.checkAndCreateSubscription(safeId, emptyUserAnswers)
      result.futureValue mustBe Right(subscriptionID)
    }

    "must return SubscriptionCreateInformationMissingError when UserAnswers is empty" in {
      val responseCreateSubscription: Future[Option[SubscriptionID]] = Future.successful(None)
      val safeId                                                     = SafeId("CBC12345678")

      when(mockSubscriptionConnector.readSubscription(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockSubscriptionConnector.createSubscription(any())(any(), any())).thenReturn(responseCreateSubscription)

      val result = service.checkAndCreateSubscription(safeId, UserAnswers("id"))

      result.futureValue mustBe Left(SubscriptionCreateInformationMissingError("Primary ContactInformation"))
    }

    "must return error when it fails to create subscription" in {
      val safeId = SafeId("CBC12345678")
      val userAnswers = UserAnswers("id")
        .set(DoYouHaveUTRPage, false)
        .success
        .value
        .set(ContactNamePage, "TestName")
        .success
        .value
        .set(ContactEmailPage, "test@gmail.com")
        .success
        .value
        .set(ContactPhonePage, "000000000")
        .success
        .value
      val responseCreateSubscription: Future[Option[SubscriptionID]] = Future.successful(None)

      when(mockSubscriptionConnector.readSubscription(any())(any(), any())).thenReturn(Future.successful(None))
      when(mockSubscriptionConnector.createSubscription(any())(any(), any())).thenReturn(responseCreateSubscription)

      val result = service.checkAndCreateSubscription(safeId, userAnswers)

      result.futureValue mustBe Left(SubscriptionCreateError)

    }

    "getDisplaySubscriptionId" - {

      "must return 'SubscriptionID' for valid input" in {
        val safeId = SafeId("CBC12345678")

        when(mockSubscriptionConnector.readSubscription(any())(any(), any())).thenReturn(Future.successful(Some(SubscriptionID("id"))))
        val result = service.getDisplaySubscriptionId(safeId)
        result.futureValue mustBe Some(SubscriptionID("id"))
      }

      "must return 'None' for any failures of exceptions" in {
        val safeId = SafeId("CBC12345678")
        when(mockSubscriptionConnector.readSubscription(any())(any(), any())).thenReturn(Future.successful(None))
        val result = service.getDisplaySubscriptionId(safeId)
        result.futureValue mustBe None
      }
    }
  }

}
