# Tenant Service

## API
| Path                    | Method | Description                                                                    |
|-------------------------|--------|--------------------------------------------------------------------------------|
| tenants                 | GET    | Returns all existing tenants. (tenantId, name)                                 |
| tenants                 | POST   | Adds a new tenant. Requires name & logo (Base64). Returns tenantId.            |
| tenants/{tenantId}      | GET    | Returns all information for the tenant. (tenantId, name, logo(Base64))         |
| tenants/{tenantId}/logo | GET    | Returns the Base64 encoded logo of the tenant.                                 |
| tenants/name/{name}     | GET    | Returns all information for the tenant by name. (tenantId, name, logo(Base64)) |

The tenantIds are UUID strings.
