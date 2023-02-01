package de.htwg.cad.qr.tenant.db

import com.google.cloud.datastore.StructuredQuery.PropertyFilter
import com.google.cloud.datastore._
import de.htwg.cad.qr.tenant.{TenantCreationRequest, TenantInformationShort}

import scala.jdk.CollectionConverters.IteratorHasAsScala

private object DatastoreHandler {
  private val projectId = "qrcode-374515"
  private val datastore = DatastoreOptions.newBuilder.setProjectId(projectId).build.getService
  private val kind = "tenant"

  def saveTenant(request: TenantCreationRequest, id: String): String = {
    val taskKey = datastore.newKeyFactory.setKind(kind).newKey(id)
    val tenantInformation = Entity.newBuilder(taskKey)
      .set("tenantId", id)
      .set("name", request.name)
      .set("premium", request.premium)
      .build
    datastore.put(tenantInformation)
    id
  }

  def getEntry(tenantId: String): TenantInformationShort = {
    val taskKey = datastore.newKeyFactory.setKind(kind).newKey(tenantId)
    val retrieved: Entity = datastore.get(taskKey, Seq.empty[ReadOption]: _*)
    TenantInformationShort(
      retrieved.getString("tenantId"),
      retrieved.getString("name")
    )
  }

  def getByName(name: String): TenantInformationShort = {
    val query = Query.newEntityQueryBuilder()
      .setKind(kind)
      .setFilter(PropertyFilter.eq("name", name))
      .setLimit(1)
      .build()
    collectEntries(query).head
  }

  def listTenants(): List[TenantInformationShort] = {
    val query = Query.newEntityQueryBuilder()
      .setKind(kind)
      .setFilter(PropertyFilter.eq("premium", false))
      .build()
    collectEntries(query)
  }

  private def collectEntries(query: EntityQuery): List[TenantInformationShort] =
    datastore.run(query, Seq.empty[ReadOption]: _*).asScala
      .map(entry => TenantInformationShort(
        entry.getString("tenantId"),
        entry.getString("name")))
      .toList
}
