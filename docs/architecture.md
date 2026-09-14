# Architecture notes

## Request paths

Two front doors share everything below the controller layer.

- `web/*Controller` render Thymeleaf templates. Forms POST back with the CSRF token Thymeleaf injects, results are communicated through flash attributes and a redirect (PRG pattern), and `WebExceptionHandler` turns `NotFoundException` into the `error` template. Spring Security's own 403 lands on the same template through Boot's error controller.
- `api/*ApiController` return JSON. `ApiExceptionHandler` (`@RestControllerAdvice` scoped to the `api` package) maps `NotFoundException` to 404, `IllegalArgumentException` and validation failures to 400, `IllegalStateException` to 409, `AccessDeniedException` to 403, and anything else to 500 with the details logged server-side only.

Both use the same session cookie. `SecurityConfig` permits the login page, static assets and health endpoints, sends unauthenticated `/api/**` calls a bare 401 instead of a redirect, and enables method security so every controller method carries its own `@PreAuthorize` from `security/Access`.

## Risk pipeline (`RiskService.calculateRisk`)

1. Load the vendor and its most recent `EvaluationCriteria` (missing criteria is an `IllegalStateException`, surfaced as 409 in the API and a flash error in the UI).
2. Ask the active `RiskStrategy` for a score. `WeightedRiskStrategy` clamps inputs to [0,1] and rounds to three decimals.
3. Persist a `RiskScore` row and call `Vendor.updatePerformance`, which derives the new status (`HIGH_RISK` above the HIGH band, otherwise `ACTIVE`, except suspended or blacklisted vendors keep their manual status).
4. Compare with the threshold from the newest `risk_rules` row (default 0.7).
5. Above threshold: `AlertFactory.highRiskAlert` builds the alert, it is saved, and a `HighRiskAlertEvent` is published. Two listeners react in the same transaction: `AlertLogListener` writes a WARN line, `RecommendationListener` asks `RecommendationService` for up to three `ACTIVE` vendors in the LOW band, ranked by score, excluding the vendor itself.

Everything happens inside one `@Transactional` boundary, so a failure anywhere rolls back the score, the status change, the alert and the recommendations together.

## Persistence decisions

- Schema lives in `V1__schema.sql`; Hibernate runs with `ddl-auto: validate` so entity drift fails fast at startup.
- Enum-like columns are `VARCHAR` with `@JdbcTypeCode(SqlTypes.VARCHAR)` on the entity side. MySQL native `ENUM` columns made Hibernate's validator unhappy and would have required a migration for every new value.
- Foreign keys are plain `Integer` columns on the entities rather than `@ManyToOne` associations. The app never navigates object graphs; it lists and joins by id in services, which keeps JSON serialisation trivial and avoids lazy-loading surprises.
- The demo seed is a Flyway Java migration (`db/migration/V2__Seed_demo_data.java`) so the BCrypt hashes are produced by the same encoder used at login and never appear in source.
- `User` uses `SINGLE_TABLE` inheritance with `role` as the discriminator; the same column is mapped read-only on the base class so callers can ask any user for its role without an `instanceof`.

## Testing strategy

- One `MySQLContainer` (singleton pattern in `AbstractIntegrationTest`) serves every integration test class; Spring Boot's `@ServiceConnection` wires the datasource. Each test method runs in a rolled-back transaction, and MockMvc executes on the same thread so the rollback covers controller calls too.
- `ApiSecurityTest.roleMatrix` is the executable form of the RBAC table in the README. Add a row there whenever an endpoint or role changes.
- `RiskFlowApiTest` is the acceptance test for the pipeline above; `RiskServiceTest` covers the same branches with mocks and no database.
- JaCoCo enforces 80% line coverage at `verify`; the application entry class is excluded.

## Operational notes

- The image is built in a Maven stage and run on a JRE-only Alpine base as a non-root user, with `MaxRAMPercentage=75` so the JVM sizes itself to the container.
- Compose starts MySQL first with a health check and only then the app; the app's own health check hits `/actuator/health`.
- All secrets and hosts come from environment variables. `application-docker.yml` only changes the datasource host to the Compose service name and turns on template caching.
- To add a risk algorithm: implement `RiskStrategy` as a `@Component`, give it a unique `name()`, and either set `SENTINEL_RISK_STRATEGY` or call `RiskService.useStrategy` at runtime. Nothing else changes (Open/Closed).
