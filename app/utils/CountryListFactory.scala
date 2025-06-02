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

package utils

import config.FrontendAppConfig
import models.Country
import play.api.Environment
import play.api.libs.json.Json
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

import javax.inject.{Inject, Singleton}

@Singleton
class CountryListFactory @Inject() (environment: Environment, appConfig: FrontendAppConfig) {

  lazy val countryList: Option[Seq[Country]] = getCountryList

  private def getCountryList: Option[Seq[Country]] =
    environment.resourceAsStream(appConfig.countryCodeJson) map Json.parse map {
      _.as[Seq[Country]]
        .map(
          country => if (country.alternativeName.isEmpty) country.copy(alternativeName = Option(country.description)) else country
        )
        .sortWith(
          (country, country2) => country.description.toLowerCase < country2.description.toLowerCase
        )
    }

  def getDescriptionFromCode(code: String): Option[String] =
    countryList.flatMap(_.find(_.code == code).map(_.description))

  lazy val countryListWithoutGB: Option[Seq[Country]] = countryList.map {
    _.filter(
      x => x.code != "GB"
    )
  }

  def countrySelectList(value: Map[String, String], countries: Seq[Country]): Seq[SelectItem] = {
    val countryJsonList = countries
      .groupBy(_.code)
      .map {
        case (_, countries) =>
          val country = countries.head
          val names = countries
            .flatMap(
              c => List(Some(c.description), c.alternativeName)
            )
            .flatten
            .distinct
          val isSelected = value.get("country").contains(country.code)
          SelectItem(
            Some(country.code),
            country.description,
            isSelected,
            attributes = Map("data-text" -> (if (isSelected) country.description else names.mkString(":")))
          )
      }
      .toSeq
      .sortBy(_.text)
    SelectItem(Some(""), "Select a country", selected = false) +: countryJsonList
  }
}
