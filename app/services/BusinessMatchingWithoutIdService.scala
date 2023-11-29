/*
 * Copyright 2023 HM Revenue & Customs
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
import models.{Address, ApiError, MandatoryInformationMissingError, SafeId, UUIDGen}
import pages._
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Clock
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessMatchingWithoutIdService @Inject() (registrationConnector: RegistrationConnector)(implicit ec: ExecutionContext, uuidGen: UUIDGen, clock: Clock) {


  def registerWithoutId()(implicit request: DataRequest[AnyContent], hc: HeaderCarrier): Future[Either[ApiError, SafeId]] = businessRegistration()

  private val registrationError = Future.successful(Left(MandatoryInformationMissingError()))

  private def businessRegistration()(implicit request: DataRequest[AnyContent], hc: HeaderCarrier): Future[Either[ApiError, SafeId]] =
    (for {
      organisationName <- request.userAnswers.get(BusinessWithoutIDNamePage)
      phoneNumber  = request.userAnswers.get(ContactPhonePage)
      emailAddress = request.userAnswers.get(ContactEmailPage)
      address <- request.userAnswers.get(BusinessWithoutIdAddressPage)
    } yield sendBusinessRegistration(organisationName, address, ContactDetails(phoneNumber, None, None, emailAddress)))
      .getOrElse(registrationError)

  def sendBusinessRegistration(organisationName: String, address: Address, contactDetails: ContactDetails)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Either[ApiError, SafeId]] =
    registrationConnector
      .registerWithoutID(RegisterWithoutId.apply(organisationName, address, contactDetails))

}
