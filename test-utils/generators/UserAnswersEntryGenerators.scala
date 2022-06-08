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

package generators

import models.{Address, BusinessType}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryDoYouHaveSecondContactUserAnswersEntry: Arbitrary[(DoYouHaveSecondContactPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoYouHaveSecondContactPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySecondContactPhoneUserAnswersEntry: Arbitrary[(SecondContactPhonePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SecondContactPhonePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySecondContactHavePhoneUserAnswersEntry: Arbitrary[(SecondContactHavePhonePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SecondContactHavePhonePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHaveTelephoneUserAnswersEntry: Arbitrary[(HaveTelephonePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HaveTelephonePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }
  
  implicit lazy val arbitrarySecondContactEmailUserAnswersEntry: Arbitrary[(SecondContactEmailPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SecondContactEmailPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySecondContactNameUserAnswersEntry: Arbitrary[(SecondContactNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SecondContactNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryContactEmailUserAnswersEntry: Arbitrary[(ContactEmailPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ContactEmailPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryContactNameUserAnswersEntry: Arbitrary[(ContactNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ContactNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryContactPhoneUserAnswersEntry: Arbitrary[(ContactPhonePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ContactPhonePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBusinessWithoutIdAddressUserAnswersEntry: Arbitrary[(BusinessWithoutIdAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessWithoutIdAddressPage.type]
        value <- arbitrary[Address].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTradingNameUserAnswersEntry: Arbitrary[(WhatIsTradingNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTradingNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryUTRUserAnswersEntry: Arbitrary[(UTRPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[UTRPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBusinessHaveDifferentNameUserAnswersEntry: Arbitrary[(BusinessHaveDifferentNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessHaveDifferentNamePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBusinessWithoutIDNameUserAnswersEntry: Arbitrary[(BusinessWithoutIDNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessWithoutIDNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBusinessNameUserAnswersEntry: Arbitrary[(BusinessNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBusinessTypeUserAnswersEntry: Arbitrary[(BusinessTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BusinessTypePage.type]
        value <- arbitrary[BusinessType].map(Json.toJson(_))
      } yield (page, value)
    }
}
