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

package models.register.response.details

import play.api.libs.json.{Format, Json}

case class AddressResponse(
  addressLine1: String,
  addressLine2: Option[String],
  addressLine3: Option[String],
  addressLine4: Option[String],
  postalCode: Option[String],
  countryCode: String
) {

  val asList: List[String] =
    List(Option(addressLine1), addressLine2, addressLine3, addressLine4, postCodeFormatter(postalCode), Option(countryCode).filterNot(_ == "GB"))
      .filter(_.isDefined)
      .map(_.get)

  def postCodeFormatter(postcode: Option[String]): Option[String] =
    postcode match {
      case Some(postcode) =>
        val tail = postcode.substring(postcode.length - 3)
        val head = postcode.substring(0, postcode.length - 3)
        Some(s"$head $tail".toUpperCase)
      case _ => None
    }
}

object AddressResponse {

  implicit val format: Format[AddressResponse] = Json.format[AddressResponse]
}
