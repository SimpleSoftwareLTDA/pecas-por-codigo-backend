# Banking Integration: Asaas

## 1. Overview

The platform uses the **Asaas API** for financial operations, including customer creation, billing, and subscription management for suppliers.

## 2. Key Components

- **AsaasClient:** A Feign/Declarative client for communicating with `https://www.asaas.com/api/v3`.
- **BankingService:** Abstracted service that maps domain entities to Asaas requests.
- **AsaasId:** Each `Supplier` entity stores an `asaasId` which acts as the foreign key in the Asaas ecosystem.

## 3. Transactional Safety

**CRITICAL:** Avoid making external Asaas API calls inside a `@Transactional` block. Long-running network calls can exhaust the connection pool and cause DB deadlocks.

- *Current Policy:* Trigger synchronization after the DB transaction commits (Transactional Outbox or Post-Commit Hooks).
