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

import org.scalatest.Suite

trait AuthStubs { this: Suite =>

  val authUrl            = "/auth/authorise"
  val ggSignInUrl        = "/auth-login-stub/gg-sign-in.*"
  val testAuthInternalId = "internalId"

  val authRequest =
    s"""{
       |  "authorise" : [ ],
       |  "retrieve" : [ "internalId", "allEnrolments", "affinityGroup", "credentialRole" ]
       |}""".stripMargin

  def authOKResponse(cbcId: String, affinity: String = "Organisation") =
    s"""|  {
        |    "internalId": "$testAuthInternalId",
        |    "affinityGroup": "$affinity",
        |    "allEnrolments" : [ {
        |      "key" : "HMRC-CBC-ORG",
        |      "identifiers" : [ {
        |        "key" : "cbcId",
        |        "value" : "$cbcId"
        |      } ],
        |      "state" : "Activated",
        |      "confidenceLevel" : 50
        |    } ]
        |  }
         """.stripMargin

  def authOKResponseWithoutEnrolment(affinity: String = "Organisation") =
    s"""|  {
        |    "internalId": "$testAuthInternalId",
        |    "affinityGroup": "$affinity",
        |    "allEnrolments" : []
        |  }
         """.stripMargin

  def ggSignInSuccess() = "gg-sign-in"

}
