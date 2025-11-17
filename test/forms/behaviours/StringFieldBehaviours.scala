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

package forms.behaviours

import org.scalacheck.Gen
import play.api.data.{Form, FormError}
import utils.RegexConstants

trait StringFieldBehaviours extends FieldBehaviours with RegexConstants {

  def fieldWithMaxLength(form: Form[_], fieldName: String, maxLength: Int, lengthError: FormError): Unit =
    s"not bind strings longer than $maxLength characters" in {

      forAll(stringsLongerThan(maxLength) -> "longString") { string =>
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors must contain only lengthError
      }
    }

  def fieldWithPostCodeRequired(form: Form[_], fieldName: String, countryCodeList: Seq[String], invalidError: FormError): Unit =
    s"must not bind when postcode is required for a country" in {
      forAll(Gen.oneOf(countryCodeList)) { country =>
        val result = form.bind(Map("country" -> country)).apply(fieldName)
        result.errors.head mustEqual invalidError
      }
    }

  def fieldWithMaxLengthAlpha(form: Form[_], fieldName: String, maxLength: Int, lengthError: FormError): Unit =
    s"must not bind strings longer than $maxLength characters" in {

      forAll(stringsLongerThanAlpha(maxLength) -> "longString") { string =>
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithNonEmptyWhitespace(form: Form[_], fieldName: String, requiredError: FormError): Unit =
    s"must not bind strings of only whitespace" in {

      val result = form.bind(Map(fieldName -> " ")).apply(fieldName)
      result.errors mustEqual Seq(requiredError)
    }

  def stringsNotOfFixedLengthsNumeric(validLengths: Set[Int]): Gen[String] =
    Gen
      .choose(1, 50)
      .suchThat(len => !validLengths.contains(len))
      .flatMap(len => Gen.listOfN(len, Gen.numChar).map(_.mkString))

  def fieldWithFixedLengthsNumeric(
    form: Form[_],
    fieldName: String,
    acceptedLengths: Set[Int],
    lengthError: FormError
  ): Unit =
    s"must not bind strings that are not ${acceptedLengths.mkString(" or ")} characters long" in {

      forAll(stringsNotOfFixedLengthsNumeric(acceptedLengths) -> "invalidString") { string =>
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithMaxLengthEmail(form: Form[_], fieldName: String, maxLength: Int, lengthError: FormError): Unit =
    s"must not bind strings longer than $maxLength characters" in {

      forAll(validEmailAddressToLong(maxLength) -> "longString") { string =>
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithMaxLengthPhoneNumber(form: Form[_], fieldName: String, maxLength: Int, lengthError: FormError): Unit =
    s"must not bind strings longer than $maxLength characters" in {

      forAll(validPhoneNumberTooLong(maxLength) -> "longString") { string =>
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors mustEqual Seq(lengthError)
      }
    }

}
