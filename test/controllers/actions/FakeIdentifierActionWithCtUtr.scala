package controllers.actions

import models.requests.IdentifierRequest
import play.api.mvc.*
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FakeIdentifierActionWithCtUtr @Inject() (bodyParsers: PlayBodyParsers) extends IdentifierAction {
  private val ctEnrolment = Enrolment(
    key = "IR-CT",
    identifiers = Seq(EnrolmentIdentifier("UTR", "1234567890")),
    state = "Activated"
  )

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] =
    block(IdentifierRequest(request, "id", enrolments = Set(ctEnrolment)))

  override def parser: BodyParser[AnyContent] = bodyParsers.default
  override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}
