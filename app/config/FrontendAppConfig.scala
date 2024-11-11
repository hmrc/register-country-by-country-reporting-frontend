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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.hmrcfrontend.config.ContactFrontendConfig
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, contactFrontendConfig: ContactFrontendConfig) {

  val host: String    = configuration.get[String]("host")
  val appName: String = configuration.get[String]("appName")
  val feedbackLink    = s"${contactFrontendConfig.baseUrl.get}/contact/beta-feedback?service=${contactFrontendConfig.serviceId.get}"

  def feedbackUrl(implicit request: RequestHeader): String = request.uri match {
    case uri if uri.contains("problem") => s"$feedbackLink"
    case _                              => s"$feedbackLink + &backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"
  }

  val loginUrl: String                         = configuration.get[String]("urls.login")
  val loginContinueUrl: String                 = configuration.get[String]("urls.loginContinue")
  val signOutUrl: String                       = configuration.get[String]("urls.signOut")
  lazy val lostUTRUrl: String                  = configuration.get[String]("urls.lostUTR")
  lazy val corporationTaxEnquiriesLink: String = configuration.get[String]("urls.corporationTaxEnquiries")
  lazy val selfAssessmentEnquiriesLink: String = configuration.get[String]("urls.selfAssessmentEnquiries")
  lazy val businessTaxAccountLink: String      = configuration.get[String]("urls.btaLogin")
  lazy val emailEnquiries: String              = configuration.get[String]("urls.emailEnquiries")
  lazy val findCompanyName: String             = configuration.get[String]("urls.findCompanyName")

  lazy val registerCountryByCountryUrl: String =
    s"${configuration.get[Service]("microservice.services.register-country-by-country").baseUrl}${configuration.get[String]("microservice.services.register-country-by-country.startUrl")}"

  lazy val enrolmentStoreProxyUrl: String =
    s"${configuration.get[Service]("microservice.services.enrolment-store-proxy").baseUrl}${configuration.get[String]("microservice.services.enrolment-store-proxy.startUrl")}"

  val taxEnrolmentsUrl1: String = s"${configuration.get[Service]("microservice.services.tax-enrolments").baseUrl}${configuration
    .get[String]("microservice.services.tax-enrolments.url1")}"
  val taxEnrolmentsUrl2: String = s"${configuration.get[String]("microservice.services.tax-enrolments.url2")}"

  private val exitSurveyBaseUrl: String = configuration.get[Service]("microservice.services.feedback-frontend").baseUrl
  val exitSurveyUrl: String             = s"$exitSurveyBaseUrl/feedback/register-for-country-by-country-reporting"

  val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en")
  )

  lazy val countryCodeJson: String = configuration.get[String]("json.countries")

  val timeout: Int   = configuration.get[Int]("timeout-dialog.timeout")
  val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  val cacheTtl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  val enrolmentKey: String      = configuration.get[String](s"keys.enrolmentKey.cbcr")
  val nonUkEnrolmentKey: String = configuration.get[String](s"keys.enrolmentKey.cbcrNonUk")
  val ctEnrolmentKey: String    = configuration.get[String]("keys.enrolmentKey.ct")

  lazy val countryByCountryReportingFrontendUrl: String = configuration.get[String]("urls.country-by-country-reporting-frontend")

  lazy val sendEmailUrl: String              = configuration.get[Service]("microservice.services.email").baseUrl
  lazy val emailOrganisationTemplate: String = configuration.get[String]("emailtemplates.organisation")

  lazy val encryptionEnabled: Boolean = configuration.get[Boolean]("mongodb.encryptionEnabled")
}
