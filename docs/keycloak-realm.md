# Keycloak realm import

This project includes a Keycloak realm export at `src/main/resources/keycloak/realm-export.json`.
The `docker-compose.yaml` mounts this file into the Keycloak container and runs `start-dev --import-realm`,
so the realm is imported on startup.

## What gets created
- Realm: `easyjobs`
- Client: `easyjobs-bff` (secret `easyjobs-secret`)
- Roles: `ADMIN`, `OWNER`, `MANAGER`, `MEMBER`, `READ_ONLY`, `SUPPORT`

## How to use
1) Start the stack: `docker compose up -d`
2) Access Keycloak: `http://localhost:8180/admin` (admin/admin)
3) Use the `easyjobs` realm and the `easyjobs-bff` client to authenticate the app.

## Updating the realm export
Keycloak only imports the realm on first startup. If you change
`src/main/resources/keycloak/realm-export.json`, restart with a fresh Keycloak container:
`docker compose down` then `docker compose up -d`.

## App configuration
The app reads these defaults from `src/main/resources/application.yaml`:
- `OIDC_AUTH_SERVER_URL` (default `http://localhost:8180/realms/easyjobs`)
- `OIDC_CLIENT_ID` (default `easyjobs-bff`)
- `OIDC_CLIENT_SECRET` (default `easyjobs-secret`)
