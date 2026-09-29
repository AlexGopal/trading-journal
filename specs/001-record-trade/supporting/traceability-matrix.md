# Traceability Matrix - 001 Record Completed Trade

| Requirement | Business Rules | Specification / AC | Tasks | OpenAPI | Tests |
| --- | --- | --- | --- | --- | --- |
| FR-001 Record completed trade | BR-001, BR-016, BR-017 | US-001, US-002 | T022, T030, T047, T049, T055, T059 | `POST /api/v1/trades` | TC-001, TC-002 |
| FR-002 Stock trades only | BR-001 | Feature scope / Non-Goals | T008, T012, T035, T052, T063 | `CreateTradeRequest` | TC-027, TC-028 |
| FR-003 LONG/SHORT trade types | BR-002 | VAL-002 | T007, T014, T018, T019, T036 | `TradeType` | TC-002, TC-005, TC-015, TC-016 |
| FR-004 Require ticker | BR-003 | VAL-001 | T013, T037, T048, T054 | `CreateTradeRequest.ticker` | TC-003 |
| FR-005 Uppercase ticker | BR-004 | VAL-010, AC-010 | T013, T037, T050, T059 | `TradeResponse.ticker` | TC-004 |
| FR-006 Valid entry price | BR-005 | VAL-006, VAL-011 | T016, T039, T048, T054, T060 | `CreateTradeRequest.entryPrice` | TC-009, TC-012 |
| FR-007 Valid exit price | BR-006 | VAL-007, VAL-011 | T016, T039, T048, T054, T060 | `CreateTradeRequest.exitPrice` | TC-009, TC-012 |
| FR-008 Valid share quantity | BR-007 | VAL-008, VAL-009, VAL-011 | T017, T039, T048, T054, T059, T060 | `CreateTradeRequest.numberOfShares` | TC-010, TC-011, TC-013 |
| FR-009 Require entry date | BR-008 | VAL-003 | T015, T038, T048, T054 | `CreateTradeRequest.entryDate` | TC-006 |
| FR-010 Require exit date | BR-009 | VAL-004 | T015, T038, T048, T054 | `CreateTradeRequest.exitDate` | TC-006 |
| FR-011 Valid date sequence | BR-010 | VAL-005 | T015, T038, T048, T054, T059, T060 | Request validation | TC-007, TC-008 |
| FR-012 LONG dollar P&L | BR-011 | Calculation behavior | T018, T036, T040, T059 | `TradeResponse.dollarPnl` | TC-015 |
| FR-013 SHORT dollar P&L | BR-012 | Calculation behavior | T019, T036, T040, T059 | `TradeResponse.dollarPnl` | TC-016 |
| FR-014 LONG percentage return | BR-013 | Calculation behavior | T018, T036, T040, T059 | `TradeResponse.percentageReturn` | TC-015 |
| FR-015 SHORT percentage return | BR-014 | Calculation behavior | T019, T036, T040, T059 | `TradeResponse.percentageReturn` | TC-016 |
| FR-016 Interpret performance | BR-015 | AC-011, AC-012, AC-013 | T020, T036, T050, T055 | `TradeResponse` | TC-017, TC-023 |
| FR-017 Persist trade facts | BR-001, BR-016, BR-017 | Persistence behavior | T008, T009, T011, T022, T023, T040, T044, T069 | Success contract | TC-001, TC-020, TC-021 |
| FR-018 Generated identifier | — | Successful creation | T008, T024, T031, T041, T044, T061 | `TradeResponse.id` | TC-001, TC-014 |
| FR-019 Derive performance | BR-011, BR-012, BR-013, BR-014 | Derived-value behavior | T010, T018, T019, T023, T040, T044 | `dollarPnl`, `percentageReturn` | TC-015, TC-016, TC-022 |
| FR-020 Present performance | BR-015 | Success response / UI behavior | T028, T031, T050, T055, T059 | `TradeResponse` | TC-001, TC-002, TC-018, TC-023 |
| FR-021 Manual entry only | BR-017 | Feature scope | T047, T049, T052, T063 | `POST /api/v1/trades` | TC-027, TC-028 |
| FR-022 Completed trades only | BR-016 | Feature scope | T015, T047, T052, T059 | `CreateTradeRequest` | TC-001, TC-002, TC-008 |
| FR-023 Repeated submissions | BR-018 | AC-017 | T012, T024, T040, T044, T061 | `POST /api/v1/trades` | TC-014 |
| FR-024 Trim ticker whitespace | BR-019 | VAL-010, AC-016 | T013, T037, T054, T059 | `CreateTradeRequest.ticker` | TC-003, TC-004 |

## Cross-Cutting Traceability

| Requirement / Concern | Tasks | OpenAPI / Contract | Tests |
| --- | --- | --- | --- |
| API-004–API-011 Create-trade contract and errors | T027–T035 | `POST /api/v1/trades`, 201 / 400 / 500, request/response/error schemas | TC-001, TC-002, TC-018, TC-019, TC-026 |
| PostgreSQL and explicit schema management | T068, T069 | N/A | TC-020, TC-021 |
| Flyway migration decision | T068, T069 | N/A | TC-020, TC-021 |
| OP-004 Deliberate backend logging | T066, T067 | Required creation, validation-rejection, and technical-failure events with safe context | TC-025 |
| SEC-003 Safe failure details | T033, T043, T067 | `TechnicalErrorResponse` | TC-019, TC-025 |
| Frontend presentation behavior | T045–T057 | `TradeResponse` | TC-023, TC-024 |
| Shared frontend presentation and interaction conventions | T047, T051, T053, T056, T057, T064 | `specs/shared/frontend/spec.md` | TC-023, TC-024, TC-029 |
| Contract conformance | T027–T035 | `contracts/openapi.yaml` | TC-026 |

## Implementation Evidence

- Domain, validation, calculation, service, controller, logging, Flyway, and PostgreSQL evidence: `backend/src/main`, `backend/src/test`, and the verified `mvn test` run documented in `quickstart.md`.
- Frontend API-boundary, form, state, validation, responsive composition, accessibility-oriented, and formatting evidence: `frontend/src` and the verified `npm.cmd test` / `npm.cmd run build` runs documented in `quickstart.md`.
- Full-stack evidence: the verified local Docker/PostgreSQL, Spring Boot, and Vite run documented in `quickstart.md`, including LONG, SHORT, same-day/fractional-share, ticker-normalization, duplicate-ID, and validation behavior.

## Notes

- Exact human-readable validation/error wording is intentionally non-contractual; status, structure, field association, client safety, and understandable content remain required.
- Maximum ticker length and detailed ticker-character restrictions remain unresolved.
- Testcontainers with PostgreSQL is approved for PostgreSQL-sensitive tests; only the exact Testcontainers dependency version and E2E framework remain implementation-time decisions.
- This matrix does not introduce new behavior; it links existing approved artifacts.
