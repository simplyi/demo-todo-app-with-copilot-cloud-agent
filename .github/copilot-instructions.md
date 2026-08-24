# Copilot Instructions for This Repository

## Project Overview
- This repository is a Spring Boot web application for a Todo app.
- It uses Java 21, Maven, and Spring Boot Web MVC.
- Keep changes simple, production-oriented, and aligned with Spring conventions.

## Recommended Project Architecture
- Use a layered structure:
  - `controller` for HTTP endpoints and request/response mapping.
  - `service` for business logic and orchestration.
  - `repository` for persistence access.
  - `model`/`entity` for domain data.
  - `dto` for API contracts when needed.
- Keep controllers thin and move business logic into services.
- Keep classes focused on one responsibility.

## Coding Conventions
- Follow standard Java style and Spring Boot idioms.
- Prefer clear, small methods over large multi-purpose methods.
- Use immutable local variables (`final`) where practical.
- Avoid wildcard imports.
- Add Javadoc only for non-obvious public APIs; keep comments meaningful and concise.

## Naming Conventions
- Use `PascalCase` for classes and interfaces.
- Use `camelCase` for methods, fields, and variables.
- Use descriptive names based on domain intent (for example, `TodoService`, `TodoController`).
- Suffix Spring components consistently (`*Controller`, `*Service`, `*Repository`).

## Dependency Injection Recommendations
- Prefer constructor injection for required dependencies.
- Avoid field injection.
- Keep dependencies explicit; inject interfaces where it improves decoupling.
- Keep bean wiring simple and rely on Spring auto-configuration unless customization is necessary.

## Testing Expectations
- Add or update tests for behavior changes.
- Prefer fast unit tests for service/business logic.
- Use `@SpringBootTest` or slice tests only when integration with Spring context is required.
- Keep tests deterministic and independent.
- Follow Given/When/Then structure in test method bodies where helpful.

## Maven Build and Test Commands
- Run tests: `./mvnw test`
- Run full verification: `./mvnw verify`
- Build artifact: `./mvnw clean package`
- Run locally: `./mvnw spring-boot:run`

## Error Handling Recommendations
- Validate input at API boundaries.
- Throw specific exceptions instead of generic `Exception`.
- Centralize API error translation with `@ControllerAdvice` for consistent HTTP responses.
- Return meaningful status codes and safe error messages (no internal implementation details).
- Log errors with enough context to diagnose issues, without logging secrets or sensitive data.

## Additional Repository-Wide Guidance
- Keep dependencies minimal; add new libraries only when justified.
- Preserve backward-compatible API behavior unless a change is explicitly required.
- Update related tests with each functional change.
- Prefer configuration via `application.properties`/profiles over hardcoded values.
