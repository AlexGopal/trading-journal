# Trading Journal and Analysis App — Supporting Requirements

## Executive Summary

This document defines the implementation obligations for Feature 001 of the Trading Journal and Analysis App.

Feature 001 allows a single user to manually record a completed stock trade, persist the trade, calculate approved performance measures, and receive the calculated result.

These requirements are derived from the approved Intended System, Business Rules, Research, Constitution, Architecture, and Data Model.

This document defines **what the implementation must satisfy**.

It does not define detailed user-story behavior, final endpoint contracts, implementation steps, or test procedures.

---

# 1. Business Context

The Trading Journal and Analysis App is intended to help an individual trader create a structured history of completed trades and understand trading performance over time.

Feature 001 establishes the first functional slice by supporting manual recording of completed stock trades and calculating:

- dollar profit or loss;
- percentage return.

The feature intentionally excludes future capabilities such as:

- strategy/setup tracking;
- technical-indicator tracking;
- dashboards;
- market-data integration;
- brokerage integration;
- AI-assisted analysis;
- authentication;
- additional asset types.

---

# 2. Scope

## 2.1 In Scope

Feature 001 must support:

- manual entry of completed stock trades;
- `LONG` and `SHORT` trade directions;
- ticker entry and uppercase normalization;
- entry and exit dates;
- entry and exit prices;
- fractional share quantities;
- approved date and numeric validation;
- dollar profit/loss calculation;
- percentage-return calculation;
- persistence of approved trade facts;
- presentation of the recorded trade and calculated performance through the application;
- a React + TypeScript frontend;
- a Spring Boot backend;
- REST communication between frontend and backend;
- PostgreSQL persistence.

## 2.2 Out of Scope

Feature 001 must not introduce:

- open positions;
- automatic trade import;
- brokerage synchronization;
- live market data;
- automatic technical-indicator calculation;
- MACD functionality;
- RSI functionality;
- moving-average functionality;
- dashboards or charts;
- additional asset types;
- multi-user authentication;
- authorization;
- AI-generated trading analysis;
- automated trading;
- commissions, fees, or dividend adjustments;
- multiple entries or exits for a single trade.

---

# 3. Functional Requirements

## FR-001 — Record a Completed Stock Trade

The system shall allow the user to record a completed stock trade using the approved Feature 001 trade data.

The trade shall contain:

- ticker;
- trade type;
- entry date;
- entry price;
- exit date;
- exit price;
- number of shares.

**Upstream basis:**
- Intended System
- BR-001
- BR-016
- BR-017
- Data Model

---

## FR-002 — Support Stock Trades Only

Feature 001 shall support stock trades only.

The implementation shall not introduce additional asset types.

**Upstream basis:**
- BR-001
- Intended System scope

---

## FR-003 — Support LONG and SHORT Trade Types

Every recorded trade shall identify its trade type as exactly one of:

```text
LONG
SHORT
```

No additional trade directions are approved for Feature 001.

**Upstream basis:**
- BR-002
- Data Model `TradeType`

---

## FR-004 — Require Ticker

Every recorded trade shall contain a non-blank ticker.

**Upstream basis:**
- BR-003

---

## FR-005 — Normalize Ticker to Uppercase

The system shall normalize the ticker to uppercase before it becomes the stored trading record.

Example:

```text
aapl → AAPL
```

The normalized value shall be used as the application's persisted ticker representation.

**Upstream basis:**
- BR-004
- Data Model

---

## FR-006 — Require Valid Entry Price

Every recorded trade shall contain an entry price greater than zero.

```text
entryPrice > 0
```

**Upstream basis:**
- BR-005

---

## FR-007 — Require Valid Exit Price

Every recorded trade shall contain an exit price greater than zero.

```text
exitPrice > 0
```

**Upstream basis:**
- BR-006

---

## FR-008 — Require Valid Share Quantity

Every recorded trade shall contain a number of shares greater than zero.

```text
numberOfShares > 0
```

Fractional shares shall be supported.

**Upstream basis:**
- BR-007

---

## FR-009 — Require Entry Date

Every recorded trade shall contain an entry date.

**Upstream basis:**
- BR-008

---

## FR-010 — Require Exit Date

Every recorded completed trade shall contain an exit date.

**Upstream basis:**
- BR-009
- BR-016

---

## FR-011 — Enforce Valid Date Sequence

The system shall reject a trade whose exit date occurs before its entry date.

The required relationship is:

```text
exitDate >= entryDate
```

Same-day trades shall be valid.

**Upstream basis:**
- BR-010

---

## FR-012 — Calculate LONG Dollar Profit/Loss

For a `LONG` trade, the system shall calculate dollar profit or loss as:

```text
(Exit Price - Entry Price) × Number of Shares
```

**Upstream basis:**
- BR-011

---

## FR-013 — Calculate SHORT Dollar Profit/Loss

For a `SHORT` trade, the system shall calculate dollar profit or loss as:

```text
(Entry Price - Exit Price) × Number of Shares
```

**Upstream basis:**
- BR-012

---

## FR-014 — Calculate LONG Percentage Return

For a `LONG` trade, the system shall calculate percentage return as:

```text
((Exit Price - Entry Price) / Entry Price) × 100
```

**Upstream basis:**
- BR-013

---

## FR-015 — Calculate SHORT Percentage Return

For a `SHORT` trade, the system shall calculate percentage return as:

```text
((Entry Price - Exit Price) / Entry Price) × 100
```

**Upstream basis:**
- BR-014

---

## FR-016 — Interpret Performance Result

The system shall preserve the approved interpretation of calculated performance:

- positive result → profit;
- negative result → loss;
- zero result → break-even.

**Upstream basis:**
- BR-015

---

## FR-017 — Persist Completed Trade Facts

The system shall persist approved Feature 001 trade facts in PostgreSQL.

The persisted trade facts shall include:

- generated trade identifier;
- ticker;
- trade type;
- entry date;
- entry price;
- exit date;
- exit price;
- number of shares.

**Upstream basis:**
- Architecture
- Data Model
- Constitution persistence standards

---

## FR-018 — Use Generated Trade Identifier

Each persisted trade shall receive a system-generated numeric identifier.

The application representation shall use:

```text
Long
```

and the PostgreSQL representation shall use:

```text
BIGINT
```

or the equivalent approved generated numeric identity mechanism.

**Upstream basis:**
- Data Model

---

## FR-019 — Derive Performance Rather Than Persist Duplicate Calculated State

Dollar P&L and percentage return shall be derived from the authoritative persisted trade facts.

They shall not be stored as independent authoritative database fields in Feature 001.

**Upstream basis:**
- Data Model
- Architecture

---

## FR-020 — Return/Present Calculated Performance

After successful trade processing, the application shall make the calculated:

- dollar P&L;
- percentage return

available to the frontend for presentation to the user.

Derived output shall preserve `BigDecimal` calculation precision until the approved output boundary. Percentage-return division shall use intermediate scale 10 with `HALF_UP` rounding. API dollar P&L shall use 2 decimal places with `HALF_UP`, and API percentage return shall use 4 decimal places with `HALF_UP`. The UI shall display both values with exactly 2 fractional decimal places.

These derived-output rules do not change approved input or persistence precision for prices or share quantities. The complete API response shape remains owned by Specification and OpenAPI.

Currency symbol, locale, and thousands-grouping style are non-contractual presentation choices unless already established by the repository.

**Upstream basis:**
- Intended System
- Architecture
- Data Model

---

## FR-021 — Manual Entry Only

Feature 001 shall accept trade information through manual user entry.

The implementation shall not import or synchronize trades from external systems.

**Upstream basis:**
- BR-017
- Intended System
- Research

---

## FR-022 — Completed Trades Only

Feature 001 shall not allow an open trade to be recorded as a completed Feature 001 trade.

Both entry and exit information are required.

**Upstream basis:**
- BR-016

---

## FR-023 — Allow Repeated Valid Manual Submissions

Feature 001 shall not perform automatic duplicate-trade detection.

If the same valid completed-trade information is submitted more than once, each successful submission shall create a separate persisted trade with its own generated identifier.

**Upstream basis:**
- BR-018

---

## FR-024 — Trim Ticker Whitespace Before Uppercase Normalization

The system shall remove leading and trailing whitespace from ticker input before final non-blank validation and uppercase normalization.

Example:

```text
"  aapl  " → "AAPL"
```

If trimming produces an empty ticker, the trade shall be rejected.

**Upstream basis:**
- BR-003
- BR-004
- BR-019

---

# 4. Data Requirements

## DR-001 — Trade Entity

Feature 001 shall use a single primary persisted trade entity consistent with `data-model.md`.

The implementation shall not create speculative Feature 001 tables for future concepts such as:

- strategies;
- indicators;
- portfolios;
- brokerage accounts;
- users;
- asset hierarchies.

---

## DR-002 — Date Representation

Entry and exit dates shall use date-only representations.

Approved representations are:

```text
Java: LocalDate
PostgreSQL: DATE
```

Feature 001 shall not introduce time-of-day or time-zone semantics.

---

## DR-003 — Price Representation

Entry and exit prices shall use decimal-safe values.

Approved representations are:

```text
Java: BigDecimal
PostgreSQL: NUMERIC(19,4)
```

Binary floating-point types shall not be used for persisted or authoritative price values.

---

## DR-004 — Share Quantity Representation

Number of shares shall use decimal-safe values.

Approved representations are:

```text
Java: BigDecimal
PostgreSQL: NUMERIC(19,6)
```

This representation must support fractional shares.

---

## DR-005 — Derived Performance Representation

Dollar P&L and percentage return shall use `BigDecimal` or an equivalent exact decimal representation in backend calculation logic.

They shall not be converted to binary floating-point values for authoritative business calculations.

---

## DR-006 — Ticker Representation

Ticker shall be represented as text and persisted in uppercase normalized form.

Maximum length and detailed allowed-character rules are not yet approved.

The implementation shall not invent them silently.

---

## DR-007 — Reject Input Beyond Approved Persisted Scale

Feature 001 shall not silently round trade input that exceeds the approved persisted scale.

The application shall reject:

- `entryPrice` or `exitPrice` values with more than 4 fractional decimal places;
- `numberOfShares` values with more than 6 fractional decimal places.

This rule applies to submitted authoritative trade facts. It does not define user-visible rounding for derived P&L or percentage return.

---

# 5. Architecture Requirements

## ARQ-001 — Separate Frontend, Backend, and Persistence Responsibilities

The application shall maintain clear responsibility boundaries between:

- React frontend;
- Spring Boot backend;
- PostgreSQL persistence.

The frontend shall not access PostgreSQL directly.

---

## ARQ-002 — Layer Backend Responsibilities

Backend responsibilities shall be separated by concern.

At minimum:

- controllers handle HTTP/API concerns;
- service/application components coordinate business behavior;
- repositories handle persistence access;
- domain/model structures represent approved data concepts.

Business logic shall not be implemented directly in controllers.

---

## ARQ-003 — Backend Business Logic Is Authoritative

Approved business calculations, normalization, and validation shall be enforced by the backend.

Frontend validation may duplicate selected checks for user experience but shall not become the authoritative acceptance mechanism.

---

## ARQ-004 — Use API-Facing Models Deliberately

The implementation shall maintain a deliberate API boundary.

JPA persistence entities shall not automatically become public API contracts merely for convenience.

Exact request and response DTOs remain downstream contract decisions.

---

## ARQ-005 — Keep Feature 001 Architecturally Simple

The implementation shall not introduce:

- microservices;
- message brokers;
- caches;
- plugin systems;
- distributed tracing infrastructure;
- speculative integration adapters;
- generic multi-asset hierarchies

unless a later approved requirement justifies them.

---

# 6. API Requirements

## API-001 — REST over HTTP

Frontend/backend communication shall use REST over HTTP.

---

## API-002 — Contract Consistency

Once the Feature 001 Specification and OpenAPI contract are approved, implementation shall remain consistent with them.

The implementation shall not silently change paths, HTTP methods, request fields, response fields, status codes, validation behavior, or error behavior.

---

## API-003 — Versioned API Prefix

Feature 001 shall use path-based API versioning:

```text
/api/v1
```

---

## API-004 — Create Trade Endpoint

Feature 001 shall expose trade creation through:

```text
POST /api/v1/trades
```

No additional trade endpoint is required by Feature 001.

---

## API-005 — Successful Creation Status

A successfully recorded trade shall return:

```text
201 Created
```

The response body shall contain the authoritative recorded trade facts, generated identifier, dollar P&L, and percentage return.

---

## API-006 — Validation Failure Status

A request that violates approved Feature 001 validation rules shall return:

```text
400 Bad Request
```

The trade shall not be persisted.

---

## API-007 — Technical Failure Status

An unexpected server or persistence failure that prevents successful completion shall return:

```text
500 Internal Server Error
```

The response shall not expose stack traces, credentials, or sensitive runtime details.

---

## API-008 — Request Contract

The create-trade request shall contain:

```text
ticker
tradeType
entryDate
entryPrice
exitDate
exitPrice
numberOfShares
```

Dates shall use ISO local-date form `YYYY-MM-DD`.

---

## API-009 — Success Response Contract

The successful create-trade response shall contain:

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

---

## API-010 — Validation Error Contract

A validation failure response shall contain:

```text
message
fieldErrors
```

`fieldErrors` shall associate invalid request fields with user-meaningful validation messages.

---

## API-011 — Technical Error Contract

A technical failure response shall contain:

```text
message
```

The message shall be safe for client exposure and shall not reveal internal stack traces or sensitive runtime details.

---

# 7. Frontend Requirements

## FE-001 — React + TypeScript

The Feature 001 frontend shall use React with TypeScript.

---

## FE-002 — Vite

The frontend shall use Vite for development/build tooling.

---

## FE-003 — Material UI

Material UI shall be the primary frontend component library.

A competing primary component framework shall not be introduced.

Feature 001 shall follow `specs/shared/frontend/spec.md` for shared layout, spacing, typography, Material UI composition, forms, actions, responsive behavior, accessibility, and loading/success/error presentation. Feature-specific behavior remains governed by Feature 001 artifacts.

---

## FE-004 — User-Facing Validation Feedback

The frontend shall present useful validation feedback for approved Feature 001 input rules.

Frontend validation does not replace backend validation.

---

## FE-005 — API Client Boundary

Frontend API communication shall be isolated behind a deliberate API-client boundary rather than scattered through unrelated UI components.

---

## FE-006 — Typed Frontend Models

Known application and API data structures shall use explicit TypeScript types.

Use of `any` shall not be used as a shortcut around approved known structures.

---

# 8. Backend Requirements

## BE-001 — Java 21

The backend shall use Java 21 LTS as the approved baseline.

---

## BE-002 — Spring Boot

The backend shall use Spring Boot.

---

## BE-003 — Spring Data JPA / Hibernate

Standard relational persistence shall use Spring Data JPA / Hibernate.

This does not prohibit future explicit SQL or projections when later requirements justify them.

---

## BE-004 — Calculation Logic Must Be Independently Testable

Feature 001 performance calculations shall be testable independently from:

- browser/UI execution;
- a running HTTP server;
- persistence infrastructure.

---

# 9. Persistence Requirements

## PER-001 — PostgreSQL Primary Database

Persistent application data shall use PostgreSQL.

---

## PER-002 — PostgreSQL Local Runtime

Local PostgreSQL shall run through Docker using repository-controlled configuration.

---

## PER-003 — No Permanent Reliance on Automatic Schema Generation

Automatic ORM schema generation shall not become the permanent schema-management mechanism.

Explicit, reviewable Flyway migrations shall be the authoritative PostgreSQL schema-management mechanism.

---

## PER-004 — Use Flyway for Schema Migrations

Feature 001 shall use Flyway for PostgreSQL schema creation and evolution while retaining Spring Data JPA / Hibernate as the application persistence/ORM layer.

Hibernate automatic schema generation shall not be authoritative. The initial implementation shall include a versioned migration for the approved Trade table.

---

# 10. Testing Requirements

## TEST-001 — Automated Testing Required

Feature 001 shall include automated tests appropriate to its behavior and risk.

---

## TEST-002 — Backend Test Stack

Backend testing shall use:

- JUnit;
- Spring Boot Test.

---

## TEST-003 — Business Calculation Tests

Automated tests shall verify approved:

- LONG dollar P&L;
- SHORT dollar P&L;
- LONG percentage return;
- SHORT percentage return;
- profit/loss/break-even outcomes;
- preservation of `BigDecimal` calculation precision until the approved output boundary;
- API serialization of dollar P&L at 2 decimal places and percentage return at 4 decimal places.

---

## TEST-004 — Validation Tests

Automated tests shall cover approved validation rules including:

- ticker required;
- ticker surrounding whitespace trimmed before final non-blank validation and uppercase normalization;
- whitespace-only ticker rejected after trimming;
- entry price > 0;
- exit price > 0;
- shares > 0;
- fractional shares accepted;
- price values with more than 4 fractional decimal places rejected;
- share quantities with more than 6 fractional decimal places rejected;
- entry date required;
- exit date required;
- exit date cannot precede entry date;
- same-day trades accepted;
- trade type restricted to LONG/SHORT.

---

## TEST-005 — Appropriate Test Scope

Tests should use the narrowest appropriate scope.

The complete Spring context should not be loaded for every test when a narrower scope is sufficient.

---

## TEST-006 — PostgreSQL-Sensitive Integration Testing

PostgreSQL-sensitive persistence and Flyway integration tests shall run against a real PostgreSQL container through Testcontainers rather than substituting H2 where PostgreSQL behavior affects correctness. Tests shall verify that Flyway applies the initial versioned Trade-table migration successfully. The exact Testcontainers dependency version shall be selected and pinned during implementation setup.

---

## TEST-007 — Frontend Test Stack

Frontend testing shall use:

- Vitest;
- React Testing Library.

---

## TEST-008 — Frontend Tests Focus on User-Observable Behavior

Frontend tests shall focus on meaningful user-observable behavior including:

- form interaction;
- validation feedback;
- success states;
- error states;
- dollar P&L currency display at 2 decimal places;
- percentage-return display at 2 decimal places.

Tests shall avoid depending unnecessarily on component implementation details.

---

## TEST-009 — E2E Framework Not Yet Required

No E2E testing framework is approved by this artifact.

If the downstream Test Specification determines one is required, the tool shall be selected through the appropriate decision process.

---

## TEST-010 — Duplicate Submission Behavior

Automated tests shall verify that submitting the same valid completed-trade information more than once:

- does not trigger automatic duplicate rejection;
- creates a separately persisted trade for each successful submission;
- assigns a distinct generated identifier to each persisted trade.

**Upstream basis:**
- BR-018
- FR-023

---

# 11. Security Requirements

## SEC-001 — No Authentication for Feature 001

Feature 001 shall not implement authentication or authorization.

The absence of authentication is a current scope decision, not a permanent product rule.

---

## SEC-002 — Validate Untrusted Input

The backend shall validate untrusted Feature 001 input before accepting it as a valid trade.

---

## SEC-003 — Do Not Expose Internal Failure Details

The application shall not expose backend stack traces, secrets, or sensitive runtime details to end users.

---

## SEC-004 — No Secrets in Source Control

Credentials, passwords, tokens, and other secrets shall not be committed to source control.

---

## SEC-005 — Externalize Sensitive Configuration

Database credentials and other sensitive local/runtime configuration shall use an appropriate external configuration mechanism.

---

# 12. Operational Requirements

## OP-001 — Reproducible Local Database Environment

The local PostgreSQL environment shall be reproducible across development machines through Docker configuration stored with the project.

---

## OP-002 — Frontend and Backend Need Not Be Containerized Initially

Feature 001 shall not require frontend or backend containerization.

Full-app containerization remains deferred.

---

## OP-003 — No Production Deployment Target Required Yet

Feature 001 shall remain runnable as a maintainable local application.

No cloud provider or production hosting platform shall be selected merely to complete Feature 001.

---

## OP-004 — Deliberate Logging

Backend application code shall use deliberate application logging rather than ad hoc console output.

Logs shall record successful trade creation, application-level validation rejection where appropriate, and unexpected technical/persistence failure.

Safe context may include the generated trade ID after persistence, normalized ticker where useful, and a high-level outcome or failure category. Logs shall not contain credentials, secrets, tokens, database passwords, full request payloads, or unnecessary entry/exit prices or share quantities. Internal exception details may be logged when appropriate for diagnosis, but stack traces and internal runtime details shall not be exposed in client responses.

---

## OP-005 — No Unnecessary Operational Infrastructure

Feature 001 shall not introduce monitoring platforms, distributed tracing, message brokers, caches, or similar infrastructure without an approved requirement.

---

# 13. Constitution-Derived Requirements

## CR-001 — Follow Spec-Driven Development

Implementation shall be based on reviewed and approved SDD artifacts.

Undocumented assumptions shall not override approved artifacts.

---

## CR-002 — Preserve Approved Scope

Implementation shall not add future-facing functionality simply because it may be useful later.

---

## CR-003 — Maintainability Over Cleverness

Implementation shall favor:

- clear responsibilities;
- simple control flow;
- predictable conventions;
- testability;
- maintainable abstractions.

Premature optimization and speculative design are prohibited unless justified by an approved requirement.

---

## CR-004 — Type Safety

Java and TypeScript code shall use domain-appropriate types.

Finite concepts such as trade type shall not use uncontrolled string representations where an approved finite domain exists.

---

## CR-005 — No Silent Artifact Drift

If implementation reveals a conflict or missing requirement, the appropriate artifact shall be reconciled before implementation continues.

Implementation shall not silently diverge from frozen documentation.

---

## CR-006 — Repository-First AI Implementation

Before implementation, Codex or another AI implementation agent shall inspect the existing repository structure and approved artifacts.

It shall reuse existing project structure rather than scaffolding duplicate frontend/backend applications.

---

## CR-007 — Human Review Required

Architecture, Specification, Plan/Tasks, and completed implementation shall pass the required human review gates before being treated as approved.

Review Profiles should supplement, not replace, human judgment.

---

# 14. Compatibility and Integration Requirements

## INT-001 — No External Market or Brokerage Integration

Feature 001 shall not depend on:

- market-data providers;
- brokerages;
- trading platforms;
- authentication providers;
- AI services.

---

## INT-002 — Manual Data Is Authoritative for Feature 001

Trade information entered by the user is the approved Feature 001 input source.

No external service is required to validate ticker existence or retrieve prices.

---

## INT-003 — Future Integrations Require New Approval

Any future external integration shall require appropriate updates to:

- Dependency Map;
- Research;
- Architecture;
- security requirements;
- contracts;
- affected feature artifacts.

---

# 15. Non-Functional Requirements

## NFR-001 — Maintainability

The implementation shall preserve clear separation of responsibilities and avoid unnecessary duplication of business logic.

---

## NFR-002 — Extensibility Without Premature Design

The implementation should remain reasonably extensible for future capabilities while avoiding speculative abstractions for features that are not yet approved.

---

## NFR-003 — Reproducibility

Repository-controlled configuration should support consistent development behavior across machines.

---

## NFR-004 — Correct Financial Representation

Financial and quantity calculations shall use exact decimal representations appropriate to the domain.

Binary floating-point arithmetic shall not be used for authoritative financial calculations.

---

## NFR-005 — Contract Clarity

Frontend/backend interaction shall use explicit typed contracts once Specification/OpenAPI are finalized.

---

## NFR-006 — Testability

Business logic shall be structured so that approved calculations and validation can be tested without depending on unrelated infrastructure.

---

# 16. Assumptions

The current approved assumptions are:

- the application is single-user;
- the initial user is an individual retail trader;
- trades are entered manually;
- Feature 001 supports completed stock trades only;
- the application is a responsive web application;
- desktop-friendly usability is expected;
- PostgreSQL is available locally through Docker;
- no external integrations are required;
- no authentication is required;
- future capabilities are not automatically part of Feature 001.

---

# 17. Risks

## RQ-RISK-001 — Numeric and Display Rounding Drift

Derived output precision can drift if calculation, API serialization, and UI formatting apply inconsistent scales or reduce precision prematurely.

**Handling:**  
Keep authoritative calculations in `BigDecimal`, use intermediate division scale 10 with `HALF_UP`, serialize API dollar P&L to 2 decimal places with `HALF_UP` and percentage return to 4 decimal places with `HALF_UP`, and display both in the UI with exactly 2 fractional decimal places.

---

## RQ-RISK-002 — Premature Future Capability Design

The broad product roadmap may encourage speculative abstractions.

**Handling:**  
Requirements explicitly prohibit implementation outside approved Feature 001 scope.

---

## RQ-RISK-003 — API Contract Drift

Frontend and backend may diverge if request/response structures are implemented before Specification/OpenAPI are frozen.

**Handling:**  
Contract details must remain downstream decisions and later be reconciled through Specification/OpenAPI.

---

## RQ-RISK-004 — Database/Test Mismatch

Using H2 for all persistence tests could hide PostgreSQL-specific behavior.

**Handling:**  
Use Testcontainers with a real PostgreSQL container when behavior depends materially on PostgreSQL; do not substitute H2 for those tests.

---

# 18. Remaining Open Decisions

The following remain unresolved and must not be silently invented:

- maximum ticker length;
- ticker character restrictions;
- exact PostgreSQL version;
- exact Spring Boot/library patch versions;
- E2E testing framework;
- production deployment target;
- full application containerization;
- future authentication design;
- future multi-asset model;
- future market-data or brokerage integrations.

---

# 19. Requirement Dependencies

The principal dependency flow is:

```text
Intended System
      ↓
Business Rules
      ↓
Research
      ↓
Constitution
      ↓
Architecture
      ↓
Data Model
      ↓
Supporting Requirements
      ↓
Specification
```

Functional requirements depend primarily on Business Rules and Data Model.

Architecture, security, testing, persistence, and operational requirements depend primarily on Constitution, Research, and Architecture.

The Specification must consume these requirements without redefining them.

---

# 20. Requirement Traceability Summary

| Requirement Area | Primary Upstream Authority |
|---|---|
| Trade scope | Intended System / BR-001 / BR-016 / BR-017 |
| Trade direction | BR-002 |
| Ticker | BR-003 / BR-004 |
| Prices | BR-005 / BR-006 |
| Shares | BR-007 |
| Dates | BR-008 / BR-009 / BR-010 |
| LONG P&L | BR-011 |
| SHORT P&L | BR-012 |
| LONG return | BR-013 |
| SHORT return | BR-014 |
| Profit/loss interpretation | BR-015 |
| Persistence representation | Data Model |
| Layered architecture | Constitution / Architecture |
| REST | Research / Constitution |
| PostgreSQL | Research / Constitution / Architecture |
| JPA/Hibernate | Research / Constitution |
| Testing | Constitution / Research |
| Security | Constitution |
| No external integrations | Intended System / Research |
| No authentication | Intended System / Research / Constitution |
| Duplicate submission behavior | BR-018 |
| Ticker whitespace normalization | BR-019 |

---

# 21. Artifact Relationships

## Upstream Inputs

This artifact is derived from:

- `trading_journal_project_brief.md`
- `specs/001-record-trade/supporting/intended-system.md`
- `specs/001-record-trade/supporting/business-rules.md`
- `specs/001-record-trade/research.md`
- `.specify/memory/constitution.md`
- `specs/001-record-trade/supporting/architecture.md`
- `specs/001-record-trade/data-model.md`
- `specs/shared/frontend/spec.md`

## Downstream Consumers

This artifact must be consumed by:

- `specs/001-record-trade/spec.md`
- `specs/001-record-trade/plan.md`
- `specs/001-record-trade/tasks.md`
- `specs/001-record-trade/contracts/openapi.yaml`
- `specs/001-record-trade/supporting/test-spec.md`
- `specs/001-record-trade/supporting/traceability-matrix.md`
- `specs/001-record-trade/checklists/requirements.md`
- `specs/001-record-trade/supporting/implementation-build-prompt.md`
- implementation and review activities

## Authority Boundary

This document is authoritative for Feature 001 implementation obligations.

It is subject to:

- project-wide Constitution governance;
- approved Business Rules;
- Intended System scope;
- approved Architecture;
- approved Data Model;
- the shared frontend specification for cross-feature presentation and interaction conventions.

It is not authoritative for:

- detailed user-story behavior;
- final endpoint contracts;
- implementation sequencing;
- test procedures;
- UI design details;
- task breakdown.

Specification remains authoritative for observable feature behavior.

## Conflict Handling

Supporting Requirements may not contradict:

- Constitution;
- approved Business Rules;
- Intended System;
- Architecture;
- Data Model.

If a conflict is discovered, it must be surfaced and reconciled before Specification is frozen.

Downstream artifacts must not silently weaken or redefine these requirements.

---

# 22. Review Status

**Status:** Draft — ready for human review.

Once reviewed and accepted, this artifact becomes authoritative upstream input for `spec.md`.
