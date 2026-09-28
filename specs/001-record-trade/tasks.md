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
- [ ] T002 Confirm or establish the approved backend baseline: Java 21, Spring Boot, Spring Data JPA/Hibernate, JUnit, and Spring Boot Test; pin or explicitly constrain the Java runtime, Spring Boot, and backend dependency versions in repository-controlled build/configuration files without inventing versions before environment inspection.
- [ ] T003 Confirm or establish the approved frontend baseline: React, TypeScript, Vite, Material UI, Vitest, and React Testing Library; pin or explicitly constrain Node/package-manager and frontend dependency versions in repository-controlled build/configuration files without inventing versions before environment inspection.
- [ ] T004 Configure repository-controlled PostgreSQL through Docker using a pinned image/version and externalized database credentials.
- [ ] T005 Confirm the frontend and backend run directly for Feature 001 and are not unnecessarily containerized, and verify the pinned or constrained runtime/dependency configuration produces reproducible setup and builds from repository-controlled configuration.
- [ ] T006 Record and reconcile any repository or artifact conflict before implementation proceeds.

---

# Phase 2 — Domain and Persistence

- [ ] T007 [P] Implement `TradeType` with exactly `LONG` and `SHORT`.
- [ ] T008 Implement the Feature 001 `Trade` model with generated `Long` ID, ticker, trade type, entry/exit dates, entry/exit prices, and number of shares.
- [ ] T009 Configure persistence representations to match the Data Model: `BIGINT`, `DATE`, `NUMERIC(19,4)` for prices, and `NUMERIC(19,6)` for shares.
- [ ] T010 Ensure `dollarPnl` and `percentageReturn` remain derived values and are not persisted as authoritative columns.
- [ ] T011 Implement the minimum Spring Data repository needed to persist Feature 001 trades.
- [ ] T012 Ensure no uniqueness rule, duplicate-detection field, or speculative table prevents repeated valid submissions or introduces future concepts.
- [ ] T068 Configure Flyway as the authoritative PostgreSQL schema-management mechanism while retaining Spring Data JPA / Hibernate as the application persistence/ORM layer and disabling reliance on Hibernate automatic schema generation for authoritative schema creation or evolution.
- [ ] T069 Add the initial versioned Flyway migration that creates the approved Trade table with generated `BIGINT` ID, `DATE` fields, `NUMERIC(19,4)` prices, `NUMERIC(19,6)` shares, and no authoritative derived-performance columns or duplicate-prevention constraint.

---

# Phase 3 — Business Logic and Validation

- [ ] T013 Implement ticker normalization: trim surrounding whitespace, reject blank-after-trim input, and normalize accepted tickers to uppercase.
- [ ] T014 Implement trade-type validation so only `LONG` and `SHORT` are accepted.
- [ ] T015 Implement date validation: entry and exit dates required, `exitDate >= entryDate`, and same-day trades accepted.
- [ ] T016 Implement price validation: entry/exit prices greater than zero and no more than 4 fractional decimal places.
- [ ] T017 Implement share validation: quantity greater than zero, fractional shares allowed, and no more than 6 fractional decimal places.
- [ ] T018 Implement LONG dollar P&L and percentage-return calculations using `BigDecimal` without premature precision reduction; use intermediate division scale 10 with `HALF_UP` for percentage return.
- [ ] T019 Implement SHORT dollar P&L and percentage-return calculations using `BigDecimal` without premature precision reduction; use intermediate division scale 10 with `HALF_UP` for percentage return.
- [ ] T020 Preserve positive/profit, negative/loss, and zero/break-even semantics while keeping calculations independently testable.
- [ ] T021 Ensure excess input scale is rejected rather than silently rounded.

---

# Phase 4 — Service/Application Workflow

- [ ] T022 Implement the create-trade application/service workflow that coordinates validation, normalization, calculation, persistence, and result creation.
- [ ] T023 Ensure only authoritative trade facts are persisted while derived performance is returned in the application result.
- [ ] T024 Ensure repeated identical valid submissions create separate trades with distinct generated IDs.
- [ ] T025 Ensure persistence failure does not produce a successful application outcome.
- [ ] T026 Keep business rules out of controllers and persistence mechanics out of frontend code.
- [ ] T066 Implement application-framework logging for successful trade creation, application-level validation rejection where appropriate, and unexpected technical/persistence failure; allow safe context such as generated trade ID, normalized ticker, and high-level outcome/failure category while excluding credentials, secrets, tokens, database passwords, full request payloads, and unnecessary prices or share quantities.

---

# Phase 5 — API and Error Handling

- [ ] T027 [P] Create the request DTO for `ticker`, `tradeType`, `entryDate`, `entryPrice`, `exitDate`, `exitPrice`, and `numberOfShares`.
- [ ] T028 [P] Create the success response DTO containing persisted trade facts, generated ID, `dollarPnl` serialized to 2 decimal places using `HALF_UP`, and `percentageReturn` serialized to 4 decimal places using `HALF_UP`.
- [ ] T029 [P] Create validation and technical error DTOs using the approved `message` / `fieldErrors` structures.
- [ ] T030 Implement `POST /api/v1/trades`.
- [ ] T031 Return `201 Created` with normalized ticker, generated ID, persisted trade facts, and derived performance for valid submissions.
- [ ] T032 Return `400 Bad Request` for approved validation failures and ensure invalid trades are not persisted.
- [ ] T033 Return `500 Internal Server Error` for unexpected technical/persistence failures without exposing stack traces, credentials, or sensitive runtime details.
- [ ] T034 Ensure API models remain deliberate DTOs rather than exposing JPA entities directly.
- [ ] T035 Do not add unapproved GET, PUT, PATCH, DELETE, history, or other endpoints.

---

# Phase 6 — Backend Tests

- [ ] T036 [P] Add unit tests covering LONG/SHORT profit, loss, break-even, percentage-return calculations at intermediate division scale 10 with `HALF_UP`, and preservation of `BigDecimal` precision before output formatting.
- [ ] T037 [P] Add ticker normalization tests covering lowercase, surrounding whitespace, missing ticker, and whitespace-only ticker.
- [ ] T038 [P] Add trade-type and date-validation tests, including unsupported/missing type, missing dates, invalid order, and same-day acceptance.
- [ ] T039 [P] Add price/share validation tests covering zero, negative, fractional shares, and approved decimal-scale limits.
- [ ] T040 Add service tests for successful persistence, returned derived performance, duplicate submissions, and persistence failure.
- [ ] T041 Add controller/API tests for valid LONG/SHORT creation, `201`, approved response fields, normalized ticker, 2-decimal `dollarPnl` using `HALF_UP`, and 4-decimal `percentageReturn` using `HALF_UP`.
- [ ] T042 Add controller/API tests for `400` validation behavior and approved validation-error structure.
- [ ] T043 Add controller/API tests for `500` technical failure behavior and safe client-facing error output.
- [ ] T044 Add Testcontainers-based tests against a real PostgreSQL container for Flyway migration application, generated IDs, numeric precision/scale, duplicate records, and non-persisted derived performance; do not substitute H2 for PostgreSQL-sensitive behavior, and pin the Testcontainers dependency version during setup.
- [ ] T067 Verify required logging events are emitted with approved safe context; prohibited credentials, secrets, tokens, database passwords, full payloads, and unnecessary prices/share quantities are absent; internal exception details may remain backend-only and client responses expose no stack traces or internal runtime details.

---

# Phase 7 — Frontend API Boundary and UI

- [ ] T045 [P] Define typed TypeScript request, success-response, and error models matching the approved API contract.
- [ ] T046 Implement or extend the frontend API client for `POST /api/v1/trades` and keep HTTP calls isolated from unrelated components.
- [ ] T047 Build the Material UI trade-entry form with ticker, trade type, entry/exit dates, entry/exit prices, and number of shares, following `specs/shared/frontend/spec.md` for layout, spacing, responsive composition, labels, actions, accessibility, and visual consistency.
- [ ] T048 Implement user-facing validation for required fields, ticker rules, date order, positive numeric values, and approved decimal scales.
- [ ] T049 Submit valid trade data through the API client while keeping backend validation authoritative.
- [ ] T050 Present successful recorded-trade results using backend-authoritative values, including normalized ticker, generated ID, and dollar P&L and percentage return with exactly 2 fractional decimal places; do not require a currency symbol, locale, or grouping style unless established by the repository.
- [ ] T051 Present loading, backend validation, technical, transport, and success states consistently with `specs/shared/frontend/spec.md`, without false success, unnecessary layout movement, or sensitive backend details.
- [ ] T052 Do not introduce dashboard, chart, trade-history, strategy, indicator, authentication, or other future Feature 001 UI.

---

# Phase 8 — Frontend Tests

- [ ] T053 [P] Test the trade-entry form renders all approved fields, supports LONG/SHORT selection, remains usable at representative desktop and narrower widths, and preserves semantic labels, keyboard access, and visible focus behavior required by `specs/shared/frontend/spec.md`.
- [ ] T054 [P] Test user-facing validation for required fields, ticker whitespace, date order, numeric limits, and decimal-scale limits.
- [ ] T055 Test successful submission uses the approved request shape and renders dollar P&L and percentage return with exactly 2 fractional decimal places from backend-authoritative values without asserting a currency symbol, locale, or grouping style unless established by the repository.
- [ ] T056 Test loading, success, backend validation, and technical/transport failures produce stable, understandable user-visible states consistent with `specs/shared/frontend/spec.md`.
- [ ] T057 Keep frontend tests focused on user-observable behavior and shared accessibility/responsive/state-presentation outcomes rather than pixel-perfect styling or internal component implementation details.

---

# Phase 9 — Integration and Review

- [ ] T058 Run the full local stack: PostgreSQL in Docker, Spring Boot backend, and React/Vite frontend.
- [ ] T059 Verify the complete create-trade flow for valid LONG, valid SHORT, same-day trades, fractional shares, and ticker normalization.
- [ ] T060 Verify invalid date order, excess price scale, excess share scale, and other approved validation failures end-to-end.
- [ ] T061 Verify identical valid submissions create two persisted trades with distinct IDs.
- [ ] T062 Verify persistence failure does not produce a successful API or frontend state.
- [ ] T063 Verify no external brokerage, market-data, authentication, AI, or other unapproved capability is required.
- [ ] T064 Review implementation against Constitution, `specs/shared/frontend/spec.md`, Architecture, Data Model, Supporting Requirements, Specification, and Plan; reconcile any drift before approval while preserving Feature 001 behavior authority.
- [ ] T065 Validate and maintain the existing Quickstart and review evidence against verified startup, configuration, test, and implementation behavior; finalize corrections without regenerating the approved downstream artifact package.

---

# Dependencies

- T001–T006 before structural implementation decisions.
- T007–T012 before service and API persistence work.
- T068 before T069; both must complete before persistence/integration verification and before treating the schema as established.
- T013–T021 before T022–T026.
- T066 follows the application/service structure established by T022–T026 and must be complete before T067 and final integration review.
- T022–T026 before T030–T035.
- DTO tasks T027–T029 may run in parallel once the contract is understood.
- Backend tests may begin after the corresponding implementation exists.
- T067 follows T066 and may run with the corresponding backend verification work.
- Frontend tasks T045–T052 require the approved API contract but can overlap with backend implementation where dependencies permit.
- T058–T065 occur after backend and frontend implementation/test phases are sufficiently complete.

---

# Coverage Summary

| Area | Primary Tasks |
|---|---|
| Repository/environment | T001–T006 |
| Domain/persistence | T007–T012 |
| Flyway schema migrations | T068–T069 |
| Validation/calculations | T013–T021 |
| Service workflow | T022–T026 |
| API/error handling | T027–T035 |
| Backend tests | T036–T044 |
| Structured application logging | T066–T067 |
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

- exact PostgreSQL version until implementation setup;
- exact Spring Boot/library patch versions until implementation setup;
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
- `specs/shared/frontend/spec.md`

## Downstream Consumers

This Tasks artifact is consumed by:

- `speckit.analyze`;
- OpenAPI;
- Test Specification;
- Traceability Matrix;
- review checklists;
- Quickstart;
- Implementation Build Prompt;
- implementation and implementation review.

## Authority Boundary

This artifact is authoritative for the concrete implementation work breakdown and sequencing for Feature 001.

It is not authoritative for business-rule meaning, observable behavior, architecture, data-model decisions, API contract decisions, or project-wide governance.

## Conflict Handling

If a task conflicts with an approved upstream artifact, the task must be corrected.

Tasks must not introduce new product behavior, change approved contracts, or silently settle unresolved decisions.

---

# Review Status

**Status:** Human reviewed; not yet approved to drive implementation.

These Tasks and their downstream package artifacts have been generated and reviewed. Tasks remain pending final `/speckit-analyze` and human approval gates before becoming the authoritative implementation checklist. `/speckit-analyze` remains a pre-implementation gate.
