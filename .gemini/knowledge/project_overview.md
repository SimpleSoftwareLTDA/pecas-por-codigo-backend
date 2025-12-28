# Project Overview - Peca Online Backend

## 1. Vision & Purpose

The **pecas-por-codigo-backend** is a high-performance JVM-based service designed to manage the supplier network and inventory for the "Peças por Código" platform. It orchestrates complex flows including supplier management, banking integrations (Asaas), and spare parts cataloging.

## 2. Technical Stack

- **Languages:** Kotlin 2.1+, Java 25 (Virtual Threads enabled).
- **Core Framework:** Spring Boot 3.4+.
- **Database:** PostgreSQL (Persistence), H2 (Testing).
- **Cache:** Redis for high-frequency reads.
- **Observability:** Micrometer/OpenTelemetry, Sentry, Grafana.
- **Design:** Clean Architecture with Feature-based slicing.

## 3. High-Level Architecture

- **Feature Slicing:** All layers (Controller, Service, Repository, Entity, DTO) are contained within feature packages (e.g., `org.pecasonline.features.supplier`).
- **Loom-First:** Configured to use Virtual Threads for handling I/O bound requests efficiently.
- **DTO Separation:** Strict separation between JPA entities and API contracts using dedicated ResponseDTOs to prevent domain leakage.
