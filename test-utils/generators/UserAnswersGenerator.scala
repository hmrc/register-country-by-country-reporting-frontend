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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(IsRegisteredAddressInUkPage.type, JsValue)] ::
      arbitrary[(IsThisYourBusinessPage.type, JsValue)] ::
      arbitrary[(DoYouHaveSecondContactPage.type, JsValue)] ::
      arbitrary[(SecondContactPhonePage.type, JsValue)] ::
      arbitrary[(SecondContactHavePhonePage.type, JsValue)] ::
      arbitrary[(HaveTelephonePage.type, JsValue)] ::
      arbitrary[(SecondContactNamePage.type, JsValue)] ::
      arbitrary[(SecondContactEmailPage.type, JsValue)] ::
      arbitrary[(ContactEmailPage.type, JsValue)] ::
      arbitrary[(ContactNamePage.type, JsValue)] ::
      arbitrary[(ContactPhonePage.type, JsValue)] ::
      arbitrary[(BusinessWithoutIdAddressPage.type, JsValue)] ::
      arbitrary[(WhatIsTradingNamePage.type, JsValue)] ::
      arbitrary[(UTRPage.type, JsValue)] ::
      arbitrary[(BusinessHaveDifferentNamePage.type, JsValue)] ::
      arbitrary[(BusinessWithoutIDNamePage.type, JsValue)] ::
      arbitrary[(BusinessNamePage.type, JsValue)] ::
      arbitrary[(BusinessTypePage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id <- nonEmptyString
        data <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
      } yield UserAnswers(
        id = id,
        data = data.foldLeft(Json.obj()) {
          case (obj, (path, value)) =>
            obj.setObject(path.path, value).get
        }
      )
    }
  }
}
