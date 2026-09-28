# Implementation Build Prompt - 001 Record Completed Trade

## Mission

Implement only Feature 001 of the Trading Journal and Analysis App: manually record a completed stock trade, persist the approved trade facts, and calculate/display trade performance.

Do not begin implementation until the pre-implementation package has passed the final `/speckit-analyze` and Requirements Checklist gate.

---

## Authority

Read the approved artifacts before modifying code.

Use these as the implementation authority, within their respective boundaries:

- `.specify/memory/constitution.md`
- `specs/shared/frontend/spec.md`
- `supporting/intended-system.md`
- `supporting/business-rules.md`
- `research.md`
- `supporting/architecture.md`
- `data-model.md`
- `supporting/requirements.md`
- `spec.md`
- `plan.md`
- `tasks.md`
- `contracts/openapi.yaml`
- `supporting/test-spec.md`
- `supporting/traceability-matrix.md`
- `checklists/requirements.md`
- `checklists/code-review-checklist.md`
- `checklists/qa-review-checklist.md`
- `quickstart.md`

If approved artifacts materially contradict each other, stop and report the conflict. Do not silently choose a side or redefine behavior.

Use `specs/shared/frontend/spec.md` as authority for shared frontend presentation and interaction conventions. Feature 001 Specification, Business Rules, Requirements, and OpenAPI remain authoritative for feature-specific behavior and contracts. The shared frontend specification cannot override Feature 001 business rules, API behavior, calculations, validation, precision, or acceptance criteria.

---

## Repository-First Rules

Before coding:

- inspect the existing repository structure, build files, packages/folders, tests, configuration, and Docker setup;
- reuse existing frontend/backend structure rather than scaffolding duplicates;
- follow repository naming and organization conventions;
- pin or explicitly constrain required runtime/dependency versions in repository-controlled configuration;
- do not invent paths, ports, commands, classes, or package names when repository evidence can determine them.

If repository reality conflicts with an approved architecture or requirement, stop and report it.

---

## Approved Technology

Use the approved stack:

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- React
- TypeScript
- Vite
- Material UI
- JUnit + Spring Boot Test
- Vitest + React Testing Library
- PostgreSQL locally through Docker

Frontend and backend do not need to be containerized for Feature 001.

---

## Feature Rules

Support completed stock trades only.

Input:

- `ticker`
- `tradeType` = `LONG` or `SHORT`
- `entryDate`
- `entryPrice`
- `exitDate`
- `exitPrice`
- `numberOfShares`

Required behavior:

- trim ticker whitespace;
- reject blank ticker;
- normalize accepted ticker to uppercase;
- require valid LONG/SHORT trade type;
- require entry/exit dates;
- accept same-day trades;
- reject `exitDate < entryDate`;
- require prices > 0;
- require shares > 0;
- allow fractional shares;
- reject prices with more than 4 fractional decimal places;
- reject shares with more than 6 fractional decimal places;
- reject excess input scale rather than silently rounding;
- allow identical valid submissions as separate trades with distinct generated IDs.

Do not invent ticker-existence validation, ticker length limits, or ticker character restrictions.

---

## Calculations

Use `BigDecimal` for authoritative calculations.

LONG:

```text
dollarPnl = (exitPrice - entryPrice) × numberOfShares
percentageReturn = ((exitPrice - entryPrice) / entryPrice) × 100
```

SHORT:

```text
dollarPnl = (entryPrice - exitPrice) × numberOfShares
percentageReturn = ((entryPrice - exitPrice) / entryPrice) × 100
```

Preserve:

- positive = profit;
- negative = loss;
- zero = break-even;
- sufficient intermediate precision;
- percentage-return division at intermediate scale 10 using `HALF_UP`;
- no authoritative `float`/`double` conversion.

Output:

- API `dollarPnl`: 2 decimal places using `HALF_UP`;
- API `percentageReturn`: 4 decimal places using `HALF_UP`;
- UI dollar P&L and percentage return: exactly 2 fractional decimal places;
- currency symbol, locale, and grouping style: non-contractual unless established by the repository.

Derived performance values are not authoritative persisted Trade fields.

---

## Architecture and Persistence

Preserve the approved layering:

```text
React frontend
→ REST API/controller
→ service/application layer
→ repository/persistence layer
→ PostgreSQL
```

Rules:

- controller stays thin;
- service/application layer owns workflow, validation coordination, normalization, calculations, persistence coordination, and result creation;
- frontend contains no persistence logic;
- JPA entities are not exposed directly as API contracts;
- use deliberate request/response DTOs.

Persistence:

- generated `BIGINT` / `Long` ID;
- dates stored as `DATE`;
- prices as `NUMERIC(19,4)`;
- shares as `NUMERIC(19,6)`;
- no authoritative P&L or percentage-return columns;
- no uniqueness rule preventing duplicate submissions.

Flyway is the authoritative schema-migration mechanism. JPA/Hibernate is the ORM/persistence layer, not the authoritative schema creator.

---

## API Contract

Implement exactly:

```text
POST /api/v1/trades
```

Request fields:

```text
ticker
tradeType
entryDate
entryPrice
exitDate
exitPrice
numberOfShares
```

Success response:

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

Status behavior:

- `201 Created` on success;
- `400 Bad Request` for approved validation failures;
- `500 Internal Server Error` for unexpected technical/persistence failures.

Validation errors use:

```text
message
fieldErrors
```

Technical errors use:

```text
message
```

Exact human-readable error wording is intentionally non-contractual. Preserve understandable content, approved status codes and structures, field association, and client safety without testing verbatim prose.

Do not add unapproved GET, PUT, PATCH, DELETE, history, or convenience endpoints.

Implementation must conform to `contracts/openapi.yaml`.

---

## Frontend Rules

Build the Feature 001 form using the approved frontend stack.

- follow `specs/shared/frontend/spec.md` for Material UI composition, responsive layout, spacing, typography, forms, actions, accessibility, and loading/success/error presentation;
- include only the approved trade-entry fields;
- keep HTTP calls behind an API-client boundary;
- frontend validation may assist the user, but backend validation remains authoritative;
- display backend-authoritative success values;
- display dollar P&L and percentage return using approved UI formatting;
- failed requests must not appear successful.

---

## Logging and Security

- use the backend logging framework rather than ad hoc console output;
- log successful trade creation, appropriate application-level validation rejection, and unexpected technical/persistence failure;
- safe log context may include generated trade ID, normalized ticker, and high-level outcome/failure category;
- do not log credentials, secrets, tokens, database passwords, full request payloads, or unnecessary prices/share quantities;
- internal exception details may be logged for diagnosis, but client responses must not expose stack traces or runtime internals;
- externalize database credentials and other sensitive configuration;
- do not add authentication or authorization for Feature 001.

---

## Implementation Sequence

Follow `tasks.md` as the authoritative work breakdown.

High-level order:

1. repository/environment setup;
2. domain, persistence, and Flyway;
3. validation and calculations;
4. service/application workflow and logging;
5. API/error handling;
6. backend tests;
7. frontend/API client/UI;
8. frontend tests;
9. integration and review evidence.

Preserve existing task IDs and dependencies.

---

## Verification

Use `supporting/test-spec.md` and `tasks.md`.

Verify at minimum:

- LONG/SHORT profit, loss, break-even, and percentage calculations;
- ticker normalization;
- date/price/share validation;
- decimal-scale rejection;
- same-day trades;
- fractional shares;
- duplicate submissions;
- persistence failure behavior;
- 201/400/500 API behavior;
- API response/error schemas;
- API/UI precision rules;
- Flyway migration and PostgreSQL mappings;
- non-persisted derived values;
- logging safety;
- frontend success/error behavior;
- OpenAPI contract conformance.

Test incrementally rather than waiting until the end.

Do not report planned tests as passed.

---

## Prohibited Work

Do not add:

- open positions;
- edit/delete;
- trade-history browsing;
- strategies/setups;
- indicators;
- charts/dashboards;
- live market data;
- brokerage integration;
- authentication/multi-user support;
- AI analysis;
- automated trading;
- fees/commissions/dividends;
- multiple entries/exits;
- generic multi-asset abstractions;
- microservices;
- message brokers;
- caches;
- distributed tracing.

Do not add speculative abstractions solely for future features.

---

## Open Decisions

Do not silently resolve:

- exact PostgreSQL version until environment setup;
- exact Spring Boot/library patch versions until environment setup;
- E2E framework;
- production deployment;
- full application containerization;
- ticker maximum length;
- detailed ticker character restrictions;
- future authentication, charting, integrations, or multi-asset design.

If one becomes necessary, stop the affected work and reconcile the appropriate artifact first.

---

## Stop Conditions

Stop and report before continuing if:

- approved artifacts materially conflict;
- repository structure conflicts with approved architecture;
- implementation requires unapproved product behavior;
- OpenAPI conflicts with the Specification;
- persistence design conflicts with the Data Model;
- a task requires violating the Constitution;
- an unapproved external dependency becomes necessary.

Do not silently change frozen artifacts to make implementation easier.

---

## Review and Completion

After implementation:

- complete `checklists/code-review-checklist.md` with evidence;
- resolve material code-review findings;
- execute `checklists/qa-review-checklist.md` using real runtime/test evidence;
- keep failed or blocked checks visible;
- update `supporting/traceability-matrix.md` with implementation evidence where appropriate;
- finalize `quickstart.md` with verified commands, ports, configuration, and test commands.

Implementation is not complete until review and QA evidence exist.

---

## Final Implementation Report

Report:

- implemented scope;
- major files/modules changed;
- completed and blocked task IDs;
- backend test command/result;
- PostgreSQL/Flyway verification;
- frontend test command/result;
- end-to-end/manual verification;
- OpenAPI conformance result;
- Code Review decision;
- QA decision;
- Quickstart/traceability updates;
- any approved deviations or unresolved blockers.

If there are no known deviations, state that explicitly.

---

## Artifact Relationships

### Upstream Inputs

All approved Feature 001 Greenfield artifacts and `specs/shared/frontend/spec.md` listed in the Authority section.

### Downstream Consumers

- an approved implementation or coding agent;
- Code Review Checklist;
- QA Review Checklist;
- final implementation report.

### Authority Boundary

This prompt controls implementation-agent execution instructions only. It cannot override approved upstream behavior, governance, architecture, requirements, contracts, or shared frontend standards. Shared frontend standards likewise cannot override Feature 001 behavior or contracts.

### Conflict Rule

If this prompt conflicts with an approved upstream artifact, the upstream artifact wins within its authority boundary and this prompt must be corrected.
