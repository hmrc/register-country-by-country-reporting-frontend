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

import config.FrontendAppConfig
import connectors.EmailConnector
import models.email.EmailRequest
import models.{SubscriptionID, UserAnswers}
import pages._
import play.api.Logging
import play.api.http.Status.ACCEPTED
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailService @Inject()(emailConnector: EmailConnector,
                             appConfig: FrontendAppConfig)
                            (implicit executionContext: ExecutionContext) extends Logging {

  private def sendAndLogEmail(emailRequest: EmailRequest)(implicit hc: HeaderCarrier): Future[Int] =
    emailConnector.sendEmail(emailRequest) map { resp =>
      resp.status match {
        case ACCEPTED => logger.info("Email queued")
        case _ => logger.warn(s"Email service failed to send an email")
      }
      resp.status
    }

  def sendEmail(userAnswers: UserAnswers, subscriptionID: SubscriptionID)(implicit hc: HeaderCarrier): Future[Option[Int]] = {

    val primaryResponse = userAnswers.get(ContactEmailPage).fold(Future.successful(Option.empty[Int])) { contactEmail =>
      sendAndLogEmail(EmailRequest(contactEmail,
        appConfig.emailOrganisationTemplate,
        subscriptionID.value, userAnswers.get(ContactNamePage))).map(Some(_))
    }

    userAnswers.get(SecondContactEmailPage).fold(Future.successful(Option.empty[Int]))  {contactEmail =>
      sendAndLogEmail( EmailRequest(contactEmail,
        appConfig.emailOrganisationTemplate,
        subscriptionID.value, userAnswers.get(SecondContactNamePage))).map(Some(_))
    }

    primaryResponse

  }
}
