# Repository Instructions

## Project overview
- This repository contains a Spring Boot web application built with Java 21 and Maven.
- The application currently uses Spring MVC and should stay aligned with standard Spring Boot conventions.

## Recommended architecture
- Keep a clear separation between web, business, and data access concerns.
- Organize production code by responsibility, for example: `controller`, `service`, `repository`, `model`, `dto`, and `config`.
- Keep controllers thin, move business rules into services, and keep persistence logic out of controllers.
- Prefer small, focused classes and methods over large multi-purpose components.

## Coding conventions
- Follow existing project style and standard Java formatting.
- Prefer immutable local variables and keep method scope small and readable.
- Use Spring and JDK libraries before introducing new dependencies.
- Avoid premature abstraction; add new layers or utilities only when they clearly improve maintainability.
- Keep configuration in `application.properties` or dedicated configuration classes.

## Naming conventions
- Use descriptive class names with Spring-friendly suffixes such as `Controller`, `Service`, `Repository`, and `Config`.
- Name methods after the action they perform and keep boolean names explicit, such as `isCompleted` or `hasDueDate`.
- Use package names in lowercase and keep them consistent with the existing `com.appsdeveloperblog.todoapp` base package.

## Dependency injection
- Prefer constructor injection for Spring-managed beans.
- Avoid field injection.
- Inject concrete dependencies through interfaces when that improves testability or supports multiple implementations.
- Keep bean dependencies minimal and avoid circular dependencies.

## Testing expectations
- Add or update automated tests for behavior changes.
- Prefer focused unit tests for business logic and Spring integration tests only when framework wiring matters.
- Keep tests deterministic and readable.
- For web endpoints, prefer Spring MVC test support when controller behavior needs verification.

## Maven build commands
- Run tests: `./mvnw test`
- Build the application: `./mvnw clean verify`
- Run the application locally: `./mvnw spring-boot:run`

## Error handling
- Validate inputs close to the boundary of the application.
- Return clear HTTP responses from web layers and centralize shared exception handling when needed.
- Do not swallow exceptions; either handle them with context or let Spring manage them appropriately.
- Log useful diagnostic context without exposing secrets or sensitive data.

## Additional repository-wide guidance
- Keep changes minimal and scoped to the task.
- Do not add dependencies unless they are clearly necessary.
- Update tests and related documentation when behavior changes.
- Preserve secure defaults: never hardcode credentials, tokens, or secrets.
