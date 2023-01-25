package de.htwg.cad.qr.tenant

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

trait JsonParser  {
  implicit val additionRequestFormat: RootJsonFormat[TenantCreationRequest] = jsonFormat2(TenantCreationRequest)
  implicit val tenantDataShortFormat: RootJsonFormat[TenantInformationShort] = jsonFormat2(TenantInformationShort)
  implicit val tenantDataFullFormat: RootJsonFormat[TenantInformationFull] = jsonFormat3(TenantInformationFull)
}
