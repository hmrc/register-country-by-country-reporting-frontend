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
import models.email.{EmailRecipient, EmailRequest}
import models.{SubscriptionID, UserAnswers}
import pages.*
import play.api.Logging
import play.api.http.Status.ACCEPTED
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailService @Inject() (emailConnector: EmailConnector, appConfig: FrontendAppConfig, subscriptionService: SubscriptionService)(implicit
  executionContext: ExecutionContext
) extends Logging {

  private def submitEmailRequest(emailRequest: EmailRequest)(implicit hc: HeaderCarrier): Future[Int] =
    emailConnector.sendEmail(emailRequest) map { resp =>
      resp.status match {
        case ACCEPTED => logger.info("Email queued")
        case _        => logger.warn("Email service failed to send an email")
      }
      resp.status
    }

  // Fetches the users email / name from their own answers in the journey - they may not have hit these pages so can be empty
  private def userAnswerRecipients(userAnswers: UserAnswers): Seq[EmailRecipient] = {
    val answerEmails =
      Seq(
        userAnswers.get(ContactEmailPage),
        userAnswers.get(SecondContactEmailPage)
      ).flatten

    userAnswers.get(ContactNamePage).fold(Seq.empty[EmailRecipient]) { contactName =>
      answerEmails.map { email =>
        EmailRecipient(
          email = email,
          name = contactName
        )
      }
    }
  }
// Makes a call to the subscriptionConnector to fetch the users primary and secondary contact (This can also be empty)
  private def subscriptionRecipients(userAnswers: UserAnswers)(implicit
    hc: HeaderCarrier
  ): Future[Seq[EmailRecipient]] =
    userAnswers
      .get(RegistrationInfoPage)
      .fold(Future.successful(Seq.empty[EmailRecipient])) { registrationInfo =>
        subscriptionService.getSubscriptionEmailRecipients(registrationInfo.safeId)
      }

  private def recipientsFor(userAnswers: UserAnswers)(implicit
    hc: HeaderCarrier
  ): Future[Seq[EmailRecipient]] = {
    val recipients = userAnswerRecipients(userAnswers)

    if (recipients.nonEmpty) Future.successful(recipients)
    else subscriptionRecipients(userAnswers)
  }

  private def emailRequest(
    recipient: EmailRecipient,
    subscriptionID: SubscriptionID
  ): EmailRequest =
    EmailRequest(
      recipient.email,
      appConfig.emailOrganisationTemplate,
      subscriptionID.value,
      recipient.name
    )

  def sendEmail(userAnswers: UserAnswers, subscriptionID: SubscriptionID)(implicit
    hc: HeaderCarrier
  ): Future[Seq[Int]] =
    recipientsFor(userAnswers).flatMap {
      case Nil =>
        logger.warn(
          s"No email recipients found for subscriptionID ${subscriptionID.value}; email not sent"
        )
        Future.successful(Seq.empty[Int])

      case recipients =>
        Future.traverse(recipients) { recipient =>
          submitEmailRequest(emailRequest(recipient, subscriptionID))
        }
    }

}
