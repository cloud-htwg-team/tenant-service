package de.htwg.cad.qr.tenant.db

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._

import scala.concurrent.{ExecutionContext, Future}

class MicroserviceCommunicationHandler(implicit system: ActorSystem[Nothing], executionContext: ExecutionContext) {
  private case class CreateTenantRequest(name: String, withResources: Boolean)
  private case class CreateTenantResponse(displayName: String, passwordSignInAllowed: Boolean, emailLinkSignInEnabled: Boolean, tenantId: String)

  private implicit val createTenantRequestFormat: RootJsonFormat[CreateTenantRequest] = jsonFormat2(CreateTenantRequest)
  private implicit val createTenantResponseFormat: RootJsonFormat[CreateTenantResponse] = jsonFormat4(CreateTenantResponse)

  def createTenant(name: String, premium: Boolean): Future[String] = {
    performRequest[CreateTenantResponse](
      Post(s"http://10.92.0.149/create-tenant", CreateTenantRequest(name, premium)))
      .map(_.tenantId)
  }

  private def performRequest[T](request: HttpRequest)(implicit um: Unmarshaller[HttpResponse, T]): Future[T] = {
    Http().singleRequest(request)
      .flatMap(Unmarshal(_).to[T])
  }
}
