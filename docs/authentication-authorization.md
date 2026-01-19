# Autenticacao e autorizacao (BFF + OIDC)

Este projeto usa o padrao **BFF (Backend For Frontend)** com **OIDC** via Keycloak.
O backend cria e gerencia a sessao; o navegador recebe apenas um cookie opaco `HttpOnly`.
Access/Refresh Tokens ficam **somente no servidor** e sao persistidos no **Redis**.

## Por que OIDC + Keycloak
- **Padrao aberto**: OIDC e o fluxo recomendando para autenticacao moderna.
- **Separacao de responsabilidades**: o backend nao lida com senha diretamente no login.
- **Centralizacao de identidade**: usuarios, roles e politicas ficam no Keycloak.
- **Escalabilidade**: possivel evoluir para MFA, social login e federacao sem mudar o app.

## Componentes
- **Keycloak**: provedor OIDC e servidor de identidades.
- **Quarkus OIDC (web-app)**: gerencia sessao do usuario com cookie de sessao.
- **Redis**: armazenamento do estado de tokens no servidor.

## Fluxo de autenticacao
1) O frontend chama um endpoint protegido (ex: `GET /api/auth` ou `GET /api/auth/login`).
2) Quarkus detecta ausencia de sessao e redireciona o usuario para o Keycloak.
3) O usuario autentica no Keycloak.
4) Quarkus recebe o callback OIDC e cria a sessao local.
5) O backend armazena tokens no Redis e devolve o cookie de sessao `HttpOnly`.

## Fluxo de autorizacao (RBAC)
- As roles sao obtidas do claim `realm_access/roles`.
- O backend usa `SecurityIdentity` para expor `roles` em `/api/auth`.
- Roles atuais: `ADMIN`, `OWNER`, `MANAGER`, `MEMBER`, `READ`, `READ_ONLY`, `SUPPORT`.

## Cadastro de usuario
Endpoint publico:
- `POST /api/auth/register`
- Payload: `name`, `email`, `password`
- Role padrao: `READ`

O cadastro cria o usuario no Keycloak e atribui a role.
Se a atribuicao falhar, o usuario e removido para evitar estado inconsistente.

Observacao: o auto-registro via tela do Keycloak esta desabilitado no realm exportado.
O fluxo oficial de cadastro e o endpoint do backend; o login auto-reconhece usuarios criados
por esse fluxo. Para habilitar o cadastro na UI do Keycloak, ajuste o realm e reimporte.

## Login e logout explicitos
- **Login**: `GET /api/auth/login` (forca o fluxo OIDC).
- **Logout**: `POST /api/auth/logout` (encerra a sessao no backend e remove o cookie).

Em caso de falha no logout do provedor, o backend registra o erro e retorna `204`.

## CORS e cookies
Para permitir cookies entre dominios diferentes, o backend habilita CORS com credenciais:
- `quarkus.http.cors.enabled=true`
- `quarkus.http.cors.origins=http://localhost:3000`
- `quarkus.http.cors.access-control-allow-credentials=true`

## Configuracao principal
Exemplo (ver `src/main/resources/application.yaml`):
- `quarkus.oidc.application-type=web-app`
- `quarkus.oidc.auth-server-url=http://localhost:8180/realms/easyjobs`
- `quarkus.oidc.client-id=easyjobs-bff`
- `quarkus.oidc.credentials.secret=easyjobs-secret`
- `quarkus.oidc.token-state-manager.strategy=keep-all-tokens`
- `quarkus.redis.hosts=redis://localhost:6379`

## Uso do realm exportado
O realm do Keycloak esta em `src/main/resources/keycloak/realm-export.json`
e e importado automaticamente via `docker-compose.yaml`.
Veja `docs/keycloak-realm.md` para detalhes.
