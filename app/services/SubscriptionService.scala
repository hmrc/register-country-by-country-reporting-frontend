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

import connectors.SubscriptionConnector
import models.subscription.request.{CreateSubscriptionForCBCRequest, SubscriptionRequest}
import models.{ApiError, SafeId, SubscriptionCreateError, SubscriptionID, UserAnswers}
import pages.{BusinessWithoutIDNamePage, RegistrationInfoPage}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubscriptionService @Inject() (val subscriptionConnector: SubscriptionConnector) {

  def checkAndCreateSubscription(safeID: SafeId, userAnswers: UserAnswers)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Either[ApiError, SubscriptionID]] = {
    val businessName = userAnswers
      .get(BusinessWithoutIDNamePage)
      .orElse(userAnswers.get(RegistrationInfoPage).map(_.name))
      .getOrElse("Business name missing")
    getDisplaySubscriptionId(safeID) flatMap {
      case Some(subscriptionID) => Future.successful(Right(subscriptionID))
      case _ =>
        SubscriptionRequest.createSubscriptionRequest(safeID, userAnswers) match {
          case Right(subscriptionRequest) =>
            subscriptionConnector.createSubscription(CreateSubscriptionForCBCRequest(subscriptionRequest), businessName) map {
              case Some(subscriptionID) =>
                Right(subscriptionID)
              case None => Left(SubscriptionCreateError)
            }
          case Left(value) => Future.successful(Left(value))
        }
    }
  }

  def getDisplaySubscriptionId(safeId: SafeId)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Option[SubscriptionID]] =
    subscriptionConnector.readSubscription(safeId)
}
