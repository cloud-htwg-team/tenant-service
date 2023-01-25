package de.htwg.cad.qr.tenant.db

import de.htwg.cad.qr.tenant.{TenantCreationRequest, TenantInformationFull, TenantInformationShort}

import java.util.{Base64, UUID}
import scala.concurrent.{ExecutionContext, Future}

private class CloudPersistenceHandler(implicit executionContext: ExecutionContext) extends TenantPersistenceHandler {
  override def getAllTenants: List[TenantInformationShort] =
    DatastoreHandler.listTenants()

  override def createTenant(request: TenantCreationRequest): Future[String] = {
    // TODO store new tenant in Google Identity Platform (maybe get ID from there?)
    Future(DatastoreHandler.saveTenant(request))
      .flatMap(id => Future(GoogleBucketHandler.uploadObject(id, Base64.getDecoder.decode(request.logo)))
        .map(_ => id))
  }

  override def getTenantInformation(tenantId: String): Future[TenantInformationFull] = {
    val information = Future(DatastoreHandler.getEntry(tenantId))
    getLogo(tenantId)
      .flatMap(logo => information.map(info => info.withLogo(logo)))
  }

  override def getLogo(tenantId: String): Future[String] =
    Future(GoogleBucketHandler.getObject(tenantId))
      .map(Base64.getEncoder.encodeToString)
}
