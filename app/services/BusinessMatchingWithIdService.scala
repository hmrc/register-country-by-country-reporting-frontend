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

import cats.data.OptionT
import cats.data.OptionT.{fromOption, liftF}
import connectors.RegistrationConnector
import controllers.{routes, CreateSubscriptionAndUpdateEnrolment}
import models.IdentifierType.UTR
import models.matching.{AutoMatchedRegistrationRequest, RegistrationInfo, RegistrationRequest}
import models.register.request.RegisterWithID
import models.requests.DataRequest
import models.{NormalMode, UUIDGen, UniqueTaxpayerReference, UserAnswers}
import pages.{BusinessNamePage, BusinessTypePage, RegistrationInfoPage, UTRPage}
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Result}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Clock
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessMatchingWithIdService @Inject() (registrationConnector: RegistrationConnector,
                                               override val sessionRepository: SessionRepository,
                                               override val subscriptionService: SubscriptionService,
                                               override val taxEnrolmentService: TaxEnrolmentService,
                                               uuidGen: UUIDGen,
                                               clock: Clock
)(implicit
  ec: ExecutionContext
) extends Logging
    with CreateSubscriptionAndUpdateEnrolment {
  implicit private val uuidGenerator: UUIDGen = uuidGen
  implicit private val implicitClock: Clock   = clock

  def sendBusinessRegistrationInformation(registrationPayload: RegisterWithID)(implicit hc: HeaderCarrier): Future[RegistrationInfo] =
    registrationConnector
      .registerWithID(registrationPayload)
      .map { response =>
        RegistrationInfo.apply(response)
      }

  def buildRegisterWithIdForAutoMatched(autoMatchedUtr: UniqueTaxpayerReference): RegisterWithID =
    RegisterWithID(AutoMatchedRegistrationRequest(UTR, autoMatchedUtr.uniqueTaxPayerReference))

  def buildRegistrationRequest(userAnswers: UserAnswers): Option[RegisterWithID] =
    for {
      utr          <- userAnswers.get(UTRPage)
      businessName <- userAnswers.get(BusinessNamePage)
      businessType = userAnswers.get(BusinessTypePage)
    } yield RegisterWithID(RegistrationRequest(UTR, utr.uniqueTaxPayerReference, businessName, businessType, None))

  def selfHealingLogic()(implicit hc: HeaderCarrier, request: DataRequest[AnyContent]): Future[Result] =
    (for {
      registrationInfo    <- fromOption[Future](request.userAnswers.get(RegistrationInfoPage))
      maybeSubscriptionId <- liftF(subscriptionService.getDisplaySubscriptionId(registrationInfo.safeId))
      subscriptionId      <- fromOption[Future](maybeSubscriptionId)
      result              <- liftF(updateSubscriptionIdAndCreateEnrolment(registrationInfo.safeId, subscriptionId))
    } yield result).getOrElse(Redirect(controllers.routes.YourContactDetailsController.onPageLoad(NormalMode)))

}
