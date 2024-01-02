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

import connectors.RegistrationConnector
import models.register.request.{ContactDetails, RegisterWithoutId}
import models.requests.DataRequest
import models.{ApiError, MandatoryInformationMissingError, RegistrationWithoutIdInformationMissingError, SafeId, UUIDGen}
import pages._
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.HeaderCarrier
import models.register.request.Address
import play.api.Logging

import java.time.Clock
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegisterWithoutIdService @Inject() (registrationConnector: RegistrationConnector, uuidGen: UUIDGen, clock: Clock)(implicit ec: ExecutionContext) extends Logging {

  implicit private val uuidGenerator: UUIDGen = uuidGen
  implicit private val implicitClock: Clock = clock
  def registerWithoutId()(implicit request: DataRequest[AnyContent], hc: HeaderCarrier): Future[Either[ApiError, SafeId]] =
    {for {
      organisationName <- request.userAnswers.get(BusinessWithoutIDNamePage)
      phoneNumber  = request.userAnswers.get(ContactPhonePage)
      emailAddress = request.userAnswers.get(ContactEmailPage)
      address <- request.userAnswers.get(BusinessWithoutIdAddressPage)
      _       <- request.userAnswers.get(DoYouHaveSecondContactPage)
    } yield
      sendBusinessRegistration(organisationName, Address.fromAddress(address), ContactDetails(phoneNumber, None, None, emailAddress))
    }.getOrElse {
        logger.warn("Missing Registration Information")
        registrationError
      }

  private val registrationError = Future.successful(Left(MandatoryInformationMissingError("Registration Information Missing")))

  def sendBusinessRegistration(businessName: String, address: Address, contactDetails: ContactDetails)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Either[ApiError, SafeId]] =
    registrationConnector
      .registerWithoutID(RegisterWithoutId(businessName, address, contactDetails)) map {
      case Right(Some(safeId)) => Right(safeId)
      case Right(None) =>
        logger.warn("Registration WithoutId Information MissingError SafeId missing")
        Left(RegistrationWithoutIdInformationMissingError("SafeId missing"))
      case Left(error) =>
        logger.warn(s"Registration WithoutId Information $error")
        Left(error)
    }

}
