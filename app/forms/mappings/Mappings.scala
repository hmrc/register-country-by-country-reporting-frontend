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

package forms.mappings

import java.time.LocalDate
import play.api.data.{FieldMapping, FormError}
import play.api.data.Forms.of
import models.Enumerable
import play.api.data.format.Formatter

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String = "error.required", args: Seq[String] = Seq.empty): FieldMapping[String] =
    of(stringFormatter(errorKey, args))

  protected def int(requiredKey: String = "error.required",
                    wholeNumberKey: String = "error.wholeNumber",
                    nonNumericKey: String = "error.nonNumeric",
                    args: Seq[String] = Seq.empty): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def boolean(requiredKey: String = "error.required",
                        invalidKey: String = "error.boolean",
                        args: Seq[String] = Seq.empty): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey, args))


  protected def enumerable[A](requiredKey: String = "error.required",
                              invalidKey: String = "error.invalid",
                              args: Seq[String] = Seq.empty)(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey, args))

  protected def localDate(
                           invalidKey: String,
                           allRequiredKey: String,
                           twoRequiredKey: String,
                           requiredKey: String,
                           args: Seq[String] = Seq.empty): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, twoRequiredKey, requiredKey, args))

  protected def validatedTextMaxLength(requiredKey: String, lengthKey: String, maxLength: Int): FieldMapping[String] =
    of(textMaxLengthFormatter(requiredKey, lengthKey, maxLength))

  protected def validatedText(requiredKey: String,
                              invalidKey: String,
                              lengthKey: String,
                              regex: String,
                              maxLength: Int,
                              msgArg: String = ""
                             ): FieldMapping[String] =
    of(validatedTextFormatter(requiredKey, invalidKey, lengthKey, regex, maxLength, msgArg))

  protected def validatedTextFormatter(requiredKey: String,
                                       invalidKey: String,
                                       lengthKey: String,
                                       regex: String,
                                       maxLength: Int,
                                       msgArg: String = ""
                                      ): Formatter[String] =
    new Formatter[String] {
      private val dataFormatter: Formatter[String] = stringTrimFormatter(requiredKey, msgArg)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
        dataFormatter
          .bind(key, data)
          .right
          .flatMap {
            case str if !str.matches(regex)    => Left(Seq(FormError(key, invalidKey)))
            case str if str.length > maxLength => Left(Seq(FormError(key, lengthKey)))
            case str                           => Right(str)
          }

      override def unbind(key: String, value: String): Map[String, String] =
        Map(key -> value)
    }
}
