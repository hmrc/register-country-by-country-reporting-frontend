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
import controllers.routes
import models.{ApiError, InternalServerError, Mode, NormalMode, UUIDGen, UniqueTaxpayerReference, UserAnswers}
import models.IdentifierType.UTR
import models.matching.{AutoMatchedRegistrationRequest, RegistrationInfo, RegistrationRequest}
import models.register.request.RegisterWithID
import models.requests.DataRequest
import pages.{AutoMatchedUTRPage, BusinessNamePage, BusinessTypePage, IsThisYourBusinessPage, RegistrationInfoPage, UTRPage}
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Result}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Clock
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class BusinessMatchingWithIdService @Inject() (registrationConnector: RegistrationConnector,
                                               sessionRepository: SessionRepository,
                                               uuidGen: UUIDGen,
                                               clock: Clock
)(implicit
  ec: ExecutionContext
) extends Logging {
  implicit private val uuidGenerator: UUIDGen = uuidGen
  implicit private val implicitClock: Clock   = clock
  
  def sendBusinessRegistrationInformation(autoMatchedUtr: Option[UniqueTaxpayerReference] , userAnswers: UserAnswers)(implicit hc: HeaderCarrier)
  : Future[Either[ApiError, RegistrationInfo]] =
        registrationConnector
          .registerWithID(buildRegisterWithId(autoMatchedUtr, userAnswers))
          .map { response =>
            response.map(x => RegistrationInfo.apply(x))
          }
      
    


  def buildRegisterWithId(autoMatchedUtr:Option[UniqueTaxpayerReference], userAnswers: UserAnswers): RegisterWithID = {
    autoMatchedUtr.fold(buildRegistrationRequest(userAnswers)) {
      utr => RegisterWithID(AutoMatchedRegistrationRequest(UTR, utr.uniqueTaxPayerReference))
    }
  }


  private def buildRegistrationRequest(userAnswers: UserAnswers): Option[RegisterWithID] =
    for {
      utr          <-userAnswers.get(UTRPage)
      businessName <- userAnswers.get(BusinessNamePage)
      businessType = userAnswers.get(BusinessTypePage)
    } yield RegisterWithID(RegistrationRequest(UTR, utr.uniqueTaxPayerReference, businessName, businessType, None))

    private def handleRegistrationFound(
      mode: Mode,
      autoMatchedUtr: Option[UniqueTaxpayerReference],
      registrationInfo: RegistrationInfo
    )(implicit request: DataRequest[AnyContent]): Future[Result] = {
      val updatedAnswersWithUtrPage = autoMatchedUtr.map(request.userAnswers.set(UTRPage, _)).getOrElse(Success(request.userAnswers))
      for {
        updatedAnswers <- Future.fromTry(updatedAnswersWithUtrPage.flatMap(_.set(RegistrationInfoPage, registrationInfo)))
        _              <- sessionRepository.set(updatedAnswers)
      } yield {
        val preparedForm = request.userAnswers.get(IsThisYourBusinessPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Ok(view(preparedForm, registrationInfo, mode))
      }
    }

    private def handleRegistrationNotFound(
      mode: Mode,
      autoMatchedUtr: Option[UniqueTaxpayerReference]
    )(implicit request: DataRequest[AnyContent]): Future[Result] =
      if (autoMatchedUtr.nonEmpty) {
        resultWithAutoMatchedFieldCleared(mode)
      } else {
        Future.successful(Redirect(routes.BusinessNotIdentifiedController.onPageLoad()))
      }

    def resultWithAutoMatchedFieldCleared(userAnswers: UserAnswers): Future[Boolean] =
      for {
        autoMatchedUtrRemoved <- Future.fromTry(userAnswers.remove(AutoMatchedUTRPage))
        result <- sessionRepository.set(autoMatchedUtrRemoved)
      } yield result
//
//    private def selfHealIfNecessary(value: Boolean, mode: Mode)(implicit ec: ExecutionContext, request: DataRequest[AnyContent]): Future[Result] =
//      if (value) {
//        request.userAnswers.get(RegistrationInfoPage) match {
//          case Some(registrationInfo) =>
//            subscriptionService.getDisplaySubscriptionId(registrationInfo.safeId) flatMap {
//              case Some(subscriptionId) =>
//                updateSubscriptionIdAndCreateEnrolment(registrationInfo.safeId, subscriptionId)
//              case _ =>
//                gotoNextPage(value, request, mode)
//            }
//          case None =>
//            logger.error(s"Registration info not found in user answers when user answered yes to 'is this your business question'")
//            Future.successful(Redirect(routes.ThereIsAProblemController.onPageLoad()))
//        }
//      } else {
//        gotoNextPage(value, request, mode)
//      }
//
//    private def gotoNextPage(value: Boolean, request: DataRequest[AnyContent], mode: Mode): Future[Result] =
//      for {
//        updatedAnswers <- Future.fromTry(request.userAnswers.set(IsThisYourBusinessPage, value))
//        _              <- sessionRepository.set(updatedAnswers)
//      } yield Redirect(navigator.nextPage(IsThisYourBusinessPage, mode, updatedAnswers))

}
