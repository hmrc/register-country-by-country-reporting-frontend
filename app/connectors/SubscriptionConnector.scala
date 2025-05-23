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

package connectors

import config.FrontendAppConfig
import models.subscription.request.CreateSubscriptionForCBCRequest
import models.subscription.response.{CreateSubscriptionResponse, DisplaySubscriptionResponse}
import models.{SafeId, SubscriptionID}
import play.api.Logging
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.HttpErrorFunctions.is2xx
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubscriptionConnector @Inject() (val config: FrontendAppConfig, val http: HttpClientV2) extends Logging {

  def readSubscription(safeId: SafeId)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[SubscriptionID]] = {

    val submissionUrl = url"${config.registerCountryByCountryUrl}/subscription/read-subscription/${safeId.value}"

    http
      .post(submissionUrl)
      .execute[HttpResponse]
      .map {
        case responseMessage if is2xx(responseMessage.status) =>
          responseMessage.json
            .asOpt[DisplaySubscriptionResponse]
            .map(_.subscriptionID)
        case errorStatus =>
          logger.warn(s"Status $errorStatus has been thrown when display subscription was called")
          None
      }
      .recover {
        case e: Exception =>
          logger.warn(s"Error message ${e.getMessage} has been thrown when display subscription was called")
          None
      }
  }

  def createSubscription(
    createSubscriptionForCBCRequest: CreateSubscriptionForCBCRequest
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[SubscriptionID]] = {

    val submissionUrl = url"${config.registerCountryByCountryUrl}/subscription/create-subscription"
    http
      .post(submissionUrl)
      .withBody(Json.toJson(createSubscriptionForCBCRequest))
      .execute[HttpResponse]
      .map {
        case response if is2xx(response.status) =>
          response.json.asOpt[CreateSubscriptionResponse].map(_.subscriptionID)
        case response =>
          logger.warn(s"Unable to create a subscription to ETMP. ${response.status} response status")
          None
      }
      .recover {
        case e: Exception =>
          logger.warn(s"Error message ${e.getMessage} has been thrown when create subscription was called")
          None
      }
  }

}
