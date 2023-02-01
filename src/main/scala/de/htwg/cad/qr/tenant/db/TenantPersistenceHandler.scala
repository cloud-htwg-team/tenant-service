package de.htwg.cad.qr.tenant.db

import akka.actor.typed.ActorSystem
import de.htwg.cad.qr.tenant.{TenantCreationRequest, TenantInformationFull, TenantInformationShort}
import spray.json.RootJsonFormat

import scala.concurrent.{ExecutionContext, Future}

trait TenantPersistenceHandler {
  def getAllTenants(implicit conversion: RootJsonFormat[List[TenantInformationShort]]): Future[List[TenantInformationShort]]
  def createTenant(request: TenantCreationRequest)(implicit conversion: RootJsonFormat[TenantCreationRequest]): Future[String]
  def getTenantInformation(tenantId: String)(implicit conversion: RootJsonFormat[TenantInformationFull]): Future[TenantInformationFull]
  def getTenantByName(name: String)(implicit conversion: RootJsonFormat[TenantInformationFull]): Future[TenantInformationFull]
  def getLogo(tenantId: String): Future[String]
}

object TenantPersistenceHandler {
  def cloud(implicit system: ActorSystem[Nothing], executionContext: ExecutionContext): TenantPersistenceHandler = new CloudPersistenceHandler
}
