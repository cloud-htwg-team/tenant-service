package de.htwg.cad.qr.tenant.db

import akka.actor.typed.ActorSystem
import de.htwg.cad.qr.tenant.{TenantCreationRequest, TenantInformationFull, TenantInformationShort}
import spray.json.{RootJsonFormat, enrichAny}

import java.util.Base64
import scala.concurrent.{ExecutionContext, Future}

private class CloudPersistenceHandler(implicit system: ActorSystem[Nothing], executionContext: ExecutionContext) extends TenantPersistenceHandler {
  private val communication = new MicroserviceCommunicationHandler()

  override def getAllTenants(implicit conversion: RootJsonFormat[List[TenantInformationShort]]): Future[List[TenantInformationShort]] = {
    println("Received GetAllTenantsRequest")
    Future({
      val response = DatastoreHandler.listTenants()
      println(s"Completed GetAllTenantsRequest: ${response.toJson.compactPrint}")
      response
    })
  }

  override def createTenant(request: TenantCreationRequest)(implicit conversion: RootJsonFormat[TenantCreationRequest]): Future[String] = {
    println(s"Received TenantCreationRequest: ${request.toJson.compactPrint}")
    communication.createTenant(request.name, request.premium)
      .flatMap(tenantId => {
        val metaData = Future(DatastoreHandler.saveTenant(request, tenantId))
        val logo = uploadBase64ToBucket(request.logo, tenantId)
        metaData.flatMap(_ => logo).map(_ => {
          println(s"Completed TenantCreationRequest: $tenantId")
          tenantId
        })
      })
  }

  private def uploadBase64ToBucket(toUpload: String, name: String): Future[Unit] = {
    val seperated = toUpload.split(",")
    val content = seperated(1)
    val fileType = seperated(0).dropWhile(_ != '/').drop(1).takeWhile(_ != ';')
    Future(GoogleBucketHandler.uploadObject(name, Base64.getDecoder.decode(content)))
  }

  override def getTenantInformation(tenantId: String)(implicit conversion: RootJsonFormat[TenantInformationFull]): Future[TenantInformationFull] = {
    println(s"Received GetTenantRequest: $tenantId")
    val information = Future(DatastoreHandler.getEntry(tenantId))
    getLogo(tenantId)
      .flatMap(logo => information.map(info => {
        val response = info.withLogo(logo)
        println(s"Completed GetTenantRequest: ${response.toJson.compactPrint}")
        response
      }))
  }

  override def getTenantByName(name: String)(implicit conversion: RootJsonFormat[TenantInformationFull]): Future[TenantInformationFull] = {
    println(s"Received GetTenantRequest by Name: $name")
    Future(DatastoreHandler.getByName(name))
      .flatMap(info => getLogo(info.tenantId).map(logo => {
        val response = info.withLogo(logo)
        println(s"Completed GetTenantRequest by Name: ${response.toJson.compactPrint}")
        response
      }))
  }

  override def getLogo(tenantId: String): Future[String] = {
    println(s"Received GetLogoRequest: $tenantId")
    Future(GoogleBucketHandler.getObject(tenantId))
      .map(logo => {
        val response = Base64.getEncoder.encodeToString(logo)
        println(s"Completed GetLogoRequest: $response")
        response
      })
  }
}
