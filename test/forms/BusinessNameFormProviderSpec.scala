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
import models.BusinessType.LimitedCompany
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class BusinessNameFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = s"businessName.error.required.$LimitedCompany"
  val lengthKey   = s"businessName.error.length.$LimitedCompany"
  val invalidKey  = s"businessName.error.invalid.$LimitedCompany"
  val maxLength   = 105

  val form = new BusinessNameFormProvider()(LimitedCompany)

  ".value" - {

    "normalises curly apostrophes to straight ones" in {
      val result = form.bind(Map("value" -> "“‘apostrophes’”"))
      result.errors mustBe empty
      result.value.value mustBe "\"'apostrophes'\""
    }

    val fieldName = "value"

    behave like fieldThatBindsValidDataWithoutInvalidError(
      form,
      fieldName,
      RegexpGen.from(businessNameRegex),
      invalidKey
    )

    behave like fieldWithInvalidData(
      form,
      fieldName,
      "some emoji 🚀",
      FormError(fieldName, invalidKey)
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
  }
}
