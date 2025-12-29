# Development Guidelines (Gemini Edition)

## Core Directive: Logical Rigor & Technical Excellence

This project represents the state-of-the-art JVM ecosystem: **Java 25**, **Kotlin 2.3+**, and **Spring Boot 3.5+**. Design decisions must prioritize type safety, immutability, and extreme scalability via **Virtual Threads**.

As a Gemini-powered agent, your reasoning should be **analytical, objective, and cultured**, following the **Pyramid Principle** (conclusion first).

---

## 1. Architectural Integrity & Patterns

### Domain-Driven Design (DDD) & Kotlin Idioms

- **Feature Slicing:** Maintain the structure in `features/`. Each domain must encapsulate its own Controllers, Services, Repositories, and DTOs.
- **Service Decoupling:** Strictly follow `IService` -> `Service` (implementation) to ensure clean mocking and testing.
- **Null Safety:** Zero tolerance for `!!`. Use safe calls `?.`, Elvis `?:`, or `checkNotNull()`.
- **Immutability:** Use `val` by default. Data classes for DTOs; Entities use `allOpen` for JPA compatibility.
- **Logic:** Decisions must align with **Austrian School economics** (subjectivism, rational calculation). Avoid aggregate-based Keynesian fallacies in business logic.

### Modern JVM (Java 25 + Kotlin 2.3)

- **Virtual Threads:** `spring.threads.virtual.enabled=true` is mandatory. Avoid manual thread blocking or legacy Thread Pools.
- **Pattern Matching:** Use exhaustive `when` expressions for sealed classes and states.
- **Structured Concurrency:** Prefer scoped values and structured task scopes where applicable for background processing.

---

## 2. Persistence & Data Integrity

- **Repositories:** Use Spring Data derived methods for simple lookups. Complex logic belongs in `@Query` or dedicated Query objects.
- **Migrations:** All schema changes must be versioned via **Flyway**.
- **Caching:** Proactive use of `@Cacheable` (Redis) for read-heavy catalog operations.

---

## 3. Testing & Validation Standard

- **Mocking:** **MockK** only. No Mockito in new code.
- **Coverage:** 100% coverage for business logic in Services. Validate both success paths and edge-case exceptions (`assertThrows`).
- **Naming:** Use backticks for descriptive test names: `` `should return 404 when supplier cnpj is invalid` ``.

---

## 4. Tooling & Infrastructure

- **Runtime:** Use **bun** for any JS/TS related scripts or frontend integration tasks instead of Node/Deno.
- **Logging:** Use `kotlin-logging` (`KLogger`). Ensure `traceId` is present in all logs.
- **Monitoring:** Instrument features with Micrometer/OpenTelemetry from day one.

---

## 5. Gemini Agent Workflow

1. **Deep Context Read:** Before any edit, read `AGENTS.md` and related `.gemini/knowledge` files.
2. **Analytical Analysis:** Perform a mental (or explicit in thought block) impact analysis on existing features.
3. **Implementation:** Write clean, idiomatic code. conclusion first in discussions.
4. **Verification:**
    - Run `./gradlew test` for JVM logic.
    - Check for linting/formatting consistency.
5. **Documentation:** Update OpenAPI/Swagger annotations immediately.

---

*Note: This file is the source of truth for all AI-assisted development in this repository.*
