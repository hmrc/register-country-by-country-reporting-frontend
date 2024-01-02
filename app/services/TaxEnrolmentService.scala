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

import com.google.inject.Inject
import connectors.{EnrolmentStoreProxyConnector, TaxEnrolmentsConnector}
import models.{ApiError, EnrolmentCreationError, EnrolmentExistsError, SafeId, SubscriptionID, SubscriptionInfo, UserAnswers}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class TaxEnrolmentService @Inject() (taxEnrolmentsConnector: TaxEnrolmentsConnector, enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector)
    extends Logging {

  def checkAndCreateEnrolment(safeId: SafeId, userAnswers: UserAnswers, subscriptionId: SubscriptionID)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Either[ApiError, Int]] = {
    val subscriptionInfo = SubscriptionInfo(userAnswers, safeId, subscriptionId)
    enrolmentStoreProxyConnector.enrolmentExists(subscriptionInfo) flatMap {
      case false =>
        taxEnrolmentsConnector.createEnrolment(subscriptionInfo) map {
          case Some(value) => Right(value)
          case _           => Left(EnrolmentCreationError)
        }
      case true => Future.successful(Left(EnrolmentExistsError))
    }
  }

}
