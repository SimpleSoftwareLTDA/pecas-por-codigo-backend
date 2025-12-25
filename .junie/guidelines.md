# Project Guidelines

## Coding Conventions

- **Language:** Kotlin is the primary language for this project.
- **Logging:** Use `io.github.oshai.kotlinlogging.KotlinLogging` for logging. Define a private logger at the top of the file: `private val logger = KotlinLogging.logger {}`.
- **Error Handling:** 
    - Use `runCatching` blocks in services for handling operations that might fail, providing appropriate logging and fallback values or rethrowing exceptions.
    - Define custom exceptions in `org.pecasonline.common.exceptions` (e.g., `NotFoundException`).
    - Use `ExceptionsAdvisor` for global exception handling in the web layer.
- **Service Layer:**
    - Prefer using interfaces for services (e.g., `ISupplierService`) to facilitate mocking and decoupling.
    - Use constructor-based dependency injection.
- **Data Access:**
    - Use Spring Data JPA repositories.
    - Annotate service methods with `@Transactional` when performing write operations to ensure atomicity.
- **Formatting:**
    - Follow standard Kotlin coding styles.
    - Use 4 spaces for indentation.
    - Maintain consistency with existing code regarding naming and structure.

## Code Organization and Package Structure

The project follows a **feature-based organization** structure under the `org.pecasonline` package:

- `org.pecasonline.features.<feature_name>`: Contains all components related to a specific business feature.
    - `controller`: REST controllers and Swagger documentation/specifications.
    - `domain`: JPA entities representing the core data models.
    - `dto`: Data Transfer Objects for API requests and responses.
    - `repository`: Spring Data repositories for database access.
    - `service`: Service interfaces and implementations containing business logic.
- `org.pecasonline.common`: Contains shared utilities, base classes, exceptions, and common HTTP clients used across different features.

## Testing Approaches

### Unit Testing

- **Framework:** JUnit 5 is used for writing and running tests.
- **Mocking:** Use Mockito with the `mockito-kotlin` library for creating and managing mocks.
- **Naming:** Use backticks in test method names to describe the behavior being tested clearly (e.g., `` `should find suppliers successfully` ``).
- **Structure:** 
    - Use `@Mock` and `@InjectMocks` annotations for setup.
    - Use `whenever(...).thenReturn(...)` or `whenever(...).thenThrow(...)` for stubbing.
    - Use `assertEquals`, `assertTrue`, and `assertThrows` for assertions.
- **Location:** Unit tests are located in `src/test/kotlin` and should follow the same package structure as the class under test.

### Integration Testing

- Integration tests (when applicable) should also be located in `src/test/kotlin`, potentially using `@SpringBootTest` to load the application context.
- Use `mock` or `any()` from `mockito-kotlin` when fine-grained control over dependencies is needed within integration tests.
