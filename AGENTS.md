# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/com/easyjobs/`: feature-based packages (e.g., `greeting/api`, `greeting/domain`, `greeting/infrastructure`).
- `src/main/resources/`: runtime resources such as `application.properties` and `import.sql`.
- `src/test/java/com/easyjobs/`: unit and integration tests mirroring the main packages.
- `src/main/docker/`: Dockerfiles for JVM, legacy JAR, and native builds.
- `pom.xml`: Maven build configuration, dependencies, and test plugins.

## Build, Test, and Development Commands
- `./mvnw quarkus:dev`: run in dev mode with live reload (Dev UI at `http://localhost:8080/q/dev`).
- `./mvnw test`: run unit tests (JUnit 5).
- `./mvnw verify`: run unit tests plus integration tests (Failsafe).
- `./mvnw package`: build the JVM artifact in `target/quarkus-app/`.
- `./mvnw package -Dquarkus.package.jar.type=uber-jar`: build an uber-jar.
- `./mvnw package -Dnative`: build a native image (requires GraalVM) or use `-Dquarkus.native.container-build=true`.

## Coding Style & Naming Conventions
- Java 21, 4-space indentation, standard Quarkus/Jakarta annotations.
- Packages follow `com.easyjobs.<feature>.*` with `application/usecase` for orchestration and `application/gateway` for ports.
- Class names use `PascalCase` (e.g., `GreetingResource`, `MyEntity`).
- Resource endpoints typically end with `Resource` and map paths with `@Path`.
- Entities use Hibernate Panache Active Record; omit getters/setters unless strictly required.

## Development Principles

### TDD (Test-Driven Development)
- Follow the **RED → GREEN → REFACTOR** cycle for every new feature:
  1. **RED**: Write a failing test that defines the expected behavior.
  2. **GREEN**: Write the minimum code necessary to make the test pass.
  3. **REFACTOR**: Clean up the code while keeping tests green.

### Object-Oriented Design
- **Prefer composition over inheritance**: Use object composition to achieve code reuse instead of class inheritance hierarchies.
- **Object creation patterns**:
  - Use the **Builder Pattern** when a class has many constructor parameters (especially optional ones).
  - Use **Static Factory Methods** when constructors are simple and have few parameters.

### Immutability
- **Always prefer immutability**: Make classes and their fields immutable wherever possible.
- Use `final` for fields, return defensive copies of collections, and avoid setters.

### Object Calisthenics (9 Rules)
1. **One level of indentation per method**.
2. **Don't use the `else` keyword**.
3. **Wrap all primitives and strings** (in domain-specific types).
4. **First-class collections** (wrap collections in their own classes).
5. **One dot per line** (Law of Demeter).
6. **Don't abbreviate** (use meaningful, descriptive names).
7. **Keep all entities small** (≤50 lines per class, ≤5 lines per method).
8. **No classes with more than two instance variables**.
9. **No getters/setters/properties** (tell, don't ask).

### SOLID Principles
- **S**ingle Responsibility: A class should have only one reason to change.
- **O**pen/Closed: Open for extension, closed for modification.
- **L**iskov Substitution: Subtypes must be substitutable for their base types.
- **I**nterface Segregation: Prefer small, specific interfaces over large, general ones.
- **D**ependency Inversion: Depend on abstractions, not concretions.

### DRY (Don't Repeat Yourself)
- Avoid code duplication. Extract common logic into reusable methods or classes.

### YAGNI (You Aren't Gonna Need It)
- Don't implement features until they are actually needed. Avoid premature abstraction.

## Testing Guidelines
- Frameworks: JUnit 5 with RestAssured for HTTP tests.
- Naming: unit tests end with `Test`, integration tests end with `IT` (e.g., `GreetingResourceTest`, `GreetingResourceIT`).
- Integration tests run with `./mvnw verify` (they are skipped by default via `skipITs=true`).

## Commit & Pull Request Guidelines
- Commit history only shows an initial commit, so no established message convention yet.
- Prefer concise, imperative commit subjects (e.g., "Add user signup endpoint").
- PRs should include a short description, testing notes (commands + results), and any relevant API changes or configuration updates.

## Configuration & Security Notes
- Runtime config lives in `src/main/resources/application.properties`.
- Do not commit secrets; use environment variables or external config in dev/prod.
