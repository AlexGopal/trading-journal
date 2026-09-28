# Trading Journal and Analysis App — Architecture

## Purpose

This document defines the target system structure, major component boundaries, responsibilities, interactions, runtime topology, and architectural constraints for the Trading Journal and Analysis App.

It is the Architecture artifact for the `GREENFIELD_APPLICATION` workflow and is governed by the approved project Constitution.

This document defines **how the system is structurally organized**. It does not define detailed feature behavior, final API endpoint contracts, database schemas, acceptance criteria, or implementation tasks.

Feature 001 remains intentionally limited to recording completed stock trades and calculating approved performance measures.

---

# 1. Architectural Drivers

The architecture is driven by the following approved product and engineering goals:

1. Support a responsive web application for an individual trader.
2. Keep Feature 001 small and implementation-focused.
3. Separate frontend, backend, and relational persistence responsibilities.
4. Keep business rules authoritative in the backend.
5. Use a maintainable layered Spring Boot architecture.
6. Use a typed React + TypeScript frontend.
7. Persist application data in PostgreSQL.
8. Expose backend capabilities through REST over HTTP.
9. Preserve room for future product growth without implementing speculative abstractions now.
10. Support automated testing at appropriate architectural boundaries.
11. Keep local infrastructure reproducible across development machines.
12. Avoid adding authentication, charting, market-data services, brokerage integrations, or other future infrastructure before those capabilities are approved.

The architecture therefore favors a straightforward modular web-application structure rather than microservices, distributed systems, or other infrastructure that Feature 001 does not require.

---

# 2. Constitution Constraints

The Architecture must comply with `.specify/memory/constitution.md`.

The following Constitution rules directly constrain this design.

## 2.1 Application Separation

The system must maintain distinct responsibility boundaries between:

- React frontend;
- Spring Boot backend;
- PostgreSQL persistence layer.

The frontend must not access PostgreSQL directly.

All persistence access occurs through the backend.

## 2.2 Backend Layering

Backend responsibilities must remain separated by concern:

```text
HTTP / API Boundary
        ↓
Application / Service Layer
        ↓
Persistence Boundary
        ↓
PostgreSQL
```

Controllers own HTTP concerns.

Application/service components coordinate business behavior.

Repositories own persistence access.

Domain/model structures represent approved data concepts.

Business logic must not be placed directly in controllers.

## 2.3 Business Logic Authority

Approved domain calculations and validation rules must be enforced by the backend.

Frontend validation may provide immediate user feedback but cannot replace backend validation.

Feature 001 performance calculations therefore belong to backend business/application logic.

## 2.4 Type Safety

Java and TypeScript representations should preserve domain meaning.

Finite concepts such as trade direction should use deliberate typed representations rather than uncontrolled strings where practical.

Financial values must use domain-appropriate representations.

Exact precision, scale, and rounding rules remain owned by the Data Model and downstream requirements.

## 2.5 Scope Discipline

Architecture must not introduce speculative support for:

- additional asset types;
- open positions;
- authentication;
- brokerage integration;
- market-data integration;
- charting;
- dashboards;
- AI-assisted analysis;
- automatic technical-indicator calculation;
- message brokers;
- distributed caching;
- microservices;
- other future infrastructure.

Future extensibility should come from clear responsibilities and boundaries rather than unused abstraction hierarchies.

---

# 3. System Context

The Trading Journal and Analysis App is a single-user responsive web application.

For Feature 001, the system consists of three primary runtime areas:

```text
┌──────────────────────┐
│        User          │
└──────────┬───────────┘
           │ Browser interaction
           ▼
┌──────────────────────┐
│ React + TypeScript   │
│ Frontend             │
└──────────┬───────────┘
           │ REST / HTTP
           ▼
┌──────────────────────┐
│ Spring Boot Backend  │
│ Application          │
└──────────┬───────────┘
           │ JPA / Hibernate
           ▼
┌──────────────────────┐
│ PostgreSQL Database  │
└──────────────────────┘
```

There are no external brokerage, market-data, authentication, or trading-platform integrations in Feature 001.

The backend is the architectural center for application behavior and domain-rule enforcement.

---

# 4. Component Model

The logical component model is:

```text
Frontend
├── User Interface Components
├── Feature/Form State
├── Client-Side Validation Feedback
├── API Client Boundary
└── API-Facing TypeScript Models

Backend
├── API / Controller Boundary
├── API DTO Boundary
├── Application / Service Layer
├── Domain / Business Logic
├── Persistence Boundary
└── Repository Layer

Infrastructure
└── PostgreSQL
```

These are logical responsibilities.

Exact repository folders, Java package names, TypeScript directory names, and class names must not be invented in this artifact. They will be established through repository structure and the downstream Plan before implementation.

---

# 5. Backend Architecture

## 5.1 API / Controller Boundary

Controllers are responsible for HTTP-facing concerns such as:

- receiving requests;
- binding request data;
- initiating validation;
- delegating application work;
- converting application outcomes into approved HTTP responses.

Controllers must not contain Feature 001 P&L formulas or persistence logic.

Detailed endpoint paths, HTTP methods, status codes, and payload contracts are deferred to Specification and OpenAPI.

## 5.2 API DTO Boundary

API request and response structures should use deliberate API-facing models where this prevents persistence structures from becoming accidental external contracts.

The API boundary must not expose JPA entities merely for convenience.

The exact DTO fields and shapes will be defined by the Data Model, Specification, and OpenAPI.

The Architecture does not freeze request or response schemas.

## 5.3 Application / Service Layer

The application/service layer coordinates the Feature 001 use case.

Its responsibilities include:

- receiving validated trade-entry information from the API boundary;
- applying approved business behavior;
- invoking domain calculations;
- normalizing values when required by approved business rules;
- coordinating persistence;
- returning an application result to the API layer.

For Feature 001, the backend service/application layer is responsible for ensuring that the approved trade-recording workflow is executed as one coherent application operation.

## 5.4 Domain / Business Logic

Domain/business logic owns rules that are independent of HTTP and persistence mechanics.

For Feature 001 this includes, where applicable:

- LONG versus SHORT performance behavior;
- dollar profit/loss calculation;
- percentage-return calculation;
- business interpretation of profit, loss, and break-even;
- ticker normalization;
- domain validation that belongs to approved business rules.

These rules must remain testable without requiring the frontend or a running HTTP server.

Exact Java class design belongs to downstream design and implementation work.

## 5.5 Repository Layer

Repositories provide persistence access for approved domain data.

Spring Data JPA / Hibernate is the baseline persistence mechanism.

Repository responsibilities include:

- persisting approved trade data;
- retrieving persisted data required by approved application behavior;
- isolating normal persistence concerns from controller and frontend code.

Repository interfaces must not define future analytical queries before corresponding requirements exist.

---

# 6. Frontend Architecture

The frontend is a standalone React + TypeScript application built with Vite and using Material UI as its primary component library.

## 6.1 UI Responsibilities

The frontend is responsible for:

- rendering the trade-entry experience;
- collecting user input;
- displaying user-facing validation feedback;
- invoking the backend API;
- presenting successful results;
- presenting approved error states.

Feature 001 should remain a focused UI rather than introducing dashboard or analytical structures that are not yet required.

## 6.2 API Client Boundary

Backend communication should be isolated behind a deliberate frontend API-client boundary rather than being scattered throughout unrelated UI components.

The API client is responsible for:

- issuing HTTP requests to approved backend endpoints;
- sending and receiving approved API representations;
- handling transport-level concerns;
- surfacing backend outcomes to the calling frontend logic.

Exact client-library selection and file placement are not frozen here unless already established by repository conventions.

## 6.3 Frontend Models and Types

TypeScript types should represent approved frontend and API data deliberately.

The frontend must not use `any` as a shortcut around known application data structures.

The frontend should not independently redefine backend business rules.

Where client-side validation mirrors an approved backend rule for user experience, backend validation remains authoritative.

## 6.4 Material UI Boundary

Material UI is the primary component system.

Feature 001 may use appropriate MUI controls for:

- form inputs;
- date entry;
- numeric entry;
- selection controls;
- validation feedback;
- buttons;
- layout;
- result presentation.

A charting library must not be introduced for Feature 001.

Detailed visual styling and theme design remain implementation/design concerns unless promoted into later requirements.

---

# 7. Persistence Boundary

PostgreSQL is the authoritative persistent datastore for application data.

Spring Data JPA / Hibernate provides the standard relational persistence boundary.

The persistence architecture must support the current trading-journal feature without prematurely modeling future strategy, indicator, brokerage, or multi-asset concepts.

## 7.1 Persistence Responsibilities

The persistence layer is responsible for:

- durable storage of approved trading-journal data;
- retrieval of that data for approved application operations;
- enforcing database-level constraints where appropriate and approved downstream;
- remaining inaccessible directly from the frontend.

## 7.2 Data Representation

The exact entity structure, table structure, identifiers, column types, precision, scale, nullability, and relationships are deferred to `data-model.md`.

In particular, Architecture does not decide:

- monetary precision;
- fractional-share precision;
- percentage precision;
- rounding rules;
- whether calculated performance values are persisted or derived when read.

Those decisions must be made deliberately in the Data Model and Requirements.

## 7.3 Schema Evolution

PostgreSQL schema creation and evolution use explicit, versioned Flyway migrations.

Spring Data JPA / Hibernate remains the application persistence/ORM layer, while Flyway owns the authoritative schema lifecycle. Hibernate automatic schema generation must not be used as the authoritative schema-management strategy.

Feature 001 must include an initial versioned migration for the approved Trade table.

---

# 8. API Boundary

The React frontend communicates with the Spring Boot backend through REST over HTTP.

The API boundary is responsible for separating browser/UI concerns from backend application behavior.

The architecture requires:

- resource-oriented REST design;
- deliberate request and response models;
- backend validation;
- consistent error handling;
- future OpenAPI documentation before implementation is considered contract-complete.

The Architecture intentionally does **not** define:

- exact endpoint paths;
- exact HTTP methods;
- request fields;
- response fields;
- HTTP status codes;
- error payload structure.

Those are downstream Specification and OpenAPI responsibilities.

## 8.1 API Versioning

The Constitution requires the initial API versioning approach to be defined before public API paths are frozen.

No API path is frozen by this Architecture artifact.

The exact versioning convention may therefore be finalized by Architecture review or the Feature 001 Specification before OpenAPI generation.

---

# 9. Integration Boundaries

Feature 001 has no approved external runtime integration.

The application must not currently integrate with:

- brokerages;
- market-data providers;
- trading platforms;
- authentication providers;
- AI services;
- technical-indicator services;
- external portfolio systems.

Because no material external integration dependency is approved, a Dependency Map is not required for Feature 001.

If a future feature approves an external system, the Dependency Map, Research, Architecture, security requirements, and affected contracts must be revisited before implementation.

---

# 10. Feature 001 Data Flow

The approved architectural flow is:

```text
1. User enters completed-trade information in the React UI
                      ↓
2. Frontend performs user-facing validation feedback
                      ↓
3. Frontend sends an approved request through the API client
                      ↓
4. Spring controller receives the request
                      ↓
5. Backend validation and application/service layer enforce rules
                      ↓
6. Domain/business logic calculates approved trade performance
                      ↓
7. Repository persists the approved trade representation
                      ↓
8. PostgreSQL stores the journal record
                      ↓
9. Backend returns the approved result through the REST boundary
                      ↓
10. Frontend presents the recorded trade and calculated performance
```

Backend behavior remains authoritative even when the frontend performs equivalent validation for user experience.

This flow does not determine the exact API schema or persistence schema.

---

# 11. Error Flow

Error handling must preserve the same architectural boundaries as successful execution.

```text
Frontend Input / API Request
            ↓
Controller / Validation Boundary
            ↓
Application / Domain Processing
            ↓
Persistence
```

Potential failure categories include:

- malformed or invalid user input;
- approved business-rule violations;
- persistence failures;
- unexpected backend failures;
- frontend/backend transport failures.

The architecture requires that:

- failures are not represented as successful HTTP outcomes;
- backend exceptions are not silently swallowed;
- stack traces and sensitive implementation details are not exposed to users;
- frontend error presentation is based on approved backend/API behavior.

The exact validation-error format, status codes, and global API error representation remain downstream Specification/OpenAPI decisions.

---

# 12. Security Boundary

Feature 001 does not include authentication or authorization.

The absence of authentication does not remove normal application security responsibilities.

The architecture requires:

- backend validation of untrusted input;
- safe error handling;
- no committed secrets or credentials;
- external configuration for database credentials and other sensitive configuration;
- no exposure of internal stack traces to the frontend;
- controlled frontend-to-backend communication through the REST API.

Authentication infrastructure must not be added preemptively.

If multi-user functionality is approved later, security architecture must be researched and specified before implementation.

---

# 13. Observability Boundary

Feature 001 requires simple, deliberate backend logging.

Backend logging should:

- use the application logging framework rather than ad hoc console output;
- provide useful context for failures and important application events;
- avoid exposing credentials or sensitive data.

Feature 001 does not justify introducing:

- distributed tracing;
- external metrics platforms;
- centralized logging infrastructure;
- monitoring agents;
- message brokers.

Those capabilities require future operational requirements.

---

# 14. Runtime Topology

The initial local development topology is:

```text
Developer Machine
│
├── Browser
│     ↓
├── React + TypeScript frontend
│   └── Vite development/build tooling
│
├── Spring Boot backend
│   └── Java 21
│
└── Docker
    └── PostgreSQL container
```

The frontend and backend do not need to be containerized for the initial implementation.

Docker is required initially only for the local PostgreSQL runtime.

Exact ports, environment-variable names, startup commands, Docker image version, and local orchestration instructions belong in implementation configuration and Quickstart.

No production deployment topology is approved yet.

---

# 15. Testing Boundaries

Testing follows architectural responsibility boundaries.

## 15.1 Backend

Backend testing should allow:

- direct testing of business calculations and domain logic;
- application/service tests;
- controller/API tests;
- persistence/integration tests where database behavior matters.

Tests should use the narrowest appropriate scope.

Database-sensitive behavior should use PostgreSQL-compatible integration testing when PostgreSQL behavior affects correctness.

The exact integration-test mechanism remains open.

## 15.2 Frontend

Frontend testing uses:

- Vitest;
- React Testing Library.

Tests should focus on user-observable behavior, including meaningful form interaction, validation feedback, and rendering of backend outcomes.

## 15.3 End-to-End Testing

No E2E framework is currently approved.

The absence of an E2E framework decision does not block the Feature 001 Architecture.

E2E tooling may be selected later if the Test Specification determines it is required.

---

# 16. Architectural Extensibility

The product is expected to grow, but Feature 001 must not implement future capabilities prematurely.

The architecture supports future growth primarily through stable responsibility boundaries:

```text
UI
↓
API Boundary
↓
Application / Domain Logic
↓
Persistence Boundary
```

Future features may extend these layers for:

- trade history;
- strategy/setup tracking;
- technical-indicator context;
- performance analysis;
- dashboards;
- market-data integration;
- brokerage integration;
- AI-assisted analysis;
- additional asset types.

No dedicated plugin system, asset hierarchy, integration framework, analytics service, or microservice boundary is approved for those possibilities today.

Such structures should be introduced only when a future approved feature provides a concrete requirement.

---

# 17. Architectural Risks

## AR-001 — Premature Abstraction

**Risk:**  
Future product ideas could lead to unnecessary generic trade hierarchies, integration interfaces, analytics infrastructure, or other abstractions during Feature 001.

**Mitigation:**  
Model only the approved Feature 001 concepts and rely on clean component boundaries for future evolution.

---

## AR-002 — Business Logic Drift into UI or Controllers

**Risk:**  
P&L calculations or business validation could be duplicated across React components and Spring controllers.

**Mitigation:**  
Keep backend application/domain logic authoritative. Frontend validation is for user experience only, and controllers delegate business behavior.

---

## AR-003 — Persistence Model Becomes Public API

**Risk:**  
Using JPA entities directly as API payloads could tightly couple the REST contract to database design.

**Mitigation:**  
Maintain a deliberate API DTO boundary and separate representations where responsibilities differ.

---

## AR-004 — Financial Numeric Errors

**Risk:**  
Inappropriate numeric types or rounding assumptions could produce incorrect financial results.

**Mitigation:**  
Do not freeze financial precision in Architecture. Require the Data Model to define precision, scale, representation, and rounding before persistence and contracts are finalized.

---

## AR-005 — PostgreSQL/Test Environment Divergence

**Risk:**  
Testing only against another database engine could hide PostgreSQL-specific behavior.

**Mitigation:**  
Use PostgreSQL-compatible integration testing where database-specific behavior affects correctness.

---

## AR-006 — API Contract Drift

**Risk:**  
Frontend and backend could develop incompatible request/response expectations.

**Mitigation:**  
Use explicit API-facing models, freeze behavior in Specification/OpenAPI, and require downstream contract consistency.

---

## AR-007 — Unnecessary Operational Complexity

**Risk:**  
Containerizing the entire application or introducing infrastructure services early could increase setup and debugging cost without helping Feature 001.

**Mitigation:**  
Containerize PostgreSQL only for the initial local environment and defer additional infrastructure until required.

---

# 18. Upstream Consistency Status

The previously identified Intended System / Trade Type mismatch has been reconciled.

The approved Feature 001 input set now consistently includes:

- ticker;
- trade type (`LONG` or `SHORT`);
- entry date;
- entry price;
- exit date;
- exit price;
- number of shares.

No unresolved upstream consistency issue remains in this area.

---

# 19. Remaining Architectural / Downstream Decisions

Some decisions intentionally delegated by Architecture have since been resolved by downstream artifacts.

## 19.1 Resolved Downstream

The following are now resolved by approved downstream artifacts:

- REST API versioning convention: path-based `/api/v1`;
- Feature 001 create endpoint: `POST /api/v1/trades`;
- request and response field sets;
- HTTP success and failure status behavior;
- validation and technical-error response structures;
- price precision: `NUMERIC(19,4)`;
- share precision: `NUMERIC(19,6)`;
- calculated performance values are derived rather than independently persisted;
- excess submitted price/share scale is rejected rather than silently rounded;
- Flyway owns versioned PostgreSQL schema creation and evolution;
- API dollar P&L uses 2 decimal places and API percentage return uses 4 decimal places;
- UI dollar P&L uses currency formatting with 2 decimal places and UI percentage return uses 2 decimal places.

These decisions remain owned by their authoritative downstream artifacts and are listed here only to reflect current project state.

## 19.2 Still Unresolved

The following remain intentionally unresolved:

- exact Java package structure;
- exact frontend directory/component structure;
- exact repository layout where not already established;
- exact PostgreSQL version;
- exact Spring Boot and library patch versions;
- database integration-test mechanism;
- E2E testing framework;
- production deployment target;
- full application containerization;
- future authentication design;
- future charting library;
- future market-data or brokerage providers;
- detailed future multi-asset architecture.

These items must remain unresolved until their authoritative downstream artifact or future feature requires a decision.

---

# 20. Architecture Decision Summary

For the current approved scope:

| Area | Architecture Decision |
|---|---|
| Application style | Responsive full-stack web application |
| Frontend | React + TypeScript |
| Frontend tooling | Vite |
| UI library | Material UI |
| Backend | Java 21 + Spring Boot |
| Backend structure | Layered controller → application/service → persistence architecture |
| Business logic | Backend application/domain layer |
| API | REST over HTTP |
| API models | Deliberate API-facing DTO boundary |
| Database | PostgreSQL |
| Persistence | Spring Data JPA / Hibernate |
| Local database runtime | Docker |
| Frontend database access | Prohibited |
| Authentication | Not included in Feature 001 |
| External integrations | None for Feature 001 |
| Charting | Not included |
| Backend tests | JUnit + Spring Boot Test |
| Frontend tests | Vitest + React Testing Library |
| Full-app containers | Deferred |
| Deployment | Deferred |

---

# 21. Artifact Relationships

## Upstream Inputs

This Architecture is derived from:

- `trading_journal_project_brief.md`
- `specs/001-record-trade/supporting/intended-system.md`
- `specs/001-record-trade/supporting/business-rules.md`
- `specs/001-record-trade/research.md`
- `.specify/memory/constitution.md`

A Dependency Map was intentionally not generated because Feature 001 currently has no approved material external integration dependency.

## Downstream Consumers

This Architecture must be consumed by:

- `specs/001-record-trade/data-model.md`
- `specs/001-record-trade/supporting/requirements.md`
- `specs/001-record-trade/spec.md`
- `specs/001-record-trade/plan.md`
- `specs/001-record-trade/tasks.md`
- `specs/001-record-trade/supporting/copilot-build-prompt.md`
- applicable test, traceability, review, and implementation artifacts

## Authority Boundary

This document is authoritative for:

- target system structure;
- component responsibilities;
- frontend/backend/persistence boundaries;
- interaction flow between major components;
- runtime topology;
- approved architectural constraints for Feature 001 and the current application baseline.

It is subject to the project Constitution.

It is **not** authoritative for:

- feature-specific observable behavior;
- business-rule meaning;
- database schema details;
- numeric precision and rounding;
- exact endpoint contracts;
- acceptance criteria;
- task sequencing;
- exact package names or repository folders.

Those responsibilities belong to their appropriate upstream or downstream artifacts.

## Conflict Handling

Architecture may not override the Constitution.

If Architecture conflicts with:

- `.specify/memory/constitution.md`, Architecture must be corrected or the Constitution must be formally amended;
- approved Business Rules, the conflict must be surfaced and reconciled rather than redefining the rule;
- Research technology decisions, Research must be updated through an approved decision or Architecture must be corrected;
- Intended System scope, the conflict must be surfaced before downstream artifacts are frozen.

Downstream artifacts must not silently reinterpret this Architecture.

---

# 22. Review Status

**Status:** Draft — ready for human review.

Once reviewed and accepted, this Architecture becomes authoritative upstream input for `data-model.md`.
