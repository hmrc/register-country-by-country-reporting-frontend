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

package forms

import forms.behaviours.StringFieldBehaviours
import models.UniqueTaxpayerReference
import play.api.data.{Form, FormError}

class UTRFormProviderSpec extends StringFieldBehaviours {

  val requiredKey               = "utr.error.required"
  val invalidKey                = "utr.error.invalid"
  val charKey                   = "utr.error.char"
  val acceptedLengths: Set[Int] = Set(10, 13)

  val form: Form[UniqueTaxpayerReference] = new UTRFormProvider().apply("Self Assessment")

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validUtr
    )

    behave like fieldWithFixedLengthsNumeric(
      form,
      fieldName,
      acceptedLengths,
      lengthError = FormError(fieldName, invalidKey, Seq("Self Assessment"))
    )

    behave like fieldWithNonEmptyWhitespace(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq("Self Assessment"))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq("Self Assessment"))
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "jjdjdj£%^&kfkf",
      FormError(fieldName, charKey, Seq("Self Assessment"))
    )
  }
}
