/*
 * Copyright 2025 HM Revenue & Customs
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

package utils

import base.SpecBase
import config.FrontendAppConfig
import models.Country
import play.api.Environment
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class CountryListFactorySpec extends SpecBase {

  val mockEnvironment: Environment  = mock[Environment]
  val mockConfig: FrontendAppConfig = mock[FrontendAppConfig]

  val testCountriesJson =
    """
      |[
      |  {"code": "GB", "state":  "valid", "description": "United Kingdom"},
      |  {"code": "WF", "state":  "valid", "description": "Wallis and Futuna Islands"},
      |  {"code": "VE", "state":  "valid", "description": "Venezuela"},
      |  {"code": "VA", "state":  "valid", "description": "Vatican City"},
      |  {"code": "VC", "state":  "valid", "description": "St Vincent"},
      |  {"code": "VI", "state":  "valid", "description": "United States Virgin Islands"},
      |  {"code": "VN", "state":  "valid", "description": "Vietnam"},
      |  {"code": "VG", "state":  "valid", "description": "British Virgin Islands"},
      |  {"code": "UM", "state":  "valid", "description": "US Minor Outlying Islands"},
      |  {"code": "VU", "state":  "valid", "description": "Vanuatu"}
      |]
      |""".stripMargin

  val jsonStream = new ByteArrayInputStream(testCountriesJson.getBytes(StandardCharsets.UTF_8))

  when(mockConfig.countryCodeJson).thenReturn("path/to/json")
  when(mockEnvironment.resourceAsStream("path/to/json")).thenReturn(Some(jsonStream))

  "CountryListFactory" - {
    val factory = new CountryListFactory(mockEnvironment, mockConfig)

    "read and sort countries alphabetically" in {

      val result = factory.countryList.get

      result.map(_.description) mustBe List(
        "British Virgin Islands",
        "St Vincent",
        "United Kingdom",
        "United States Virgin Islands",
        "US Minor Outlying Islands",
        "Vanuatu",
        "Vatican City",
        "Venezuela",
        "Vietnam",
        "Wallis and Futuna Islands"
      )
    }

    "countryListWithoutGB must filter out GB" in {
      when(mockConfig.countryCodeJson).thenReturn("path/to/json")
      when(mockEnvironment.resourceAsStream("path/to/json")).thenReturn(Some(jsonStream))

      val result = factory.countryListWithoutGB.get

      result.exists(_.code == "GB") mustBe false
      result.map(_.code) must contain allElementsOf List("VG", "VC", "VI", "UM", "VU", "VA", "VE", "VN", "WF")
    }

    "getDescriptionFromCode must return country description for a given code" in {
      when(mockConfig.countryCodeJson).thenReturn("path/to/json")
      when(mockEnvironment.resourceAsStream("path/to/json")).thenReturn(Some(jsonStream))

      factory.getDescriptionFromCode("VE") mustBe Some("Venezuela")
      factory.getDescriptionFromCode("XX") mustBe None
    }

    "must build a SelectItem list correctly, with the right selection marked" in {
      val countries = Seq(
        Country("valid", "VG", "British Virgin Islands"),
        Country("valid", "VN", "Vietnam"),
        Country("valid", "VI", "United States Virgin Islands")
      )

      val selectedValue = Map("country" -> "VN")

      val result = factory.countrySelectList(selectedValue, countries)

      val expected = Seq(
        SelectItem(Some(""), "Select a country", false),
        SelectItem(Some("VG"), "British Virgin Islands", false, false, Map("data-text" -> "British Virgin Islands")),
        SelectItem(Some("VI"), "United States Virgin Islands", false, false, Map("data-text" -> "United States Virgin Islands")),
        SelectItem(Some("VN"), "Vietnam", true, false, Map("data-text" -> "Vietnam"))
      )

      result mustBe expected
    }

    "must return the correct selected country when there are alternative names" in {
      val countries = Seq(
        Country("valid", "AB", "Country_1"),
        Country("valid", "AB", "Country_1", Some("Country_1_2")),
        Country("valid", "BC", "Country_2", Some("Country_2"))
      )
      val selectedCountry = Map("country" -> "Country_1_2")

      factory.countrySelectList(selectedCountry, countries) must contain theSameElementsAs Seq(
        SelectItem(value = Some(""), text = "Select a country"),
        SelectItem(value = Some("AB"), text = "Country_1", selected = false, attributes = Map("data-text" -> "Country_1:Country_1_2")),
        SelectItem(value = Some("BC"), text = "Country_2", selected = false, attributes = Map("data-text" -> "Country_2"))
      )
    }

  }
}
