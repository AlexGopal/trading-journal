# Quickstart - 001 Record Completed Trade

## Purpose

Provide the setup, startup, and verification guide for Feature 001.

This file is created before implementation. Exact repository commands, ports, file paths, and runtime evidence must be verified from the implemented repository before Feature 001 is considered complete.

---

## Read Order

1. `spec.md`
2. `supporting/requirements.md`
3. `data-model.md`
4. `contracts/openapi.yaml`
5. `supporting/test-spec.md`
6. `supporting/traceability-matrix.md`
7. `tasks.md`

---

## Prerequisites

Feature 001 uses the approved Greenfield stack:

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- React
- TypeScript
- Vite
- Material UI
- PostgreSQL
- Docker
- Flyway
- JUnit + Spring Boot Test
- Vitest + React Testing Library

Exact implementation versions must be taken from repository-controlled configuration after environment setup.

---

## Setup and Configuration

Before running the application:

1. inspect the existing repository structure and reuse it rather than creating duplicate frontend/backend applications;
2. confirm Java, Spring Boot, Node/package-manager, and dependency versions from repository configuration;
3. confirm PostgreSQL is configured through repository-controlled Docker configuration;
4. confirm database credentials and other sensitive configuration are externalized;
5. confirm Flyway is configured as the authoritative schema-migration mechanism;
6. confirm JPA/Hibernate uses the Flyway-managed schema rather than serving as the authoritative schema creator;
7. confirm the frontend API base URL/configuration from the implemented repository.

Do not invent configuration names, commands, or ports before they are verified.

---

## Startup Order

Expected runtime order:

```text
PostgreSQL
    ↓
Flyway migration
    ↓
Spring Boot backend
    ↓
React/Vite frontend
```

Exact commands and ports are **PENDING repository verification**.

### Database Command

**PENDING** — populate from the repository-controlled Docker configuration after successful execution.

### Backend Command

**PENDING** — populate from the implemented backend build configuration or repository documentation after successful execution.

### Frontend Command

**PENDING** — populate from the implemented frontend package scripts after successful execution.

### Runtime Ports

**PENDING** — record the actual configured PostgreSQL, backend, and frontend ports after implementation setup.

---

## Database Verification

On startup, verify that:

- PostgreSQL is reachable by the backend;
- Flyway applies the initial versioned migration successfully;
- the Trade table contains the approved authoritative trade fields;
- price fields use `NUMERIC(19,4)`;
- share quantity uses `NUMERIC(19,6)`;
- `dollarPnl` and `percentageReturn` are not authoritative persisted columns;
- no uniqueness constraint prevents repeated identical valid submissions.

---

## API Verification

Feature 001 exposes:

```text
POST /api/v1/trades
```

### Valid LONG Trade

Submit a valid completed LONG trade.

Expected:

- `201 Created`;
- generated trade ID;
- normalized ticker;
- persisted trade facts;
- calculated `dollarPnl`;
- calculated `percentageReturn`.

### Valid SHORT Trade

Submit a valid completed SHORT trade.

Expected:

- `201 Created`;
- SHORT calculations follow the approved formulas;
- response uses the approved success schema.

### Ticker Normalization

Submit a ticker with surrounding whitespace and lowercase letters, for example:

```text
"  aapl  "
```

Expected:

```text
AAPL
```

### Validation Failure

Verify `400 Bad Request` for approved invalid input, including:

- missing or whitespace-only ticker;
- invalid or missing trade type;
- missing dates;
- exit date before entry date;
- nonpositive entry or exit price;
- nonpositive number of shares;
- price with more than 4 fractional decimal places;
- shares with more than 6 fractional decimal places.

Invalid trades must not be persisted.

### Duplicate Submission

Submit the same valid trade twice.

Expected:

- both submissions succeed;
- two separate Trade records are created;
- generated IDs are distinct.

### Technical Failure

Exercise only where a technical or persistence failure can be reproduced safely.

Expected:

- `500 Internal Server Error`;
- client-safe error message;
- no stack trace, credentials, secrets, or sensitive runtime details exposed.

---

## Frontend Verification

Verify that the Feature 001 UI:

- contains ticker, trade type, entry date, entry price, exit date, exit price, and number of shares;
- supports LONG and SHORT;
- shows validation feedback for approved invalid inputs;
- submits valid trades through the API client;
- displays backend-authoritative results;
- follows `specs/shared/frontend/spec.md` for shared Material UI composition, layout, spacing, responsive behavior, accessibility, and state presentation;
- displays dollar P&L and percentage return with exactly 2 fractional decimal places;
- does not require a currency symbol, locale, or grouping style unless the repository establishes one;
- does not show false success after a failed submission;
- remains keyboard-operable and usable at representative desktop and narrower widths without normal-form horizontal scrolling;
- does not introduce trade history, charts, indicators, strategies, authentication, integrations, AI, or other future capabilities.

---

## Automated Tests

Exact commands are **PENDING repository verification**.

The implemented repository must provide executable verification for:

- backend calculation and validation tests;
- service/workflow tests;
- API/controller tests;
- Testcontainers PostgreSQL/Flyway persistence tests, without substituting H2 for PostgreSQL-sensitive behavior;
- frontend component/behavior tests;
- any approved end-to-end verification introduced during implementation.

Only verified commands should be added to this Quickstart.

---

## Verification Focus

Feature 001 should be considered functionally ready for review when:

- valid LONG and SHORT trades work;
- same-day trades and fractional shares work;
- ticker normalization works;
- all approved validation rules are enforced;
- calculations match the approved formulas and percentage division scale 10 / `HALF_UP` policy;
- API precision rules are correct;
- frontend formatting rules are correct;
- duplicate submissions remain allowed;
- Flyway/PostgreSQL persistence matches the Data Model;
- failure handling is safe;
- required creation, validation-rejection, and technical-failure logging is present with safe context and without prohibited sensitive leakage;
- no unapproved Feature 001 capability is introduced.

---

## Troubleshooting

### PostgreSQL / Docker

Check:

- Docker is running;
- repository-controlled database configuration;
- configured credentials;
- port conflicts;
- container logs.

### Flyway

Check:

- database connectivity;
- migration naming/location;
- existing schema state;
- Flyway configuration;
- Hibernate schema-generation settings.

### Backend

Check:

- Java 21 availability;
- repository-controlled dependency versions;
- database connectivity;
- required environment/configuration values;
- backend logs.

### Frontend

Check:

- configured Node/package-manager version;
- dependency installation;
- frontend environment configuration;
- backend API base URL.

---

## Known Limitations

Feature 001 intentionally does not include:

- open positions;
- trade editing or deletion;
- trade-history browsing;
- strategies or setups;
- MACD, RSI, or moving averages;
- charts or dashboards;
- market-data or brokerage integrations;
- authentication or multi-user support;
- AI analysis;
- automated trading;
- fees, commissions, or dividends;
- multiple entries or exits;
- generic multi-asset support.

---

## Current Status

The pre-implementation specification package is being completed.

Runtime implementation has not started. Commands, ports, implementation-derived paths, and executed test evidence remain **PENDING** and must be finalized from repository evidence during implementation.

---

## Artifact Relationships

### Upstream Inputs

- `.specify/memory/constitution.md`
- `supporting/architecture.md`
- `data-model.md`
- `supporting/requirements.md`
- `spec.md`
- `plan.md`
- `tasks.md`
- `contracts/openapi.yaml`
- `supporting/test-spec.md`
- `supporting/traceability-matrix.md`
- `specs/shared/frontend/spec.md`

### Downstream Consumers

- developers;
- reviewers;
- QA;
- `supporting/implementation-build-prompt.md`.

### Authority Boundary

This Quickstart is authoritative for verified setup, startup, and verification instructions.

It does not redefine Feature 001 requirements, business rules, architecture, or API behavior.

The shared frontend specification governs cross-feature presentation and interaction conventions; Feature 001 artifacts govern feature behavior and contract details.

### Conflict Rule

If this Quickstart conflicts with the implemented repository or an approved upstream artifact, reconcile the appropriate source rather than preserving an unverified or stale instruction.
