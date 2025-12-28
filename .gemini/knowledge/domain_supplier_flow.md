# Domain Knowledge: Supplier Flow

## 1. Core Logic

The Supplier flow manages the onboarding and lifecycle of suppliers. It is one of the most critical parts of the system, involving address verification, contact management, and banking synchronization.

## 2. Refactored Service Layer

- **ISupplierService / SupplierService:** Orchestrates the high-level flow. Delegates specific persistence tasks to sub-services.
- **IContactService / ContactService:** Extracted to handle complex `Contact` field mapping and persistence (SRP).
- **IAddressService:** Manages geographic data and normalization.

## 3. Data Integrity Rules

- **CNPJ Uniqueness:** Enforced at both JPA level and Database level.
- **Mandatory Relationships:** Every Supplier **must** have a valid `Address` and `Contact`. These are non-nullable in the Kotlin domain and JPA Mapping (`nullable = false`).
- **ASASS Integration:** Upon creation, a Supplier is synchronized with the ASASS API to facilitate payments and subscriptions.

## 4. API Contract (SupplierResponseDTO)

To ensure security:

- `asaasId` is hidden from responses.
- `token` data is hidden.
- Nested relationships (Address, Contact) are returned but strictly mapped.
