package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class ContactPhoneFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("contactPhone.error.required")
        .verifying(maxLength(24, "contactPhone.error.length"))
    )
}
