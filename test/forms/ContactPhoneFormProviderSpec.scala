package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ContactPhoneFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "contactPhone.error.required"
  val lengthKey = "contactPhone.error.length"
  val maxLength = 24

  val form = new ContactPhoneFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
