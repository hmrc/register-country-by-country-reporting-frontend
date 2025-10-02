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
import models.{GroupIds, SubscriptionInfo}
import play.api.Logging
import play.api.http.Status.NO_CONTENT
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.HttpErrorFunctions.is2xx
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EnrolmentStoreProxyConnector @Inject() (val config: FrontendAppConfig, val http: HttpClientV2) extends Logging {

  def enrolmentExists(subscriptionInfo: SubscriptionInfo)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Boolean] = {

    val serviceEnrolmentPattern = subscriptionInfo.utr
      .map {
        utr =>
          s"HMRC-CBC-ORG~cbcId~${subscriptionInfo.cbcId}~UTR~$utr"
      }
      .getOrElse(s"HMRC-CBC-NONUK-ORG~cbcId~${subscriptionInfo.cbcId}")

    val submissionUrl = url"${config.enrolmentStoreProxyUrl}/enrolment-store/enrolments/$serviceEnrolmentPattern/groups"

    http
      .get(submissionUrl)
      .execute[HttpResponse]
      .map {
        case response if response.status == NO_CONTENT => false
        case response if is2xx(response.status) =>
          response.json
            .asOpt[GroupIds]
            .exists(
              groupIds =>
                if (groupIds.principalGroupIds.nonEmpty) {
                  true
                } else {
                  false
                }
            )
        case response =>
          logger.error(s"Enrolment response not formed. ${response.status} response status")
          throw new IllegalStateException()
      }

  }
}
