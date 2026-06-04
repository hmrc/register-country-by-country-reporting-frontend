/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers

import base.SpecBase
import config.FrontendAppConfig
import play.api.i18n.Lang

class LanguageSwitchControllerSpec extends SpecBase {

  "LanguageSwitchController" - {

    "fallbackURL" - {
      "should return the IndexController.onPageLoad() URL" in {
        val application = applicationBuilder(None).build()
        val controller  = application.injector.instanceOf[LanguageSwitchController]

        val result = controller.fallbackURL

        result mustEqual routes.IndexController.onPageLoad().url
      }
    }

    "languageMap" - {
      "should return the language map from FrontendAppConfig" in {
        val application = applicationBuilder(None).build()
        val controller  = application.injector.instanceOf[LanguageSwitchController]
        val appConfig   = application.injector.instanceOf[FrontendAppConfig]

        val result = controller.languageMap

        result mustEqual appConfig.languageMap
      }

      "should contain English language mapping" in {
        val application = applicationBuilder(None).build()
        val controller  = application.injector.instanceOf[LanguageSwitchController]

        val result = controller.languageMap

        result must contain("en" -> Lang("en"))
      }

      "should have 'en' as a key" in {
        val application = applicationBuilder(None).build()
        val controller  = application.injector.instanceOf[LanguageSwitchController]

        val result = controller.languageMap

        result.keySet must contain("en")
      }
    }
  }
}
