package controllers

import controllers.actions._
import forms.ContactPhoneFormProvider

import javax.inject.Inject
import models.Mode
import navigation.{CBCRNavigator, Navigator}
import pages.ContactPhonePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ContactPhoneView

import scala.concurrent.{ExecutionContext, Future}

class ContactPhoneController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        navigator: CBCRNavigator,
                                        standardActionSets: StandardActionSets,
                                        formProvider: ContactPhoneFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: ContactPhoneView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData() {
    implicit request =>

      val preparedForm = request.userAnswers.get(ContactPhonePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.identifiedUserWithData().async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ContactPhonePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ContactPhonePage, mode, updatedAnswers))
      )
  }
}
