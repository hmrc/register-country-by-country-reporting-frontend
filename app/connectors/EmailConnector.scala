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
import models.email.EmailRequest
import play.api.Logging
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailConnector @Inject() (val config: FrontendAppConfig, http: HttpClientV2)(implicit ex: ExecutionContext) extends Logging {

  def sendEmail(emailRequest: EmailRequest)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    http.post(url"${config.sendEmailUrl}/hmrc/email").withBody(Json.toJson(emailRequest)).execute[HttpResponse] recoverWith {
      case e: Exception =>
        logger.warn(s"EmailConnector: The email could not be sent to the EMAIL service - ${e.getMessage}")
        Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "The email could not be sent to the EMAIL service"))
    }

}
