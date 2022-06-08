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

package connectors

import config.FrontendAppConfig
import models.register.request.{RegisterWithID, RegisterWithoutId}
import models.register.response.RegisterWithIDResponse
import models.{ApiError, InternalServerError, NotFoundError}
import play.api.Logging
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.HttpReads.is2xx
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationConnector @Inject()(val config: FrontendAppConfig, val http: HttpClient) extends Logging {
  val registrationUrl = s"${config.registerCountryByCountryUrl}/registration"

  def registerWithID(registration: RegisterWithID
                                   )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ApiError, RegisterWithIDResponse]] =
    http.POST[RegisterWithID, HttpResponse](s"$registrationUrl/utr", registration) map {
      case response if is2xx(response.status) =>
        response.json.asOpt[RegisterWithIDResponse] match {
          case Some(responseDetails) => Right(responseDetails)
          case _ =>
            logger.warn("Failed to read registerWithIDResponse json")
            Left(InternalServerError)
        }
      case response if response.status == NOT_FOUND => Left(NotFoundError)
      case errorStatus =>
        logger.warn(s"RegisterWithID call failed with Status ${errorStatus.status}")
        Left(InternalServerError)
    }



}
