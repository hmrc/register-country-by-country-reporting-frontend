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

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

case class RequestParameters(paramName: String, paramValue: String)

object RequestParameters {
  implicit val formats: OFormat[RequestParameters] = Json.format[RequestParameters]
}

case class RequestCommonForSubscription(
  regime: String,
  conversationID: Option[String] = None,
  receiptDate: String,
  acknowledgementReference: String,
  originatingSystem: String,
  requestParameters: Option[Seq[RequestParameters]]
)

object RequestCommonForSubscription {

  implicit val requestCommonForSubscriptionFormats: OFormat[RequestCommonForSubscription] =
    Json.format[RequestCommonForSubscription]

  private val mdtp = "MDTP"

  def createRequestCommonForSubscription(): RequestCommonForSubscription = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    //Generate a 32 chars UUID without hyphens
    val acknowledgementReference = UUID.randomUUID().toString.replace("-", "")

    RequestCommonForSubscription(
      regime = "CBC",
      receiptDate = ZonedDateTime.now().format(formatter),
      acknowledgementReference = acknowledgementReference,
      originatingSystem = mdtp,
      requestParameters = None
    )
  }

}
