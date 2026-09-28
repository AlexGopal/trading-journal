# Trading Journal and Analysis App — Research

## Document Purpose

This document records the major technical investigations and decisions for the Trading Journal and Analysis App before Constitution and Architecture are established.

The purpose of this artifact is not to define detailed implementation structure. It records the selected technologies, realistic alternatives considered, rationale for each choice, important consequences, and remaining technical questions.

The decisions in this document are based on the approved Intended System and Business Rules for Feature 001.

---

## Research Summary

The application will use a modern full-stack web architecture centered on technologies that are relevant to the intended portfolio/work environment while remaining appropriate for a small Greenfield application that may grow over time.

### Selected Baseline

| Area | Decision |
|---|---|
| Backend language | Java |
| Java baseline | Java 21 LTS |
| Backend framework | Spring Boot |
| Frontend library | React |
| Frontend language | TypeScript |
| Frontend build tooling | Vite |
| UI component library | Material UI |
| API style | REST |
| Primary database | PostgreSQL |
| Persistence access | Spring Data JPA / Hibernate |
| Local database runtime | PostgreSQL in Docker |
| Backend testing | JUnit + Spring Boot Test |
| Frontend testing | Vitest + React Testing Library |
| Initial containerization | PostgreSQL only |
| Full-app containerization | Deferred until useful |
| Authentication | Deferred; not required for initial single-user scope |
| Charting library | Deferred until a feature requires charting |
| External market/brokerage integrations | Deferred |

Exact patch versions should be pinned when the implementation environment is created rather than treated as permanent project intent.

---

# 1. Backend Technology Decision

## Decision

Use **Java with Spring Boot** for the backend.

Use **Java 21 LTS** as the initial Java baseline.

## Rationale

Java and Spring Boot are appropriate because:

- they align with the technologies the project is intended to demonstrate;
- Spring Boot provides a mature structure for REST APIs, validation, persistence, testing, configuration, and later security requirements;
- the project will likely grow from basic trade CRUD and performance calculations into filtering, analytics, and integrations;
- the Spring ecosystem provides established support for relational persistence and testing;
- using a familiar enterprise stack reduces unnecessary learning overhead while still creating a realistic application.

Java 21 provides a stable long-term-support baseline without requiring the project to depend on the newest language release.

## Alternatives Considered

### Node.js / TypeScript Backend

**Advantages**
- one language across frontend and backend;
- lightweight setup;
- strong ecosystem.

**Reason not selected**
- the project is specifically intended to demonstrate Java/Spring Boot experience;
- using Node would reduce alignment with the desired work-oriented technology stack.

### Python Backend

**Advantages**
- fast development;
- strong analytics ecosystem.

**Reason not selected**
- less aligned with the primary backend skills this application is intended to demonstrate;
- no current application requirement justifies introducing Python into the core backend.

## Consequences

- Backend development will follow Spring conventions.
- Java domain types can strongly model trades and trade direction.
- Future features can add validation, persistence, security, and integration components within the same ecosystem.
- The application will have separate frontend and backend projects or modules rather than a single-language full-stack codebase.

---

# 2. Spring Boot Version Strategy

## Decision

Use a **current stable Spring Boot release** when the backend is initialized.

At the time of this research, Spring Boot 4.1.x is a current stable line.

## Rationale

The application is Greenfield, so there is no existing framework version that must be preserved.

A current stable release provides:

- current framework support;
- current dependency management;
- modern Spring conventions;
- fewer immediate upgrade concerns than beginning on an already-aging baseline.

## Alternative Considered

### Older Spring Boot 3.x Line

**Advantages**
- extremely mature ecosystem;
- broad examples and community history.

**Reason not selected as the default**
- there is no legacy compatibility requirement forcing an older major version;
- the project can begin on a current stable Greenfield baseline.

## Consequences

- Exact compatibility of added libraries must be checked when dependencies are introduced.
- Version upgrades should not be performed casually once implementation begins.

---

# 3. Frontend Technology Decision

## Decision

Use **React with TypeScript**.

## Rationale

React is well suited to the planned application because future functionality is likely to include:

- trade-entry forms;
- trade-history tables;
- filters;
- cards and summary metrics;
- dashboards;
- dialogs;
- charts;
- interactive analysis.

TypeScript is selected from the beginning rather than adding it later.

Expected benefits include:

- explicit frontend models for objects such as Trade and TradeType;
- safer handling of API response shapes;
- better editor support;
- earlier detection of frontend/backend mismatches;
- easier maintenance as the application gains more analytical data structures.

## Alternatives Considered

### React with JavaScript

**Advantages**
- slightly less syntax and setup for a very small project.

**Reason not selected**
- the project is expected to grow beyond a trivial UI;
- the value of typed domain and API models outweighs the small additional complexity.

### Angular

**Advantages**
- comprehensive application framework;
- strong TypeScript integration.

**Reason not selected**
- adds more framework structure than this project currently requires;
- React better matches the intended technology experience.

### Vue

**Advantages**
- approachable and productive frontend framework.

**Reason not selected**
- React better matches the intended project technology profile.

## Consequences

- Frontend domain and API types should be deliberately maintained.
- TypeScript compiler checks become part of frontend verification.
- Avoid using `any` as a shortcut around proper types unless a justified integration requires it.

---

# 4. Frontend Build Tool Decision

## Decision

Use **Vite** for the React + TypeScript frontend.

## Rationale

Vite provides a focused development/build environment for a client-side React application.

It avoids introducing a larger full-stack frontend framework when:

- routing and rendering requirements are currently straightforward;
- the backend is already provided by Spring Boot;
- server-side rendering is not a product requirement;
- the application is intended primarily as an interactive dashboard-style web application rather than a public SEO-focused content site.

## Alternatives Considered

### Next.js

**Advantages**
- routing and application framework features;
- server rendering and server-side capabilities.

**Reason not selected**
- duplicates server responsibilities already owned by the Spring Boot backend;
- no current requirement exists for server-side rendering or SEO-oriented rendering;
- adds unnecessary architectural complexity for Feature 001.

### Create React App

**Reason not selected**
- Vite is the more appropriate modern setup for a new standalone React application.

## Consequences

- React remains a separate client application.
- Spring Boot remains the application backend.
- Frontend-to-backend communication will occur through REST APIs.

---

# 5. UI Component Strategy

## Decision

Use **Material UI (MUI)** as the primary React component library.

## Rationale

The application is expected to behave more like a data-entry and analytics application than a heavily branded marketing website.

Likely UI needs include:

- text and numeric inputs;
- date inputs;
- buttons;
- validation messages;
- tables;
- cards;
- dialogs;
- navigation;
- responsive layouts;
- later dashboard components.

Material UI provides established React components for these application-oriented needs and reduces the amount of low-value UI boilerplate required for early features.

The choice also builds on prior familiarity rather than introducing an entirely new styling approach.

## Alternatives Considered

### Tailwind CSS

**Advantages**
- highly flexible;
- productive styling workflow;
- excellent for custom visual design.

**Reason not selected**
- provides styling utilities rather than the same breadth of ready-made application components;
- the current priority is building a clean functional application efficiently.

### Plain CSS / CSS Modules

**Advantages**
- minimal dependency footprint;
- complete control.

**Reason not selected**
- increases the amount of basic component and styling work;
- offers little project-specific benefit for the initial application.

## Consequences

- MUI components should be used consistently instead of mixing several competing component libraries.
- A project theme can later establish consistent typography, spacing, and visual identity.
- Custom CSS remains acceptable where MUI styling mechanisms are insufficient.
- The UI should not rely blindly on default component appearance; later Architecture/UX decisions may define a coherent theme.

---

# 6. API Style Decision

## Decision

Use a **REST API over HTTP** between the React frontend and Spring Boot backend.

## Rationale

REST is appropriate because:

- the application's operations naturally involve resources such as trades;
- the frontend and backend are separate;
- Spring Boot has strong REST support;
- Feature 001 primarily requires straightforward data submission and retrieval;
- it keeps the interface easy to inspect, test, document, and later expose through OpenAPI.

## Alternatives Considered

### GraphQL

**Advantages**
- flexible client-driven querying;
- useful for complex interconnected data requirements.

**Reason not selected**
- current application requirements do not justify the additional schema and infrastructure complexity;
- REST is simpler for the initial resource-oriented workflows.

## Consequences

- Detailed endpoint paths, payloads, status codes, and error contracts remain downstream Specification/OpenAPI decisions.
- This Research artifact approves the architectural style, not specific endpoints.

---

# 7. Primary Database Decision

## Decision

Use **PostgreSQL** as the primary application database.

## Rationale

The application's core domain is naturally relational.

Examples include:

- trades;
- trade direction;
- dates;
- prices;
- share quantities;
- later strategy/setup classifications;
- later indicator observations;
- later analytical filtering and aggregation.

PostgreSQL provides a production-grade relational database while remaining straightforward to run locally through Docker.

It is preferred over using an in-memory database as the application's real persistence layer because the Trading Journal is intended to retain and analyze historical data over time.

## Alternatives Considered

### H2

**Advantages**
- very easy setup;
- useful for demonstrations and certain automated tests;
- integrates easily with Spring Boot.

**Reason not selected as the primary database**
- the application is intended to behave as a real persistent journal rather than a temporary demo;
- developing directly against PostgreSQL reduces differences between development and realistic deployment environments.

H2 may still be considered for isolated tests if doing so does not hide PostgreSQL-specific behavior.

### MySQL

**Advantages**
- mature relational database;
- widely used;
- suitable for this domain.

**Reason not selected**
- no project requirement favors it over PostgreSQL;
- PostgreSQL is equally appropriate and is selected as the project's standard relational database.

### SQLite

**Advantages**
- very lightweight;
- requires little operational setup.

**Reason not selected**
- less representative of the intended server-based application architecture;
- PostgreSQL provides a stronger growth path for future filtering and analytics.

## Version Strategy

Use a currently supported PostgreSQL major version when the database environment is established.

The exact major version must be pinned in Docker configuration so all development machines use the same database version.

## Consequences

- Database setup becomes a required local-development dependency.
- Docker will make that dependency reproducible.
- Database migrations are managed explicitly with Flyway rather than relying on automatic schema creation.
- JPA/Hibernate remains the application persistence/ORM layer; Flyway owns PostgreSQL schema creation and evolution.

---

# 8. Persistence Access Decision

## Decision

Use **Spring Data JPA with Hibernate/JPA persistence** for normal relational data access.

## Rationale

Feature 001 and the expected product direction are based primarily on structured relational entities.

Spring Data JPA:

- reduces repetitive CRUD repository code;
- integrates naturally with Spring Boot;
- supports entity persistence and repository abstractions;
- still allows custom queries when later analytics require them.

## Alternatives Considered

### Raw JDBC / JdbcTemplate

**Advantages**
- explicit SQL;
- direct control over queries.

**Reason not selected as the default**
- introduces avoidable boilerplate for standard persistence;
- Feature 001 does not require low-level SQL control.

### jOOQ

**Advantages**
- strong SQL-centric development model;
- excellent control for complex SQL.

**Reason not selected initially**
- current CRUD requirements do not justify the extra tooling;
- future analytics needs can be reevaluated if query complexity grows significantly.

## Consequences

- Domain/persistence modeling should avoid overcomplicated entity relationships.
- JPA should not prevent the project from using explicit SQL or projections later when analytical queries make them appropriate.
- Detailed entity structure belongs in Data Model and Architecture, not this document.

---

# 9. Local Database and Docker Decision

## Decision

Run **PostgreSQL in Docker** for local development.

Do **not** require the Spring Boot backend or React frontend to be containerized during the first implementation increment.

## Rationale

Using Docker for PostgreSQL provides:

- consistent database setup across development machines;
- easy reset/recreation;
- no requirement to install PostgreSQL directly on Windows;
- an explicit database version and configuration.

Containerizing the entire application immediately would add build and orchestration work before it provides meaningful value.

## Alternatives Considered

### Install PostgreSQL Directly on Each Development Machine

**Reason not selected**
- creates more machine-specific setup;
- Docker provides a more reproducible development dependency.

### Dockerize Frontend + Backend + Database Immediately

**Reason not selected**
- unnecessary for Feature 001;
- increases initial project setup and debugging surface;
- can be added once the application itself is functioning.

## Consequences

- Docker is an initial development prerequisite for the database.
- Full application containerization remains a later infrastructure decision.

---

# 10. Backend Testing Decision

## Decision

Use **JUnit** with **Spring Boot testing support**.

Different test scopes should be used intentionally rather than loading the complete Spring context for every test.

## Expected Testing Layers

Potential layers include:

- unit tests for calculations and business logic;
- service tests;
- controller/API tests;
- repository/integration tests where persistence behavior matters.

## Database Test Strategy

The exact database integration-test approach remains to be finalized.

Candidate approaches include:

1. PostgreSQL through Testcontainers for database-sensitive integration tests;
2. H2 for limited fast tests where database-specific behavior is irrelevant.

Research preference is to use real PostgreSQL-based integration testing where correctness depends on database behavior, rather than assuming H2 perfectly represents PostgreSQL.

## Consequences

- Feature 001 P&L calculations should be testable independently from HTTP and database layers.
- Testing responsibilities will be refined in Architecture, Plan, and Test Specification.

---

# 11. Frontend Testing Decision

## Decision

Use **Vitest** and **React Testing Library**.

## Rationale

Vitest integrates naturally with a Vite-based frontend.

React Testing Library encourages testing components through user-observable behavior rather than relying heavily on internal implementation details.

Expected initial testing includes:

- trade-form validation behavior;
- user interactions;
- rendering of success/error states;
- API integration boundaries where practical.

## Alternatives Considered

### Jest

**Advantages**
- mature and widely known.

**Reason not selected**
- Vitest aligns directly with the selected Vite development environment and is sufficient for the planned frontend.

## Consequences

- UI tests should focus on behavior visible to the user.
- End-to-end browser testing is not yet selected and can be introduced later if needed.

---

# 12. Authentication and Security Scope

## Decision

Do **not** implement user authentication in Feature 001.

## Rationale

The approved Intended System defines the initial application as single-user and explicitly places multi-user authentication outside the initial scope.

Adding authentication now would:

- add significant application complexity;
- distract from the first trading-journal capability;
- solve a problem the approved feature does not currently have.

## Future Consideration

If the application becomes hosted for multiple users, authentication and authorization must be researched and specified before that capability is implemented.

## Consequences

- Security still matters for normal application practices and input validation.
- Lack of Feature 001 authentication must not be interpreted as a permanent product requirement.

---

# 13. Charting and Visualization Decision

## Decision

Do **not** select a charting library yet.

## Rationale

Feature 001 does not require charts.

Choosing a charting dependency before analytical requirements exist would be premature. The eventual library should be selected based on actual dashboard and visualization needs.

## Consequences

- No charting package should be added solely for anticipated future work.
- Revisit this decision when the first chart/dashboard feature is specified.

---

# 14. External Integrations Decision

## Decision

No market-data API, brokerage API, or external trading integration will be introduced for Feature 001.

## Rationale

The approved Feature 001 workflow is manual entry of completed trades.

External integrations are future product possibilities but would introduce:

- external dependencies;
- credentials;
- rate limits;
- reliability concerns;
- additional data contracts;
- security considerations.

Those costs are not justified by the current feature.

## Consequences

- A Dependency Map is not required for Feature 001 based on the currently approved scope.
- If an external integration is approved later, Dependency Map and Research should be revisited.

---

# 15. Database Migration Tooling

## Decision Status

**Approved: Flyway.**

## Decision

Use **Flyway** for PostgreSQL schema creation and evolution.

Spring Data JPA / Hibernate remains the persistence and ORM layer used by application code. Hibernate automatic schema generation is not the authoritative schema-management mechanism.

Feature 001 begins with a versioned Flyway migration that creates the approved Trade table using the Data Model's PostgreSQL representations.

---

# 16. Deployment Strategy

## Decision Status

**Deferred.**

## Rationale

Feature 001 requires a maintainable local application but no hosting platform has been approved.

Potential deployment targets should be investigated when deployment becomes an actual project goal.

No cloud provider should be selected merely to fill out the architecture.

---

# 17. Technical Decision Summary

## Approved

- Java backend
- Java 21 LTS baseline
- Spring Boot
- React
- TypeScript
- Vite
- Material UI
- REST API
- PostgreSQL primary database
- Spring Data JPA / Hibernate
- PostgreSQL run locally in Docker
- Flyway for versioned PostgreSQL schema migrations
- `BigDecimal` derived-performance calculation without premature precision reduction
- derived output scales: API dollar P&L 2 decimals, API percentage return 4 decimals, UI dollar P&L currency 2 decimals, and UI percentage return 2 decimals
- JUnit + Spring Boot Test
- Vitest + React Testing Library
- no authentication for Feature 001
- no external market/brokerage integration for Feature 001
- no charting dependency until required
- backend/frontend do not need initial containerization

## Deferred / Open

- exact dependency patch versions at implementation time;
- production deployment target;
- full application containerization;
- end-to-end browser testing framework;
- charting library;
- authentication mechanism if multi-user functionality is later approved;
- market-data provider;
- brokerage integration;
- detailed strategy for supporting future non-stock asset types.

---

# 18. Risks and Tradeoffs

## R-001 — Framework and Library Evolution

Modern frontend and backend ecosystems evolve frequently.

**Mitigation:**  
Treat technology families and supported major lines as architectural decisions while pinning exact versions in build files. Upgrade deliberately rather than automatically.

## R-002 — Premature Future-Feature Design

The application has a broad long-term direction that could tempt implementation to introduce abstractions before they are required.

**Mitigation:**  
Keep Feature 001 small. Do not introduce charting, integrations, multiple asset-type hierarchies, authentication, or advanced analytics until approved features require them.

## R-003 — ORM Overreach

JPA is useful for normal persistence but can become awkward if later analytical queries become complex.

**Mitigation:**  
Use JPA for standard persistence without treating it as a prohibition on explicit SQL, projections, or other query approaches when future requirements justify them.

## R-004 — Database Environment Differences

Using a different database engine for all tests could hide PostgreSQL-specific behavior.

**Mitigation:**  
Use PostgreSQL-backed integration testing where database behavior materially affects correctness.

## R-005 — UI Library Lock-In

A component library can influence design conventions.

**Mitigation:**  
Use Material UI consistently, maintain a project-level theme, and avoid depending unnecessarily on proprietary or specialized components.

---

# 19. Open Questions for Downstream Artifacts

The following questions should be resolved only when their downstream artifact requires the answer:

1. What is the exact backend/frontend repository directory structure?
2. What are the domain entities and database tables?
3. What numeric types and precision should be used for money, percentages, and fractional shares?
4. What exact REST resources and endpoint contracts are required?
5. What global API error format should be used?
6. What Material UI theme and visual conventions should be established?
7. When charting becomes necessary, which chart library best fits the approved visualization requirements?
8. What deployment environment will be used?
9. If multi-user functionality is approved, what authentication and authorization approach will be used?

These questions are not blockers for completing the current Research artifact.

---

# 20. Artifact Relationships

## Upstream Inputs

- `trading_journal_project_brief.md`
- `supporting/intended-system.md`
- `supporting/business-rules.md`
- approved technology discussions and project decisions

## Downstream Consumers

This Research artifact provides decision and rationale input to:

- `.specify/memory/constitution.md`
- `supporting/architecture.md`
- `data-model.md`
- `supporting/requirements.md`
- `spec.md`
- `plan.md`
- `tasks.md`
- later implementation and review artifacts

## Authority Boundary

This artifact is authoritative for the approved technology and technical-direction decisions documented here.

It is not authoritative for:

- detailed architecture;
- exact package/folder structure;
- API endpoint contracts;
- database schema;
- feature behavior;
- individual implementation tasks.

Those responsibilities belong to their respective downstream artifacts.

---

# 21. Research Review Status

**Status:** Ready for review.

The decisions in this document should be reviewed before being treated as frozen upstream input for Constitution and Architecture.

Any disagreement with a technology choice should be resolved here rather than silently changed in downstream artifacts.
