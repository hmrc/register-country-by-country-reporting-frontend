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
import models.Country
import play.api.data.FormError
import utils.RegexConstants
import wolfendale.scalacheck.regexp.RegexpGen

class BusinessWithoutIdAddressFormProviderSpec extends StringFieldBehaviours with RegexConstants {

  val countries = Seq(Country("valid", "AD", "Andorra"))
  val form      = new BusinessWithoutIdAddressFormProvider()(countries)

  val addressLineMaxLength = 35

  ".addressLine1" - {

    val fieldName   = "addressLine1"
    val requiredKey = "businessWithoutIdAddress.error.addressLine1.required"
    val invalidKey  = "businessWithoutIdAddress.error.addressLine1.invalid"
    val lengthKey   = "businessWithoutIdAddress.error.addressLine1.length"

    behave like fieldThatBindsValidDataWithoutInvalidError(
      form,
      fieldName,
      RegexpGen.from(apiAddressRegex),
      invalidKey
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like fieldWithNonEmptyWhitespace(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "jjdjdj£%^&kfkf",
      FormError(fieldName, invalidKey)
    )
  }

  ".addressLine2" - {

    val fieldName  = "addressLine2"
    val invalidKey = "businessWithoutIdAddress.error.addressLine2.invalid"
    val lengthKey  = "businessWithoutIdAddress.error.addressLine2.length"

    behave like fieldThatBindsValidDataWithoutInvalidError(
      form,
      fieldName,
      RegexpGen.from(apiAddressRegex),
      invalidKey
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "jjdjdj£%^&kfkf",
      FormError(fieldName, invalidKey)
    )
  }

  ".addressLine3" - {

    val fieldName   = "addressLine3"
    val requiredKey = "businessWithoutIdAddress.error.addressLine3.required"
    val invalidKey  = "businessWithoutIdAddress.error.addressLine3.invalid"
    val lengthKey   = "businessWithoutIdAddress.error.addressLine3.length"

    behave like fieldThatBindsValidDataWithoutInvalidError(
      form,
      fieldName,
      RegexpGen.from(apiAddressRegex),
      invalidKey
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like fieldWithNonEmptyWhitespace(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "jjdjdj£%^&kfkf",
      FormError(fieldName, invalidKey)
    )
  }

  ".addressLine4" - {

    val fieldName  = "addressLine4"
    val invalidKey = "businessWithoutIdAddress.error.addressLine4.invalid"
    val lengthKey  = "businessWithoutIdAddress.error.addressLine4.length"

    behave like fieldThatBindsValidDataWithoutInvalidError(
      form,
      fieldName,
      RegexpGen.from(apiAddressRegex),
      invalidKey
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = addressLineMaxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "jjdjdj£%^&kfkf",
      FormError(fieldName, invalidKey)
    )
  }

  ".postCode" - {

    val fieldName         = "postCode"
    val requiredKey       = "businessWithoutIdAddress.error.postcode.required"
    val lengthKey         = "businessWithoutIdAddress.error.postcode.length"
    val postCodeMaxLength = 10

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validPostCodes
    )

    behave like fieldWithMaxLengthAlpha(
      form,
      fieldName,
      maxLength = postCodeMaxLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    behave like fieldWithPostCodeRequired(
      form,
      fieldName,
      Seq("JE", "GG", "IM"),
      invalidError = FormError(fieldName, Seq(requiredKey), Seq())
    )
  }
}
