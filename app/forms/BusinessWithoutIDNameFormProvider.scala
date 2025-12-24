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

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import utils.RegexConstants

import javax.inject.Inject

class BusinessWithoutIDNameFormProvider @Inject() extends Mappings with RegexConstants {

  private val maxLength = 105

  def apply(): Form[String] =
    Form(
      mapping(
        "value" -> text("businessWithoutIDName.error.required").verifying(businessNameWithoutIdConstraint)
      )(identity)(Some(_))
    )

  private def businessNameWithoutIdConstraint: Constraint[String] =
    Constraint("constraint.businessName") { value =>
      if (value.length > maxLength) {
        Invalid(ValidationError("businessWithoutIDName.error.length"))
      } else if (!value.matches(businessNameRegex)) {
        Invalid(ValidationError("businessWithoutIDName.error.invalid"))
      } else {
        Valid
      }
    }

}
