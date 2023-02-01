package de.htwg.cad.qr.tenant.db

import akka.actor.typed.ActorSystem
import de.htwg.cad.qr.tenant.{TenantCreationRequest, TenantInformationFull, TenantInformationShort}

import java.util.Base64
import scala.concurrent.{ExecutionContext, Future}

private class CloudPersistenceHandler(implicit system: ActorSystem[Nothing], executionContext: ExecutionContext) extends TenantPersistenceHandler {
  private val communication = new MicroserviceCommunicationHandler()

  override def getAllTenants: Future[List[TenantInformationShort]] =
    Future(DatastoreHandler.listTenants())

  override def createTenant(request: TenantCreationRequest): Future[String] = {
    communication.createTenant(request.name, request.premium)
      .flatMap(tenantId => {
        val metaData = Future(DatastoreHandler.saveTenant(request, tenantId))
        val logo = Future(GoogleBucketHandler.uploadObject(tenantId, Base64.getDecoder.decode(request.logo)))
        metaData.flatMap(_ => logo).map(_ => tenantId)
      })
  }

  override def getTenantInformation(tenantId: String): Future[TenantInformationFull] = {
    val information = Future(DatastoreHandler.getEntry(tenantId))
    getLogo(tenantId)
      .flatMap(logo => information.map(info => info.withLogo(logo)))
  }

  override def getTenantByName(name: String): Future[TenantInformationFull] = {
    Future(DatastoreHandler.getByName(name))
      .flatMap(info => getLogo(info.tenantId).map(info.withLogo))
  }

  override def getLogo(tenantId: String): Future[String] =
    Future(GoogleBucketHandler.getObject(tenantId))
      .map(Base64.getEncoder.encodeToString)
}
