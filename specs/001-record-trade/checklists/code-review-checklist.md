# Code Review Checklist - 001 Record Completed Trade

## Authority

- `.specify/memory/constitution.md`
- `specs/shared/frontend/spec.md`
- `supporting/architecture.md`
- `data-model.md`
- `supporting/requirements.md`
- `spec.md`
- `plan.md`
- `tasks.md`
- `contracts/openapi.yaml`
- `supporting/test-spec.md`
- `supporting/traceability-matrix.md`

- [ ] Implementation stays within Feature 001 scope and introduces no unapproved future capabilities.
- [ ] Controller remains thin; service/application layer owns validation, normalization, calculations, and workflow coordination.
- [ ] JPA entities are not exposed directly as API contracts.
- [ ] LONG/SHORT calculations and `BigDecimal` precision match approved rules.
- [ ] Ticker, date, price, share, and decimal-scale validation match the Specification.
- [ ] Duplicate valid submissions create separate records with distinct generated IDs.
- [ ] Flyway manages the PostgreSQL schema and JPA/Hibernate uses that schema without authoritative automatic schema generation.
- [ ] `POST /api/v1/trades` and 201/400/500 behavior match `contracts/openapi.yaml`.
- [ ] API and frontend derived-value formatting match approved precision rules.
- [ ] Error handling does not expose stack traces, secrets, credentials, or sensitive runtime details.
- [ ] Backend emits the required creation, validation-rejection, and technical-failure events with approved safe context and without prohibited sensitive values or client-visible internal exception details.
- [ ] Frontend uses the approved API-client boundary and does not contain persistence or backend business logic.
- [ ] Frontend follows the shared layout, Material UI composition, responsive, accessibility, and state-presentation conventions without introducing pixel-perfect coupling or altering Feature 001 behavior.
- [ ] Tests cover calculations, validation, persistence, Flyway, API success/failure, frontend behavior, and duplicate submissions.
- [ ] Runtime and dependency versions are pinned or explicitly constrained.
- [ ] Traceability remains consistent with approved requirements, tasks, contract, and tests.

## Final Decision

- [ ] PASS
- [ ] PASS WITH WARNINGS
- [ ] FAIL
- [ ] PENDING
