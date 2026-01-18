# Fluxos de dados

Veja `docs/keycloak-realm.md` para o import do realm do Keycloak usado no fluxo de auth.

## 1. Identidade: `users` e `user_sessions`
Contexto `identity`.

### `users`
- DDL: `src/main/resources/db/migration/V1__create_users_table.sql`
- Domínio (POJO): `identity/domain/model/User.java` (sem anotações JPA).
- Entidade JPA: `identity/infrastructure/persistence/UserEntity.java` (`@Entity`, `@Table("users")`, Panache).
- Gateway (contrato): `identity/application/gateway/UserGateway.java` com `save(User)`, `findByEmail(String)`, `updateCredits(UUID userId, int amount)`.
- Implementação: `identity/infrastructure/persistence/UserGatewayImpl.java` (mapeia `UserEntity` <-> `User` e persiste).

### `user_sessions`
- DDL: `src/main/resources/db/migration/V1__create_sessions_table.sql`
- Domínio (POJO): `identity/domain/model/Session.java`
- Entidade JPA: `identity/infrastructure/persistence/SessionEntity.java`
- Gateway (contrato): `identity/application/gateway/SessionGateway.java`
- Implementação: `identity/infrastructure/persistence/SessionGatewayImpl.java`

## 2. Negócio: `optimizations`
Contexto `resume`.

### `optimizations`
- DDL: `src/main/resources/db/migration/V1__create_optimizations_table.sql`
- Domínio (POJO): `resume/domain/model/Optimization.java` (métodos como `isCompleted()`, `canBeEdited()`, campos `targetRole`, `optimizedContent` JSONB).
- Entidade JPA: `resume/infrastructure/persistence/OptimizationEntity.java` (`@Entity`, `@Table("optimizations")`, `@Lob` se precisar para JSONB).
- Gateway (contrato): `resume/application/gateway/OptimizationGateway.java` com `create(Optimization)`, `updateStatus(UUID id, Status)`, `findById(UUID)`.
- Implementação: `resume/infrastructure/persistence/OptimizationGatewayImpl.java`.
