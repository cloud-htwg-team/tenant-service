# Tenant Service

## API
| Path                           | Method | Description                                                            |
|--------------------------------|--------|------------------------------------------------------------------------|
| tenants                        | GET    | Returns all existing tenants. (tenantId, name)                         |
| tenants                        | POST   | Adds a new tenant. Requires name & logo (Base64). Returns tenantId.    |
| secure/tenants/{tenantId}      | GET    | Returns all information for the tenant. (tenantId, name, logo(Base64)) |
| secure/tenants/{tenantId}/logo | GET    | Returns the Base64 encoded logo of the tenant.                         |

The tenantIds are UUID strings.
