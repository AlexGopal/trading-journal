# Trading Journal and Analysis App — Constitution

## Purpose

This Constitution defines the project-wide engineering principles, standards, constraints, and non-negotiable practices for the Trading Journal and Analysis App.

It applies across all features and implementation work unless a rule is explicitly amended or an exception is formally approved.

This document governs **how the project is engineered**. It does not define feature-specific behavior, business rules, endpoint payloads, or acceptance criteria.

---

# 1. Project Principles

## 1.1 Spec-Driven Development

The project must follow a Spec-Driven Development process.

Implementation must be based on reviewed and approved upstream artifacts rather than undocumented assumptions.

Before implementation begins for a feature, the applicable artifacts must be sufficiently complete and internally consistent, including as applicable:

- Intended System;
- Business Rules;
- Research;
- Constitution;
- Architecture;
- Data Model;
- Supporting Requirements;
- Specification;
- Plan;
- Tasks;
- OpenAPI;
- Test Specification;
- Traceability;
- review checklists.

Downstream artifacts must not silently override approved upstream artifacts.

## 1.2 Small, Incremental Features

Features should be implemented as focused vertical slices.

The project must avoid adding speculative functionality solely because it may be useful later.

Future extensibility is desirable, but it must not cause unnecessary abstractions, dependencies, or infrastructure in the current feature.

## 1.3 Maintainability Over Cleverness

Code and architecture should favor:

- clarity;
- explicit responsibilities;
- simple control flow;
- predictable conventions;
- testability;
- maintainable abstractions.

Unnecessary complexity, premature optimization, and speculative design are prohibited unless justified by an approved requirement.

## 1.4 Approved Scope Is Authoritative

Implementation must remain within approved feature scope.

A developer or AI implementation agent must not add:

- unapproved product capabilities;
- new external integrations;
- new asset types;
- authentication;
- analytics;
- charting;
- infrastructure;
- or other future-facing capabilities

unless those capabilities have been approved through the SDD artifacts.

---

# 2. Architecture Standards

## 2.1 Frontend and Backend Separation

The application must maintain a clear separation between:

- the React frontend;
- the Spring Boot backend;
- the relational persistence layer.

The frontend must not directly access the database.

The backend is responsible for application behavior, business logic, validation enforcement, persistence coordination, and public API behavior.

## 2.2 Layered Backend Responsibilities

Backend responsibilities must be separated by concern.

At minimum:

- controllers handle HTTP/API concerns;
- services or application-level components coordinate business behavior;
- repositories handle persistence access;
- domain/model objects represent approved data concepts.

Business logic must not be placed directly in controllers.

Persistence-specific logic must not be unnecessarily exposed to controllers or frontend code.

## 2.3 Frontend Responsibilities

The frontend is responsible for:

- rendering user interfaces;
- collecting user input;
- providing user-facing validation feedback;
- invoking approved backend APIs;
- presenting backend results and errors.

Frontend validation may improve user experience, but backend validation remains authoritative for server-side acceptance.

## 2.4 Architecture Must Follow Approved Artifacts

Detailed architecture belongs in the Architecture artifact.

This Constitution must not be treated as permission to invent packages, components, schemas, or runtime services that have not been justified downstream.

---

# 3. Allowed and Prohibited Technologies

## 3.1 Approved Core Technology Stack

The approved project baseline is:

- **Java 21 LTS** for backend development;
- **Spring Boot** for the backend application framework;
- **React** for the frontend;
- **TypeScript** for frontend application code;
- **Vite** for frontend development and build tooling;
- **Material UI (MUI)** as the primary frontend component library;
- **REST over HTTP** for frontend/backend communication;
- **PostgreSQL** as the primary application database;
- **Spring Data JPA / Hibernate** for standard relational persistence;
- **Docker** for the local PostgreSQL runtime;
- **JUnit and Spring Boot Test** for backend testing;
- **Vitest and React Testing Library** for frontend testing.

Exact dependency versions must be pinned in implementation/build configuration.

## 3.2 Introducing New Technologies

A new core framework, database, major UI framework, persistence technology, API style, or infrastructure dependency must not be introduced without:

1. a documented need;
2. Research or an equivalent decision record;
3. review for compatibility with existing architecture;
4. approval before implementation.

Small supporting libraries may be introduced without a Constitution amendment when they:

- solve an approved implementation need;
- do not redefine project architecture;
- do not duplicate an existing approved dependency;
- are documented in the applicable Plan or implementation artifact.

## 3.3 Prohibited Technology Drift

The implementation must not silently replace:

- Spring Boot with another backend framework;
- React with another frontend framework;
- PostgreSQL with another primary database;
- REST with another primary API style;
- Material UI with a competing primary component system.

Such changes require an approved amendment to project-wide technical direction.

---

# 4. Coding and Layering Standards

## 4.1 Clear Ownership of Logic

Business and domain rules must be implemented in appropriate backend business/application layers rather than duplicated across controllers or UI components.

When a business rule is defined in an approved Business Rules artifact, implementation must trace back to that rule.

## 4.2 Type Safety

Type safety should be preserved across the codebase.

For Java:

- use appropriate domain types and enums;
- avoid unnecessary raw types;
- avoid stringly-typed representations when an approved finite domain exists.

For TypeScript:

- define explicit models/types for application and API data;
- avoid `any` unless an external boundary genuinely requires it and the use is justified.

## 4.3 Naming and Readability

Names must communicate intent.

Avoid:

- unexplained abbreviations;
- misleading generic names;
- duplicated concepts with inconsistent terminology.

Terminology should remain consistent with approved domain artifacts.

## 4.4 Error Handling

Errors must be handled deliberately.

The system must not:

- silently swallow exceptions;
- expose stack traces or sensitive internals to end users;
- use successful HTTP responses to represent failures.

Detailed error contracts belong in Specification and OpenAPI.

## 4.5 No Dead or Speculative Code

Do not commit unused abstractions, placeholder integrations, speculative domain hierarchies, or future-feature code that is not required by approved scope.

---

# 5. API and Contract Standards

## 5.1 REST as the Approved API Style

Public application APIs must follow REST-oriented HTTP conventions unless an explicitly approved project-wide change is made.

## 5.2 Contract-First Consistency

When OpenAPI exists for a feature, implementation must remain consistent with the approved OpenAPI contract.

The following must not drift silently between artifacts and code:

- paths;
- methods;
- request fields;
- response fields;
- status codes;
- validation semantics;
- documented error behavior.

## 5.3 DTO Boundary

API request and response structures should be represented through deliberate API-facing models/DTOs where doing so prevents persistence models from becoming accidental public contracts.

Detailed DTO definitions belong in downstream design artifacts.

## 5.4 API Versioning

The initial versioning approach must be defined by Architecture/Specification before public API paths are frozen.

Breaking API changes after a contract is approved require explicit reconciliation of affected artifacts.

---

# 6. Testing Standards

## 6.1 Automated Testing Is Required

Each implemented feature must include automated tests appropriate to its behavior and risk.

Testing must not be deferred solely because implementation was AI-assisted.

## 6.2 Backend Testing

Backend testing should include appropriate combinations of:

- unit tests for business calculations and domain logic;
- service/application tests;
- controller/API tests;
- persistence/integration tests where database behavior matters.

Tests should use the narrowest appropriate scope.

Loading the full Spring application context for every test is discouraged when a smaller test scope is sufficient.

## 6.3 Database-Sensitive Testing

Where correctness depends on PostgreSQL behavior, testing should use PostgreSQL-compatible integration testing rather than assuming another database engine is equivalent.

H2 may be used selectively for tests only when PostgreSQL-specific behavior is not material.

## 6.4 Frontend Testing

Frontend tests must focus on user-observable behavior.

Vitest and React Testing Library are the approved baseline.

Tests should cover meaningful interaction and validation behavior rather than implementation internals.

## 6.5 Tests Must Reflect Approved Behavior

Tests may not redefine requirements.

If expected behavior is unclear or conflicts with approved artifacts, the conflict must be resolved in the artifacts rather than encoded as an arbitrary test expectation.

---

# 7. Security and Privacy Standards

## 7.1 Security Applies Even Without Authentication

The absence of authentication in the initial single-user scope does not remove normal security responsibilities.

The application must:

- validate untrusted input;
- avoid exposing sensitive runtime details;
- handle errors safely;
- avoid committing credentials or secrets;
- use safe dependency and configuration practices.

## 7.2 Secrets

Secrets, credentials, tokens, and passwords must never be committed to source control.

Local secrets must use an appropriate external configuration mechanism.

## 7.3 Authentication and Authorization

Authentication and authorization must not be added until approved by the product and SDD artifacts.

When multi-user capability is introduced, the security model must be researched, specified, and reviewed before implementation.

## 7.4 Personal and Trading Data

If the application later stores user-identifying, brokerage, account, or other sensitive financial information, privacy and security requirements must be revisited before that data enters scope.

---

# 8. Data and Persistence Standards

## 8.1 PostgreSQL Is the Primary Database

Persistent application data must use PostgreSQL unless a project-wide amendment approves a different primary database.

## 8.2 Spring Data JPA / Hibernate Baseline

Standard relational persistence should use Spring Data JPA / Hibernate.

This does not prohibit:

- explicit queries;
- projections;
- native SQL;
- specialized read models

when future requirements justify them.

## 8.3 Data Types Must Match Domain Meaning

Money, percentages, quantities, dates, and identifiers must use representations appropriate to their domain meaning.

Exact precision, scale, and representation rules must be established in the Data Model before affected persistence structures are frozen.

Floating-point types must not be chosen for financial values without explicit justification.

## 8.4 Schema Management

Production-quality schema evolution must use explicit, reviewable database migrations once migration tooling is selected.

Automatic ORM schema generation must not become the permanent schema-management strategy.

## 8.5 Persistence Models Are Not Automatically API Models

Database/entity structures must not become public API contracts merely for convenience.

API, domain, and persistence representations should be separated where their responsibilities differ.

---

# 9. Observability and Operational Standards

## 9.1 Logging

Backend code must use structured, intentional application logging rather than ad hoc console output.

Logs should provide enough context to diagnose failures without exposing secrets or sensitive information.

## 9.2 Operational Complexity

Feature work must not introduce unnecessary infrastructure.

Monitoring, metrics, distributed tracing, message brokers, caches, or other operational services should be introduced only when approved requirements justify them.

## 9.3 Local Development Reproducibility

Required infrastructure dependencies should be reproducible across development machines.

PostgreSQL must initially be provided through Docker using pinned configuration.

---

# 10. Documentation Standards

## 10.1 Artifacts Are Part of the Product

SDD artifacts are authoritative project documentation and must remain synchronized with approved behavior and design.

## 10.2 No Silent Documentation Drift

If implementation reveals that an approved artifact is incorrect or incomplete:

1. stop the affected work;
2. identify the conflict;
3. update or amend the appropriate authoritative artifact;
4. review downstream impact;
5. continue only after reconciliation.

Implementation must not silently diverge from frozen documentation.

## 10.3 Repository Documentation

Developer setup and execution instructions must be kept current as project tooling evolves.

The Quickstart artifact will become authoritative for feature/project setup once generated and approved.

---

# 11. Compatibility and Versioning Principles

## 11.1 Pin Runtime and Dependency Versions

Build files and runtime configuration must pin or constrain dependency versions sufficiently to support reproducible development.

## 11.2 Deliberate Upgrades

Framework, runtime, database, and major dependency upgrades must be deliberate.

An upgrade must not be performed merely because a newer version exists.

Potential impacts on:

- APIs;
- persistence;
- build tooling;
- tests;
- browser behavior;
- deployment

must be reviewed when material.

## 11.3 Cross-Machine Consistency

Desktop/laptop development environments should rely on repository-controlled configuration wherever practical.

Machine-specific setup must not silently change project behavior.

---

# 12. AI-Assisted Development Rules

## 12.1 AI Is an Implementation Assistant, Not the Requirements Authority

Codex, Copilot, or another AI coding agent may assist with implementation, testing, refactoring, and repository inspection.

AI tools must follow the approved SDD artifacts.

They must not invent missing feature behavior or silently make product decisions.

## 12.2 Repository-First Inspection

Before modifying code, an AI implementation agent must inspect the relevant repository structure, configuration, conventions, and approved artifacts.

It must reuse established project patterns rather than scaffolding competing applications or duplicate structures.

## 12.3 Stop on Conflicts

If an AI agent discovers a conflict between:

- Constitution;
- Business Rules;
- Specification;
- OpenAPI;
- Plan;
- Tasks;
- repository constraints;

it must surface the conflict rather than arbitrarily selecting one interpretation.

## 12.4 No Silent Scope Expansion

AI tools must not add unrelated features, dependencies, abstractions, or infrastructure beyond approved scope.

## 12.5 Verification Is Required

AI-generated code is subject to the same testing, review, and documentation requirements as human-written code.

Passing compilation alone is not sufficient evidence of correctness.

---

# 13. Human Review and Approval Requirements

## 13.1 Human Approval Gates

Important artifacts must be reviewed before being treated as frozen upstream authority.

At minimum, human review is required before:

- Architecture is treated as authoritative;
- Specification is treated as authoritative;
- Plan and Tasks drive implementation;
- implementation is considered complete.

## 13.2 Review Profiles

The project's approved Spec Kit Review Profiles tooling should be used at appropriate review points.

Automated review supplements human judgment and does not replace approval.

## 13.3 Review Findings

Material review findings must be addressed or explicitly accepted before downstream work proceeds.

Review output must not be ignored solely because implementation already exists.

---

# 14. Exceptions and Amendment Process

## 14.1 Constitution Changes Are Allowed

This Constitution may be amended when project-wide needs change.

Examples include:

- adopting a new core technology;
- changing testing policy;
- introducing project-wide authentication/security requirements;
- changing persistence standards;
- modifying AI-development governance.

## 14.2 Amendment Requirements

A Constitution amendment must:

1. identify the rule being changed;
2. document the reason;
3. determine which downstream artifacts are affected;
4. update conflicting artifacts before implementation continues;
5. receive explicit human approval.

## 14.3 Feature-Specific Exceptions

A feature-specific exception must not silently modify project-wide governance.

If an exception is truly limited to one feature, it must be:

- documented;
- justified;
- explicitly approved;
- traceable in the affected feature artifacts.

If the exception should apply broadly, amend the Constitution instead.

---

# 15. Artifact Relationships

## Upstream Inputs

This Constitution is derived from:

- `trading_journal_project_brief.md`
- `specs/001-record-trade/supporting/intended-system.md`
- `specs/001-record-trade/supporting/business-rules.md` where those rules imply project-wide governance
- `specs/001-record-trade/research.md`

The Dependency Map was not generated for Feature 001 because no material external integration dependency is currently approved.

## Downstream Consumers

This Constitution governs:

- `specs/001-record-trade/supporting/architecture.md`
- `specs/001-record-trade/data-model.md`
- `specs/001-record-trade/supporting/requirements.md`
- `specs/001-record-trade/spec.md`
- `specs/001-record-trade/plan.md`
- `specs/001-record-trade/tasks.md`
- `specs/001-record-trade/contracts/openapi.yaml` when generated
- Test Specification
- Traceability Matrix
- review checklists
- Quickstart
- Implementation Build Prompt
- implementation and review activities

It also governs future Trading Journal features unless intentionally amended.

## Authority Boundary

This Constitution is authoritative for project-wide engineering governance and non-negotiable development rules.

It is **not** authoritative for:

- feature-specific behavior;
- feature-specific business rules;
- individual endpoint definitions;
- feature-specific request/response fields;
- feature-specific acceptance criteria;
- individual implementation tasks.

Those responsibilities belong to the appropriate feature artifacts.

## Conflict Rule

If a downstream artifact or implementation conflicts with this Constitution, the conflict must not be silently reconciled.

One of the following must occur before affected implementation proceeds:

1. correct the downstream artifact;
2. approve a documented feature-specific exception where appropriate;
3. formally amend this Constitution and reconcile affected downstream artifacts.

---

# 16. Governance Status

**Status:** Ready for review

Once approved, this Constitution becomes the project-wide governance baseline for the Trading Journal and Analysis App.

It should be reused by future features unless project-wide governance is intentionally amended.
