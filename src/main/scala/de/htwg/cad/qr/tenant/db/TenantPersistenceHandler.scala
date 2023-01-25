package de.htwg.cad.qr.tenant.db

import de.htwg.cad.qr.tenant.{TenantCreationRequest, TenantInformationFull, TenantInformationShort}

import scala.concurrent.{ExecutionContext, Future}

trait TenantPersistenceHandler {
  def getAllTenants: List[TenantInformationShort]
  def createTenant(request: TenantCreationRequest): Future[String]
  def getTenantInformation(tenantId: String): Future[TenantInformationFull]
  def getLogo(tenantId: String): Future[String]
}

object TenantPersistenceHandler {
  def cloud(implicit executionContext: ExecutionContext): TenantPersistenceHandler = new CloudPersistenceHandler
}
