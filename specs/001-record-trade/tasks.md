# Trading Journal and Analysis App — Feature 001 Tasks

## Purpose

This artifact defines the concrete implementation work for Feature 001: recording a completed stock trade and calculating its performance.

Tasks are derived from the approved Research, Constitution, Architecture, Data Model, Supporting Requirements, Specification, and Implementation Plan.

The tasks must not redefine approved behavior or introduce future capabilities.

---

## Format

```text
- [ ] T### [P?] Description
```

- `[P]` means the task may be completed in parallel when dependencies allow.
- Tasks should remain meaningful implementation units rather than individual coding actions.
- Implementation agents must inspect and reuse the existing repository structure before creating new modules, packages, folders, or applications.

---

# Phase 1 — Repository and Environment Setup

- [ ] T001 Inspect the existing repository structure, backend/frontend modules, build files, package/folder conventions, tests, and configuration before modifying code.
- [ ] T002 Confirm or establish the approved backend baseline: Java 21, Spring Boot, Spring Data JPA/Hibernate, JUnit, and Spring Boot Test.
- [ ] T003 Confirm or establish the approved frontend baseline: React, TypeScript, Vite, Material UI, Vitest, and React Testing Library.
- [ ] T004 Configure repository-controlled PostgreSQL through Docker using a pinned image/version and externalized database credentials.
- [ ] T005 Confirm the frontend and backend run directly for Feature 001 and are not unnecessarily containerized.
- [ ] T006 Record and reconcile any repository or artifact conflict before implementation proceeds.

---

# Phase 2 — Domain and Persistence

- [ ] T007 [P] Implement `TradeType` with exactly `LONG` and `SHORT`.
- [ ] T008 Implement the Feature 001 `Trade` model with generated `Long` ID, ticker, trade type, entry/exit dates, entry/exit prices, and number of shares.
- [ ] T009 Configure persistence representations to match the Data Model: `BIGINT`, `DATE`, `NUMERIC(19,4)` for prices, and `NUMERIC(19,6)` for shares.
- [ ] T010 Ensure `dollarPnl` and `percentageReturn` remain derived values and are not persisted as authoritative columns.
- [ ] T011 Implement the minimum Spring Data repository needed to persist Feature 001 trades.
- [ ] T012 Ensure no uniqueness rule, duplicate-detection field, or speculative table prevents repeated valid submissions or introduces future concepts.

---

# Phase 3 — Business Logic and Validation

- [ ] T013 Implement ticker normalization: trim surrounding whitespace, reject blank-after-trim input, and normalize accepted tickers to uppercase.
- [ ] T014 Implement trade-type validation so only `LONG` and `SHORT` are accepted.
- [ ] T015 Implement date validation: entry and exit dates required, `exitDate >= entryDate`, and same-day trades accepted.
- [ ] T016 Implement price validation: entry/exit prices greater than zero and no more than 4 fractional decimal places.
- [ ] T017 Implement share validation: quantity greater than zero, fractional shares allowed, and no more than 6 fractional decimal places.
- [ ] T018 Implement LONG dollar P&L and percentage-return calculations using `BigDecimal`.
- [ ] T019 Implement SHORT dollar P&L and percentage-return calculations using `BigDecimal`.
- [ ] T020 Preserve positive/profit, negative/loss, and zero/break-even semantics while keeping calculations independently testable.
- [ ] T021 Ensure excess input scale is rejected rather than silently rounded.

---

# Phase 4 — Service/Application Workflow

- [ ] T022 Implement the create-trade application/service workflow that coordinates validation, normalization, calculation, persistence, and result creation.
- [ ] T023 Ensure only authoritative trade facts are persisted while derived performance is returned in the application result.
- [ ] T024 Ensure repeated identical valid submissions create separate trades with distinct generated IDs.
- [ ] T025 Ensure persistence failure does not produce a successful application outcome.
- [ ] T026 Keep business rules out of controllers and persistence mechanics out of frontend code.

---

# Phase 5 — API and Error Handling

- [ ] T027 [P] Create the request DTO for `ticker`, `tradeType`, `entryDate`, `entryPrice`, `exitDate`, `exitPrice`, and `numberOfShares`.
- [ ] T028 [P] Create the success response DTO containing persisted trade facts, generated ID, `dollarPnl`, and `percentageReturn`.
- [ ] T029 [P] Create validation and technical error DTOs using the approved `message` / `fieldErrors` structures.
- [ ] T030 Implement `POST /api/v1/trades`.
- [ ] T031 Return `201 Created` with normalized ticker, generated ID, persisted trade facts, and derived performance for valid submissions.
- [ ] T032 Return `400 Bad Request` for approved validation failures and ensure invalid trades are not persisted.
- [ ] T033 Return `500 Internal Server Error` for unexpected technical/persistence failures without exposing stack traces, credentials, or sensitive runtime details.
- [ ] T034 Ensure API models remain deliberate DTOs rather than exposing JPA entities directly.
- [ ] T035 Do not add unapproved GET, PUT, PATCH, DELETE, history, or other endpoints.

---

# Phase 6 — Backend Tests

- [ ] T036 [P] Add unit tests covering LONG/SHORT profit, loss, break-even, and percentage-return calculations.
- [ ] T037 [P] Add ticker normalization tests covering lowercase, surrounding whitespace, missing ticker, and whitespace-only ticker.
- [ ] T038 [P] Add trade-type and date-validation tests, including unsupported/missing type, missing dates, invalid order, and same-day acceptance.
- [ ] T039 [P] Add price/share validation tests covering zero, negative, fractional shares, and approved decimal-scale limits.
- [ ] T040 Add service tests for successful persistence, returned derived performance, duplicate submissions, and persistence failure.
- [ ] T041 Add controller/API tests for valid LONG/SHORT creation, `201`, approved response fields, and normalized ticker.
- [ ] T042 Add controller/API tests for `400` validation behavior and approved validation-error structure.
- [ ] T043 Add controller/API tests for `500` technical failure behavior and safe client-facing error output.
- [ ] T044 Add PostgreSQL-compatible persistence/integration tests for generated IDs, numeric precision/scale, duplicate records, and non-persisted derived performance.

---

# Phase 7 — Frontend API Boundary and UI

- [ ] T045 [P] Define typed TypeScript request, success-response, and error models matching the approved API contract.
- [ ] T046 Implement or extend the frontend API client for `POST /api/v1/trades` and keep HTTP calls isolated from unrelated components.
- [ ] T047 Build the Material UI trade-entry form with ticker, trade type, entry/exit dates, entry/exit prices, and number of shares.
- [ ] T048 Implement user-facing validation for required fields, ticker rules, date order, positive numeric values, and approved decimal scales.
- [ ] T049 Submit valid trade data through the API client while keeping backend validation authoritative.
- [ ] T050 Present successful recorded-trade results using backend-authoritative values, including normalized ticker, generated ID, dollar P&L, and percentage return.
- [ ] T051 Present backend validation, technical, and transport failures without showing false success or sensitive backend details.
- [ ] T052 Do not introduce dashboard, chart, trade-history, strategy, indicator, authentication, or other future Feature 001 UI.

---

# Phase 8 — Frontend Tests

- [ ] T053 [P] Test the trade-entry form renders all approved fields and supports LONG/SHORT selection.
- [ ] T054 [P] Test user-facing validation for required fields, ticker whitespace, date order, numeric limits, and decimal-scale limits.
- [ ] T055 Test successful submission uses the approved request shape and renders the recorded trade and calculated performance.
- [ ] T056 Test backend validation and technical/transport failures produce appropriate user-visible error states.
- [ ] T057 Keep frontend tests focused on user-observable behavior rather than internal component implementation details.

---

# Phase 9 — Integration and Review

- [ ] T058 Run the full local stack: PostgreSQL in Docker, Spring Boot backend, and React/Vite frontend.
- [ ] T059 Verify the complete create-trade flow for valid LONG, valid SHORT, same-day trades, fractional shares, and ticker normalization.
- [ ] T060 Verify invalid date order, excess price scale, excess share scale, and other approved validation failures end-to-end.
- [ ] T061 Verify identical valid submissions create two persisted trades with distinct IDs.
- [ ] T062 Verify persistence failure does not produce a successful API or frontend state.
- [ ] T063 Verify no external brokerage, market-data, authentication, AI, or other unapproved capability is required.
- [ ] T064 Review implementation against Constitution, Architecture, Data Model, Supporting Requirements, Specification, and Plan; reconcile any drift before approval.
- [ ] T065 Capture startup/configuration details needed for Quickstart and prepare Spec/Plan/Tasks for `speckit.analyze`.

---

# Dependencies

- T001–T006 before structural implementation decisions.
- T007–T012 before service and API persistence work.
- T013–T021 before T022–T026.
- T022–T026 before T030–T035.
- DTO tasks T027–T029 may run in parallel once the contract is understood.
- Backend tests may begin after the corresponding implementation exists.
- Frontend tasks T045–T052 require the approved API contract but can overlap with backend implementation where dependencies permit.
- T058–T065 occur after backend and frontend implementation/test phases are sufficiently complete.

---

# Coverage Summary

| Area | Primary Tasks |
|---|---|
| Repository/environment | T001–T006 |
| Domain/persistence | T007–T012 |
| Validation/calculations | T013–T021 |
| Service workflow | T022–T026 |
| API/error handling | T027–T035 |
| Backend tests | T036–T044 |
| Frontend/API client/UI | T045–T052 |
| Frontend tests | T053–T057 |
| Integration/review | T058–T065 |

---

# Explicitly Excluded Work

Feature 001 must not add:

- open positions;
- trade editing or deletion;
- trade-history browsing;
- strategy/setup tracking;
- MACD, RSI, or moving averages;
- charts or dashboards;
- market-data or brokerage integration;
- authentication or multi-user support;
- AI analysis or automated trading;
- fees, commissions, dividends, or multiple entries/exits;
- generic multi-asset abstractions;
- microservices, message brokers, caches, or distributed tracing.

---

# Open Decisions Tasks Must Not Resolve Silently

The following remain intentionally open:

- exact user-visible/API rounding for derived dollar P&L;
- exact user-visible/API rounding for derived percentage return;
- exact human-readable validation/error wording;
- Flyway versus Liquibase;
- exact PostgreSQL version until implementation setup;
- exact Spring Boot/library patch versions until implementation setup;
- exact PostgreSQL integration-test mechanism;
- E2E framework;
- production deployment target;
- full application containerization;
- maximum ticker length;
- detailed ticker character restrictions;
- future authentication, charting, integration, and multi-asset design.

If an open decision becomes necessary to complete a task, reconcile the appropriate authoritative artifact before proceeding.

---

# Artifact Relationships

## Upstream Inputs

This Tasks artifact is derived from:

- `trading_journal_project_brief.md`
- `specs/001-record-trade/supporting/intended-system.md`
- `specs/001-record-trade/supporting/business-rules.md`
- `specs/001-record-trade/research.md`
- `.specify/memory/constitution.md`
- `specs/001-record-trade/supporting/architecture.md`
- `specs/001-record-trade/data-model.md`
- `specs/001-record-trade/supporting/requirements.md`
- `specs/001-record-trade/spec.md`
- `specs/001-record-trade/plan.md`

## Downstream Consumers

This Tasks artifact is consumed by:

- `speckit.analyze`;
- OpenAPI;
- Test Specification;
- Traceability Matrix;
- review checklists;
- Quickstart;
- Copilot/AI Build Prompt;
- implementation and implementation review.

## Authority Boundary

This artifact is authoritative for the concrete implementation work breakdown and sequencing for Feature 001.

It is not authoritative for business-rule meaning, observable behavior, architecture, data-model decisions, API contract decisions, or project-wide governance.

## Conflict Handling

If a task conflicts with an approved upstream artifact, the task must be corrected.

Tasks must not introduce new product behavior, change approved contracts, or silently settle unresolved decisions.

---

# Review Status

**Status:** Draft — ready for `speckit.analyze` and human review.

Material findings from analysis must be resolved before these tasks are treated as the authoritative implementation checklist.
