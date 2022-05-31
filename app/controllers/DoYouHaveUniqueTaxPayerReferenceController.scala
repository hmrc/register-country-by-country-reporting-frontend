/*
 * Copyright 2022 HM Revenue & Customs
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

import config.FrontendAppConfig
import controllers.actions.StandardActionSets
import forms.DoYouHaveUniqueTaxPayerReferenceFormProvider
import models.{Mode, NormalMode}
import navigation.CBCRNavigator
import pages.DoYouHaveUniqueTaxPayerReferencePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DoYouHaveUniqueTaxPayerReferenceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DoYouHaveUniqueTaxPayerReferenceController @Inject()(
                                                            override val messagesApi: MessagesApi,
                                                            appConfig: FrontendAppConfig,
                                                            sessionRepository: SessionRepository,
                                                            navigator: CBCRNavigator,
                                                            standardActionSets: StandardActionSets,
                                                            formProvider: DoYouHaveUniqueTaxPayerReferenceFormProvider,
                                                            val controllerComponents: MessagesControllerComponents,
                                                            view: DoYouHaveUniqueTaxPayerReferenceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad: Action[AnyContent] =
    standardActionSets.identifiedUserWithInitializedData() {
      implicit request =>
        val preparedForm = request.userAnswers.get(DoYouHaveUniqueTaxPayerReferencePage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm))
    }

  def onSubmit(): Action[AnyContent] = standardActionSets.identifiedUserWithInitializedData().async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(DoYouHaveUniqueTaxPayerReferencePage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(DoYouHaveUniqueTaxPayerReferencePage, NormalMode, updatedAnswers))
        )
  }
}
