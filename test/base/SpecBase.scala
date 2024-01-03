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

package base

import controllers.actions._
import models.{UUIDGen, UUIDGenImpl, UniqueTaxpayerReference, UserAnswers}
import navigation.{CBCRNavigator, FakeCBCRNavigator}
import org.mockito.MockitoSugar
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues, TryValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier

import java.time.{Clock, Instant, ZoneId}

trait SpecBase
    extends AnyFreeSpec
    with GuiceOneAppPerSuite
    with Matchers
    with MockitoSugar
    with TryValues
    with OptionValues
    with ScalaFutures
    with BeforeAndAfterEach
    with IntegrationPatience {

  val userAnswersId: String      = "id"
  val utr: UniqueTaxpayerReference = UniqueTaxpayerReference("1234567890")
  implicit val hc: HeaderCarrier = HeaderCarrier()

  def onwardRoute: Call                                  = Call("GET", "/foo")
  final val mockDataRetrievalAction: DataRetrievalAction = mock[DataRetrievalAction]
  final val mockSessionRepository: SessionRepository     = mock[SessionRepository]
  final val mockCtUtrRetrievalAction: CtUtrRetrievalAction = mock[CtUtrRetrievalAction]
  protected val cbcrFakeNavigator: CBCRNavigator         = new FakeCBCRNavigator(onwardRoute)

  protected def retrieveNoData(): Unit =
    when(mockDataRetrievalAction).thenReturn(new FakeDataRetrievalAction(None))

  protected def retrieveUserAnswersData(userAnswers: UserAnswers): Unit =
    when(mockDataRetrievalAction).thenReturn(new FakeDataRetrievalAction(Some(userAnswers)))

  def emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId, Json.obj(), Instant.now(fixedClock))

  implicit val uuidGenerator: UUIDGen = new UUIDGenImpl

  implicit val fixedClock: Clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))

  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[CheckForSubmissionAction].to[FakeCheckForSubmissionAction],
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[CBCRNavigator].toInstance(cbcrFakeNavigator),
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers))
      )

}
