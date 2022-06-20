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

import config.FrontendAppConfig
import connectors.EmailConnector
import models.email.EmailRequest
import models.{SubscriptionID, UserAnswers}
import pages._
import play.api.Logging
import play.api.http.Status.{ACCEPTED, BAD_REQUEST, NOT_FOUND}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailService @Inject()(emailConnector: EmailConnector,
                             appConfig: FrontendAppConfig)
                            (implicit executionContext: ExecutionContext) extends Logging {

  def sendEmail(userAnswers: UserAnswers, subscriptionID: SubscriptionID)(implicit hc: HeaderCarrier): Future[Int] = {
    val contactEmailId: List[String] = List(userAnswers.get(ContactEmailPage), userAnswers.get(SecondContactEmailPage)).flatten

    val emailRequest = EmailRequest.apply(contactEmailId,
      appConfig.emailOrganisationTemplate,
      subscriptionID.value
    )

    emailConnector.sendEmail(emailRequest) map { resp =>
      resp.status match {
        case NOT_FOUND => logger.warn("The template cannot be found within the email service")
        case BAD_REQUEST => logger.warn("Missing email or name parameter")
        case ACCEPTED => logger.info("Email queued")
        case _ => logger.warn(s"Unhandled status received from email service ${resp.status}")
      }
      resp.status
    }
  }

}
