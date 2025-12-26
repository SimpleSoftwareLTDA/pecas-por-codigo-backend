# AGENTS.md - Diretrizes de Desenvolvimento (Kotlin + Spring Boot + Java 25)

## Diretriz Principal: Manter a Rigidez Lógica e Excelência Técnica
Este projeto utiliza o estado da arte do ecossistema JVM (Java 25, Kotlin 2.3+, Spring Boot 3.5+). As decisões de design devem priorizar segurança de tipos, imutabilidade e escalabilidade via Virtual Threads.

---

## 1. Arquitetura e Estrutura de Código
### Domain-Driven Design (DDD)
*   **Fatiamento por Feature:** Manter o padrão atual na pasta `features`. Cada domínio deve conter seus próprios Controllers, Services, Repositories e Entities.
*   **Services baseados em Interface:** Seguir o padrão `IBrandService` -> `BrandService`. Isso facilita o decoupling e o mocking em testes de integração.
*   **Entidades Imutáveis:** Utilizar `val` sempre que possível. O plugin `allOpen` no `build.gradle.kts` garante a compatibilidade com JPA/Hibernate.

### Práticas Kotlin
*   **Null Safety:** Evitar o uso do operador `!!`. Utilizar safe calls `?.`, Elvis operator `?:` ou let `value?.let { ... }`.
*   **Functional Idioms:** Preferir transformações de coleção (`map`, `filter`, `fold`) em vez de loops `for` manuais.
*   **Diferenciação de DTOs e Entities:** Nunca retornar entidades JPA diretamente nas APIs. Utilizar `data class` para DTOs de Request/Response.

---

## 2. Java 25 & Spring Boot 3.5+ Modernization
### Concorrência e Performance
*   **Virtual Threads (Project Loom):** Certificar-se de que `spring.threads.virtual.enabled=true` está ativo no `application.yml`. Evitar bloqueios manuais de threads.
*   **Pattern Matching:** Utilizar expressões `when` do Kotlin de forma exaustiva para lidar com tipos selados (`sealed class`) e estados complexos.

### Comunicação e API
*   **Problem Details (RFC 7807):** Padronizar respostas de erro utilizando o suporte nativo do Spring Boot para `ProblemDetail`.
*   **Declarative Clients:** Para integrações externas (como Asaas), preferir `interface` com `@HttpExchange` em vez de hooks manuais de Feign ou RestTemplate.

---

## 3. Gestão de Dados e Persistência
*   **Repositories:** Utilizar indentação e nomes de métodos derivados do Spring Data JPA para queries simples. Para queries complexas, utilizar `@Query` com JPQL/Native SQL em arquivos dedicados ou constantes.
*   **Migrations:** Reativar o Flyway (`org.flywaydb`) para garantir que o schema production-ready seja versionado.
*   **Redis Cache:** Utilizar a anotação `@Cacheable` em métodos de leitura pesada (ex: busca de marcas ou catálogos).

---

## 4. Estratégia de Testes
*   **Mocking:** Utilizar exclusivamente `MockK` para Kotlin. Evitar Mockito onde a sintaxe do MockK for mais expressiva.
*   **Integração:** Testes de Controller devem usar `@WebMvcTest` ou `@SpringBootTest` com `MockMvc` para validar contratos e serialização.
*   **Unitários:** Lógica de negócio em Services deve ter 100% de cobertura, validando casos de sucesso e exceções (`assertThrows`).

---

## 5. Observabilidade e Monitoramento
*   **Logging:** Utilizar a biblioteca `kotlin-logging` (`KLogger`). Não utilizar `println` ou `System.out`.
*   **Tracing:** Toda nova funcionalidade deve ser instrumentada via Micrometer/OpenTelemetry (já configurado no projeto).
*   **Sentry:** Garantir que exceções críticas não capturadas sejam enviadas ao Sentry com contexto suficiente (User ID, Request ID).

---

## Fluxo de Trabalho do Agente
1.  **Análise:** Antes de criar código, verifique o impacto no domínio (`features`).
2.  **Implementação:** Siga os padrões de imutabilidade.
3.  **Validação:** Execute `./gradlew test` antes de considerar a tarefa concluída.
4.  **Documentação:** Atualize o Swagger/OpenAPI através de anotações no Controller.
