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

package controllers.actions

import models.requests.{DataRequest, IdentifierRequest}
import play.api.libs.json.Reads
import play.api.mvc.{ActionBuilder, AnyContent}
import queries.Gettable

import javax.inject.Inject

class StandardActionSets @Inject() (identify: IdentifierAction,
                                    getData: DataRetrievalAction,
                                    requireData: DataRequiredAction,
                                    initializeData: DataInitializeAction,
                                    retrieveCtUTR: CtUtrRetrievalAction,
                                    checkEnrolment: CheckEnrolledToServiceActionProvider,
                                    dependantAnswer: DependantAnswerProvider
) {

  def identifiedUserWithEnrolmentCheckAndCtUtrRetrieval(): ActionBuilder[IdentifierRequest, AnyContent] =
    identify andThen checkEnrolment() andThen retrieveCtUTR()

  def identifiedUserWithEnrolmentCheck(): ActionBuilder[IdentifierRequest, AnyContent] =
    identify andThen checkEnrolment()

  def identifiedUserWithInitializedData(): ActionBuilder[DataRequest, AnyContent] =
    identifiedUserWithEnrolmentCheck() andThen getData andThen initializeData

  def identifiedWithoutEnrolmentCheck(): ActionBuilder[DataRequest, AnyContent] =
    identify andThen getData andThen requireData

  def identifiedUserWithData(): ActionBuilder[DataRequest, AnyContent] =
    identifiedUserWithEnrolmentCheck() andThen getData andThen requireData

  def identifiedUserWithDependantAnswer[T](answer: Gettable[T])(implicit reads: Reads[T]): ActionBuilder[DataRequest, AnyContent] =
    identifiedUserWithData() andThen dependantAnswer(answer)
}
