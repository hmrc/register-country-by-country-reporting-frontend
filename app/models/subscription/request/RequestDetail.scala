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

package models.subscription.request

import play.api.libs.json.{Json, OFormat}

case class RequestDetail(
  IDType: String,
  IDNumber: String,
  tradingName: Option[String],
  /** tradingName will be populated as Organisation Name, while creating the subscription, The name which was matched in RegisterwithID response */
  isGBUser: Boolean,
  primaryContact: ContactInformation,
  secondaryContact: Option[ContactInformation]
) {
  require(tradingName.map(_.nonEmpty) == Option(true), "TradingName is null or empty")
}

object RequestDetail {
  implicit def format: OFormat[RequestDetail] = Json.format[RequestDetail]
}
