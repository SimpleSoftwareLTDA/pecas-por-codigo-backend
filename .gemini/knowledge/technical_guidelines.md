# Technical Guidelines & Best Practices

## 1. Kotlin Style

- **Indentation:** 4 spaces.
- **Nullability:** Strict avoidance of `!!`. Use `?.let`, `?:`, or explicit `checkNotNull`.
- **Naming:** Backticks for test names `` `should return forbidden if user is not admin` ``.

## 2. Spring Boot & JPA

- **DTO-First:** Never return an `@Entity` from a `@RestController`.
- **Interface-Based Services:** Always use `IService` -> `Service` pattern for DI.
- **Transactional:** Use `@Transactional(rollbackFor = [Exception::class])` for all write operations.

## 3. Testing Standard

- **Unit (Services):** Mock dependencies using `MockK`.
- **Integration (Repositories):** Use H2 database with `@DataJpaTest`.
- **Naming Convention:** `FeatureName + Layer + Test` (e.g., `SupplierServiceTest`).

## 4. Observability

- **Logs:** Use `KLogger` from `kotlin-logging`.
- **Metrics:** Increment Micrometer counters for business-critical events (e.g., searches, failures).
