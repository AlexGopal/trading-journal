# Trading Journal and Analysis App — Feature 001 Implementation Plan

## Plan Purpose

This Plan defines the implementation approach for Feature 001: recording a completed stock trade and calculating its performance.

It translates the approved Feature 001 artifacts into an ordered implementation strategy without replacing the authoritative behavior, data, architecture, or contract decisions established upstream.

This Plan does not define individual implementation tasks. Those belong in `tasks.md`.

---

# 1. Feature Goal

Feature 001 must deliver one complete vertical slice:

```text
User enters completed stock trade
        ↓
React + TypeScript frontend validates for user experience
        ↓
POST /api/v1/trades
        ↓
Spring Boot backend validates authoritative rules
        ↓
Backend normalizes ticker
        ↓
Backend calculates LONG/SHORT performance
        ↓
Spring Data JPA persists authoritative trade facts
        ↓
PostgreSQL stores the trade
        ↓
Backend returns recorded trade + derived performance
        ↓
Frontend presents success or approved error feedback
```

The implementation must remain limited to the approved Feature 001 scope.

---

# 2. Implementation Principles

Implementation must follow these constraints:

1. Preserve the approved Spec-Driven Development chain.
2. Do not introduce behavior not present in approved artifacts.
3. Keep the first feature as a focused vertical slice.
4. Keep backend validation and business calculations authoritative.
5. Keep controllers free of business logic.
6. Keep JPA entities from becoming accidental API contracts.
7. Use exact decimal types for authoritative financial values.
8. Avoid future-facing abstractions that Feature 001 does not require.
9. Add automated tests alongside implementation.
10. Stop and reconcile artifacts if implementation reveals a conflict.

---

# 3. Approved Technical Baseline

Feature 001 uses:

| Area | Approved Choice |
|---|---|
| Backend language | Java 21 LTS |
| Backend framework | Spring Boot |
| Frontend | React |
| Frontend language | TypeScript |
| Frontend tooling | Vite |
| UI library | Material UI |
| API style | REST over HTTP |
| API versioning | Path-based `/api/v1` |
| Create endpoint | `POST /api/v1/trades` |
| Database | PostgreSQL |
| Persistence | Spring Data JPA / Hibernate |
| Local DB runtime | Docker |
| Backend testing | JUnit + Spring Boot Test |
| Frontend testing | Vitest + React Testing Library |
| Authentication | Not included |
| External integrations | None |
| Charting | Not included |

Exact framework/library patch versions must be pinned during implementation setup.

---

# 4. Feature 001 Data Contract

## 4.1 User-Supplied Trade Data

The Feature 001 request contains:

```text
ticker
tradeType
entryDate
entryPrice
exitDate
exitPrice
numberOfShares
```

## 4.2 Core Data Types

| Field | Java / Domain Representation | Persistence Representation |
|---|---|---|
| id | `Long` | `BIGINT` generated identity |
| ticker | `String` | text |
| tradeType | `TradeType` enum | symbolic LONG/SHORT representation |
| entryDate | `LocalDate` | `DATE` |
| entryPrice | `BigDecimal` | `NUMERIC(19,4)` |
| exitDate | `LocalDate` | `DATE` |
| exitPrice | `BigDecimal` | `NUMERIC(19,4)` |
| numberOfShares | `BigDecimal` | `NUMERIC(19,6)` |
| dollarPnl | `BigDecimal` derived | not persisted; API scale 2; UI currency scale 2 |
| percentageReturn | `BigDecimal` derived | not persisted; API scale 4; UI display scale 2 |

## 4.3 Persistence Model

Feature 001 uses one primary persisted concept:

```text
Trade
```

No additional tables are required for:

- users;
- strategies;
- indicators;
- portfolios;
- brokerage accounts;
- asset hierarchies;
- calculated P&L;
- calculated percentage return.

---

# 5. Approved Business Behavior

Implementation must preserve the following behavior.

## 5.1 Ticker

The backend must:

1. accept ticker input;
2. trim leading/trailing whitespace;
3. reject the value if it is blank after trimming;
4. normalize the ticker to uppercase;
5. persist and return the normalized ticker.

Example:

```text
"  aapl  "
→ trim
"aapl"
→ uppercase
"AAPL"
```

No external ticker-existence validation is required.

## 5.2 Trade Type

Only:

```text
LONG
SHORT
```

are valid.

## 5.3 Dates

- entry date is required;
- exit date is required;
- `exitDate >= entryDate`;
- same-day trades are valid;
- dates use date-only semantics.

## 5.4 Prices

- entry price > 0;
- exit price > 0;
- each supports at most 4 fractional decimal places;
- excess scale is rejected rather than silently rounded.

## 5.5 Shares

- number of shares > 0;
- fractional shares are allowed;
- up to 6 fractional decimal places are supported;
- excess scale is rejected rather than silently rounded.

## 5.6 Duplicate Manual Submissions

Feature 001 does not perform automatic duplicate detection.

If the same valid trade information is submitted multiple times, each successful submission creates a separate `Trade` with its own generated identifier.

---

# 6. Performance Calculation Plan

Performance calculations belong in backend business/application logic and must be independently testable.

## 6.1 LONG Dollar P&L

```text
(Exit Price - Entry Price) × Number of Shares
```

## 6.2 SHORT Dollar P&L

```text
(Entry Price - Exit Price) × Number of Shares
```

## 6.3 LONG Percentage Return

```text
((Exit Price - Entry Price) / Entry Price) × 100
```

## 6.4 SHORT Percentage Return

```text
((Entry Price - Exit Price) / Entry Price) × 100
```

## 6.5 Interpretation

```text
positive → profit
negative → loss
zero     → break-even
```

`BigDecimal` must be used for authoritative calculations.

Percentage-return division uses intermediate scale 10 with `HALF_UP` rounding. Intermediate results must not be reduced to final presentation scale prematurely. Final API scale conversion uses `HALF_UP`: 2 decimal places for `dollarPnl` and 4 decimal places for `percentageReturn`.

Derived P&L and percentage return are not persisted as independent authoritative fields.

---

# 7. Backend Implementation Plan

## 7.1 Establish Backend Application Structure

Use the repository's existing structure after inspection.

The logical responsibilities must include:

```text
API / Controller Boundary
        ↓
Application / Service Layer
        ↓
Domain / Business Logic
        ↓
Repository / Persistence Boundary
        ↓
PostgreSQL
```

Exact package names and class names must follow repository conventions discovered during implementation.

Do not scaffold duplicate backend applications if one already exists.

---

## 7.2 Implement Domain Representation

Implement the approved Feature 001 concepts:

```text
Trade
TradeType
```

Use domain-appropriate Java types:

- `Long`;
- `String`;
- `TradeType`;
- `LocalDate`;
- `BigDecimal`.

The model must not contain speculative future fields.

---

## 7.3 Implement Persistence Representation

Implement persistence for the approved Trade facts:

```text
id
ticker
tradeType
entryDate
entryPrice
exitDate
exitPrice
numberOfShares
```

Requirements:

- PostgreSQL is authoritative;
- Spring Data JPA / Hibernate is the baseline;
- price columns must preserve `NUMERIC(19,4)`;
- share quantity must preserve `NUMERIC(19,6)`;
- derived performance values must not become persisted authoritative columns;
- no uniqueness constraint should reject otherwise-valid duplicate manual submissions.

Use Flyway as the authoritative PostgreSQL schema-management mechanism. Configure Flyway and add an initial versioned migration that creates the approved Trade table.

Spring Data JPA / Hibernate remains the application persistence/ORM layer. Hibernate automatic schema generation must not create or evolve the authoritative schema.

---

## 7.4 Implement Repository Boundary

Implement the minimum repository behavior required to persist Feature 001 trades.

Do not introduce:

- speculative analytics queries;
- history filtering;
- strategy queries;
- indicator queries;
- brokerage queries.

Only repository behavior needed for the approved create workflow should be added.

---

## 7.5 Implement Business/Application Logic

Implement an application/service operation that coordinates trade creation.

The logical sequence is:

```text
receive request data
    ↓
validate approved rules
    ↓
trim ticker
    ↓
normalize ticker uppercase
    ↓
calculate P&L
    ↓
calculate percentage return
    ↓
persist Trade
    ↓
build application/API result
```

Business calculations and domain validation must remain outside controllers.

The operation must fail without reporting success if persistence fails.

---

## 7.6 Implement API DTO Boundary

Create deliberate request and response representations rather than exposing persistence entities directly.

### Request fields

```text
ticker
tradeType
entryDate
entryPrice
exitDate
exitPrice
numberOfShares
```

### Success response fields

```text
id
ticker
tradeType
entryDate
entryPrice
exitDate
exitPrice
numberOfShares
dollarPnl
percentageReturn
```

Derived response values must be produced from authoritative `BigDecimal` calculations without premature precision reduction. Serialize `dollarPnl` to 2 decimal places and `percentageReturn` to 4 decimal places.

Dates use:

```text
YYYY-MM-DD
```

---

## 7.7 Implement REST Controller

Expose:

```text
POST /api/v1/trades
```

Approved behavior:

### Successful creation

```text
201 Created
```

### Validation failure

```text
400 Bad Request
```

### Unexpected technical/persistence failure

```text
500 Internal Server Error
```

Controllers must:

- bind HTTP input;
- trigger validation;
- delegate application behavior;
- map outcomes to approved HTTP responses.

Controllers must not contain P&L formulas or persistence logic.

---

## 7.8 Implement Error Handling

Validation failure response:

```text
message
fieldErrors
```

Technical failure response:

```text
message
```

Error handling must:

- avoid success semantics for failure;
- avoid exposing stack traces;
- avoid exposing credentials or sensitive runtime details;
- produce client-safe messages.

Exact human-readable validation and technical-error wording is intentionally non-contractual. Messages must remain understandable, while tests and clients rely on status codes and approved response structures rather than verbatim prose.

## 7.9 Implement Application Logging

Backend trade-creation and failure paths must use structured, intentional application logging rather than ad hoc console output.

Required events are successful trade creation, application-level validation rejection where appropriate, and unexpected technical/persistence failure. Safe context may include generated trade ID after persistence, normalized ticker where useful, and a high-level outcome or failure category.

Logs must not contain credentials, secrets, tokens, database passwords, full request payloads, or unnecessary entry/exit prices or share quantities. Internal exception details may be logged when appropriate for diagnosis, but stack traces and internal runtime details must not be exposed in client responses.

---

# 8. Frontend Implementation Plan

## 8.1 Establish Feature 001 UI

Build a focused trade-entry experience using React, TypeScript, Vite, and Material UI.

Follow `specs/shared/frontend/spec.md` for intentional Material UI composition, responsive layout, form and action patterns, loading/success/error states, accessibility, spacing, typography, and cross-feature visual consistency. Apply those conventions without changing Feature 001 fields, validation, contract, calculations, or acceptance behavior.

The form must collect:

- ticker;
- trade type;
- entry date;
- entry price;
- exit date;
- exit price;
- number of shares.

Do not introduce dashboard, charting, history-analysis, strategy, indicator, or authentication UI.

---

## 8.2 Frontend Types

Define explicit TypeScript types for approved request/response structures.

Avoid `any` for known Feature 001 structures.

Frontend representations should remain consistent with the REST contract.

---

## 8.3 Frontend Validation

Frontend validation may mirror approved backend rules to improve user experience.

It should support feedback for:

- required ticker;
- whitespace-only ticker;
- LONG/SHORT trade type;
- required dates;
- date sequence;
- prices > 0;
- shares > 0;
- excess price decimal scale;
- excess share decimal scale.

Backend validation remains authoritative.

---

## 8.4 API Client Boundary

Implement Feature 001 API communication through a deliberate API client boundary.

The frontend should not scatter direct HTTP calls through unrelated UI components.

The API client must call:

```text
POST /api/v1/trades
```

and expose successful and failed outcomes to frontend feature logic.

---

## 8.5 Success Presentation

After successful creation, the frontend must present the recorded trade and calculated:

- dollar P&L;
- percentage return.

The result must use the backend's authoritative normalized ticker and calculated values.

Display dollar P&L and percentage return with exactly 2 fractional decimal places. Currency symbol, locale, and thousands-grouping style are presentation choices, not Feature 001 contract requirements unless already established by the repository. UI formatting must not alter backend-authoritative calculated values or input/persistence precision.

---

## 8.6 Error Presentation

The frontend must present useful user-facing feedback for:

- validation failures;
- technical/backend failures;
- transport failures where applicable.

It must not display backend stack traces or sensitive internal information.

---

# 9. Testing Plan

Testing must be implemented alongside the feature rather than deferred until the end.

## 9.1 Backend Unit Tests

Directly test business calculations and normalization without requiring:

- browser execution;
- HTTP server;
- PostgreSQL.

Cover:

- profitable LONG;
- losing LONG;
- profitable SHORT;
- losing SHORT;
- break-even;
- preservation of `BigDecimal` calculation precision before output-scale conversion;
- ticker trimming;
- uppercase normalization.

---

## 9.2 Backend Validation Tests

Cover:

- missing ticker;
- blank ticker;
- whitespace-only ticker;
- ticker trimming;
- missing trade type;
- unsupported trade type;
- missing entry date;
- missing exit date;
- exit date before entry date;
- same-day trade accepted;
- entry price zero;
- entry price negative;
- exit price zero;
- exit price negative;
- shares zero;
- shares negative;
- fractional shares accepted;
- price scale > 4 rejected;
- share scale > 6 rejected.

---

## 9.3 Service/Application Tests

Verify the create operation:

- applies normalization;
- selects the correct LONG/SHORT calculation;
- persists authoritative trade facts;
- returns derived performance;
- does not persist derived P&L/percentage return as independent authoritative state;
- permits repeated identical valid submissions as separate trades.

---

## 9.4 Controller/API Tests

Verify:

```text
POST /api/v1/trades
```

including:

- `201 Created` success;
- approved response fields;
- normalized ticker in response;
- `dollarPnl` serialized to 2 decimal places;
- `percentageReturn` serialized to 4 decimal places;
- `400 Bad Request` for validation failure;
- approved validation error shape;
- `500 Internal Server Error` behavior where technically testable at the controller boundary;
- no sensitive internal details in errors.

---

## 9.5 Persistence / Integration Tests

Use Testcontainers with a real PostgreSQL container where persistence or Flyway correctness depends on PostgreSQL behavior. H2 must not substitute for those tests. Select and pin the exact Testcontainers dependency version during implementation setup.

Verify:

- Trade persistence;
- successful application of the initial versioned Flyway Trade-table migration;
- generated numeric ID;
- `NUMERIC(19,4)` price compatibility;
- `NUMERIC(19,6)` share compatibility;
- separate IDs for repeated valid submissions;
- no accidental persistence of derived performance fields.

---

## 9.6 Frontend Tests

Use Vitest + React Testing Library.

Focus on user-observable behavior:

- form rendering;
- input interaction;
- client-side validation feedback;
- successful submission state;
- calculated result presentation, including dollar P&L as currency with 2 decimal places and percentage return with 2 decimal places;
- backend validation error presentation;
- technical failure presentation.

Do not over-test internal component implementation details.

---

# 10. Local Runtime Plan

The expected initial development topology is:

```text
Browser
  ↓
React + TypeScript / Vite
  ↓
Spring Boot / Java 21
  ↓
PostgreSQL in Docker
```

Only PostgreSQL must be containerized initially.

Frontend and backend containerization remain deferred.

Repository-controlled Docker configuration must pin the PostgreSQL image/version when implementation configuration is established.

---

# 11. Implementation Sequence

Implementation should proceed in this order.

## Phase 1 — Repository Inspection and Setup

1. inspect existing repository structure;
2. identify existing backend/frontend modules;
3. inspect build/configuration files;
4. pin approved runtime/dependency versions;
5. establish reproducible PostgreSQL Docker configuration if not already present;
6. avoid duplicate scaffolding.

## Phase 2 — Backend Domain and Persistence

1. implement `TradeType`;
2. implement Trade data representation;
3. configure Flyway while retaining JPA/Hibernate as the application persistence layer;
4. add the initial versioned Trade-table migration;
5. implement persistence mapping;
6. implement repository;
7. verify PostgreSQL representation and migration application.

## Phase 3 — Backend Business Logic

1. implement validation;
2. implement ticker trim/uppercase normalization;
3. implement LONG calculations;
4. implement SHORT calculations;
5. implement result interpretation;
6. implement duplicate-submission behavior implicitly through normal create semantics;
7. add unit/service tests.

## Phase 4 — Backend API

1. implement request/response DTOs;
2. implement create service orchestration;
3. implement `POST /api/v1/trades`;
4. implement approved HTTP status behavior;
5. implement validation/technical error handling;
6. implement intentional application logging for trade-creation outcomes and failure paths;
7. add controller/API and logging verification tests.

## Phase 5 — Frontend

1. define typed API models;
2. implement API client;
3. build trade-entry form;
4. implement user-facing validation;
5. submit to backend;
6. present successful result;
7. present validation/technical failures;
8. add frontend tests.

## Phase 6 — Integration Verification

1. run backend tests;
2. run frontend tests;
3. run PostgreSQL-sensitive integration tests;
4. manually exercise the full create flow;
5. verify API/frontend contract consistency;
6. verify no future-scope capability was introduced.

---

# 12. Verification Scenarios

At minimum, implementation verification must cover these end-to-end behavioral scenarios:

### Scenario A — Valid LONG

```text
AAPL
LONG
Entry 220
Exit 230
10 shares
```

Expected:

```text
Dollar P&L = 100
Percentage Return ≈ 4.5454...
```

### Scenario B — Valid SHORT

```text
AAPL
SHORT
Entry 100
Exit 90
10 shares
```

Expected:

```text
Dollar P&L = 100
Percentage Return = 10
```

### Scenario C — Same-Day Trade

```text
entryDate = exitDate
```

Expected: accepted.

### Scenario D — Invalid Date Order

```text
exitDate < entryDate
```

Expected: rejected.

### Scenario E — Ticker Normalization

```text
"  aapl  "
```

Expected:

```text
AAPL
```

### Scenario F — Fractional Shares

```text
0.75 shares
```

Expected: accepted.

### Scenario G — Excess Price Precision

```text
100.12345
```

Expected: rejected.

### Scenario H — Excess Share Precision

```text
1.1234567
```

Expected: rejected.

### Scenario I — Duplicate Submission

Submit the same valid trade twice.

Expected:

```text
two persisted trades
two distinct generated IDs
```

### Scenario J — Persistence Failure

Expected:

- no successful creation response;
- no false success state in frontend;
- safe technical error behavior.

---

# 13. Explicit Non-Goals During Implementation

Do not implement:

- GET trade-history endpoints;
- update endpoints;
- delete endpoints;
- open positions;
- strategy/setup tracking;
- technical indicators;
- dashboards;
- charting;
- market-data retrieval;
- brokerage synchronization;
- authentication;
- multi-user support;
- AI-assisted analysis;
- automated trading;
- commissions/fees/dividends;
- multiple entries/exits;
- generalized asset hierarchies;
- microservices;
- message brokers;
- caching infrastructure;
- distributed tracing.

These require future approved artifacts.

---

# 14. Remaining Decisions

The following remain intentionally open and must not be silently invented:

- exact PostgreSQL version;
- exact Spring Boot/library patch versions;
- E2E framework;
- production deployment target;
- full application containerization;
- future authentication design;
- future charting library;
- future market-data/brokerage providers;
- future multi-asset architecture;
- maximum ticker length;
- detailed ticker character restrictions.

If any unresolved item becomes necessary to complete Feature 001 implementation, the appropriate authoritative artifact must be updated before implementation proceeds.

---

# 15. Plan Completion Criteria

This Plan is considered ready to drive `tasks.md` when:

- scope matches the approved Specification;
- architecture responsibilities are preserved;
- approved Data Model decisions are reflected;
- API behavior matches the Specification;
- required testing boundaries are represented;
- future capabilities remain excluded;
- unresolved decisions are explicitly preserved;
- no undocumented implementation behavior has been introduced.

---

# 16. Artifact Relationships

## Upstream Inputs

This Plan is derived from:

- `trading_journal_project_brief.md`
- `specs/001-record-trade/supporting/intended-system.md`
- `specs/001-record-trade/supporting/business-rules.md`
- `specs/001-record-trade/research.md`
- `.specify/memory/constitution.md`
- `specs/001-record-trade/supporting/architecture.md`
- `specs/001-record-trade/data-model.md`
- `specs/001-record-trade/supporting/requirements.md`
- `specs/001-record-trade/spec.md`
- `specs/shared/frontend/spec.md`

## Downstream Consumers

This Plan must be consumed by:

- `specs/001-record-trade/tasks.md`
- `specs/001-record-trade/contracts/openapi.yaml`
- `specs/001-record-trade/supporting/test-spec.md`
- `specs/001-record-trade/supporting/traceability-matrix.md`
- review checklists;
- Quickstart;
- Implementation Build Prompt;
- implementation and review activities.

## Authority Boundary

This Plan is authoritative for:

- implementation approach;
- implementation phases;
- sequencing of major work;
- responsibility boundaries during implementation;
- planned verification approach.

It is not authoritative for:

- business-rule meaning;
- observable feature behavior;
- final API contract details beyond what the Specification already approves;
- Data Model decisions;
- project-wide governance;
- individual task-level work items.

## Conflict Handling

If this Plan conflicts with:

- Constitution;
- Business Rules;
- Architecture;
- Data Model;
- Supporting Requirements;
- Specification;

the Plan must be corrected before Tasks are generated.

The Plan must not silently reinterpret approved upstream artifacts.

---

# 17. Review Status

**Status:** Human reviewed and approved as authoritative input to `tasks.md`.

The Plan was generated for review, reconciled against the approved Specification and other upstream artifacts, and then approved to drive Tasks development. Remaining decisions explicitly listed in Section 14 stay unresolved and must be settled through the appropriate authoritative artifacts before dependent implementation or downstream artifact generation proceeds.
