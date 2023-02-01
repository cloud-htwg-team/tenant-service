package de.htwg.cad.qr.tenant

case class TenantCreationRequest(name: String, logo: String, premium: Boolean)
case class TenantInformationShort(tenantId: String, name: String) {
  def withLogo(logo: String): TenantInformationFull = TenantInformationFull(tenantId, name, logo)
}
case class TenantInformationFull(tenantId: String, name: String, logo: String)
