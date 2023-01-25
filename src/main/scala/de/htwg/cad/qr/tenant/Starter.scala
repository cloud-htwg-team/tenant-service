package de.htwg.cad.qr.tenant

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import de.htwg.cad.qr.tenant.db.TenantPersistenceHandler
import spray.json.DefaultJsonProtocol._

import java.util.Base64
import scala.concurrent.ExecutionContextExecutor
import scala.util.Try

object Starter extends App with JsonParser {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val persistence: TenantPersistenceHandler = TenantPersistenceHandler.cloud

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case t: Throwable =>
        t.printStackTrace()
        complete(HttpResponse(InternalServerError))
    }

  val route = Route.seal(
    concat(
      path("tenant" / "test") {
        get {
          complete("Working!")
        }
      },
      path("tenants") {
        get {
          complete(persistence.getAllTenants)
        }
      },
      pathPrefix("secure" / "tenants") {
        concat(
          pathEnd {
            post {
              entity(as[TenantCreationRequest]) { request =>
                complete(persistence.createTenant(request))
              }
            }
          },
          pathPrefix(Segment) { tenantId =>
            concat(
              get {
                complete(persistence.getTenantInformation(tenantId))
              },
              path("logo") {
                get {
                  complete(persistence.getLogo(tenantId))
                }
              }
            )
          }
        )
      }
    ))

  val bindingFuture = Http().newServerAt("0.0.0.0", 8888).bind(route)
}
