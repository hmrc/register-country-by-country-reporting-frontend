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
import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.BusinessType.*
import org.scalatest.prop.TableDrivenPropertyChecks
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import wolfendale.scalacheck.regexp.RegexpGen

class BusinessNameFormProviderSpec extends SpecBase with StringFieldBehaviours with TableDrivenPropertyChecks {
  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  private val maxLength           = 105

  val form = new BusinessNameFormProvider()(LimitedCompany)
  val businessTypeWithMessages =
    Table(
      ("businessType", "requiredMsg", "lengthMsg", "invalidMsg"),
      (
        LimitedCompany,
        "Enter the registered name of your business",
        "Registered name of your business must be 105 characters or less",
        "Business name must only include letters a to z, numbers 0 to 9 and special characters such as hyphens, spaces and apostrophes"
      ),
      (
        Partnership, // check these
        "Enter the partnership name",
        "Partnership name must be 105 characters or less",
        "Partnership name must only include letters a to z, numbers 0 to 9 and special characters such as hyphens, spaces and apostrophes"
      ),
      (
        LimitedPartnership,
        "Enter the registered name of your business",
        "Registered name of your business must be 105 characters or less",
        "Business name must only include letters a to z, numbers 0 to 9 and special characters such as hyphens, spaces and apostrophes"
      ),
      (
        UnincorporatedAssociation,
        "Enter the name of your organisation",
        "Organisation name must be 105 characters of less",
        "Organisation name must only include letters a to z, numbers 0 to 9 and special characters such as hyphens, spaces and apostrophes"
      )
    )

  forAll(businessTypeWithMessages) { (businessType, requiredMsg, lengthMsg, invalidMsg) =>
    s".value for $businessType" - {

      val requiredKey = s"businessName.error.required.$businessType"
      val lengthKey   = s"businessName.error.length.$businessType"
      val invalidKey  = s"businessName.error.invalid.$businessType"

      val form      = new BusinessNameFormProvider()(businessType)
      val fieldName = "value"

      "normalises curly apostrophes to straight ones" in {
        val result = form.bind(Map("value" -> "“‘apostrophes’”"))
        result.errors mustBe empty
        result.value.value mustBe "\"'apostrophes'\""
      }

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

      "have correct error message content" in {

        messages(requiredKey) mustBe requiredMsg
        messages(lengthKey) mustBe lengthMsg
        messages(invalidKey) mustBe invalidMsg
      }
    }
  }

}
