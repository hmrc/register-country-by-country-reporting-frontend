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

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import utils.RegexConstants
import wolfendale.scalacheck.regexp.RegexpGen

class WhatIsTradingNameFormProviderSpec extends StringFieldBehaviours with RegexConstants{

  val form = new WhatIsTradingNameFormProvider()()

  ".businessName" - {

    val fieldName   = "value"
    val requiredKey = "whatIsTradingName.error.required"
    val lengthKey   = "whatIsTradingName.error.length"
    val invalidKey  = "whatIsTradingName.error.invalid"
    val maxLength   = 80

    behave like fieldThatBindsValidDataWithoutInvalidError(
      form,
      fieldName,
      RegexpGen.from(apiOrganisationNameRegex),
      invalidKey
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "jfhf-\\^' `&%",
      FormError(fieldName, invalidKey)
    )
  }
}
