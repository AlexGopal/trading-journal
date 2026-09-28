# Test Specification - 001 Record Completed Trade

## Test Cases

- TC-001 Valid LONG trade returns 201 with the persisted trade and calculated performance.
- TC-002 Valid SHORT trade returns 201 with correct SHORT performance calculations.
- TC-003 Missing, empty, or whitespace-only ticker returns 400 and does not persist the trade.
- TC-004 Ticker with surrounding whitespace is trimmed and normalized to uppercase.
- TC-005 Missing or unsupported trade type returns 400.
- TC-006 Missing entry date or exit date returns 400.
- TC-007 Exit date before entry date returns 400.
- TC-008 Same-day completed trade is accepted.
- TC-009 Entry price or exit price less than or equal to zero returns 400.
- TC-010 Share quantity less than or equal to zero returns 400.
- TC-011 Fractional shares up to 6 decimal places are accepted.
- TC-012 Entry or exit price with more than 4 decimal places returns 400 without silent rounding.
- TC-013 Share quantity with more than 6 decimal places returns 400 without silent rounding.
- TC-014 Duplicate valid submission creates a second Trade with a different generated ID.
- TC-015 LONG dollar P&L and percentage return use the approved formulas, with percentage division at intermediate scale 10 using `HALF_UP`.
- TC-016 SHORT dollar P&L and percentage return use the approved formulas, with percentage division at intermediate scale 10 using `HALF_UP`.
- TC-017 Break-even trade returns zero dollar P&L and zero percentage return.
- TC-018 API serializes `dollarPnl` to 2 decimal places using `HALF_UP` and `percentageReturn` to 4 decimal places using `HALF_UP`.
- TC-019 Technical or persistence failure returns 500 and does not expose stack traces, credentials, secrets, or sensitive runtime details.
- TC-020 Testcontainers starts a real PostgreSQL container and Flyway creates the approved Trade persistence structure; H2 is not used as a substitute.
- TC-021 JPA/Hibernate uses the Flyway-managed schema and does not serve as the authoritative schema-generation mechanism.
- TC-022 Derived `dollarPnl` and `percentageReturn` values are calculated and are not persisted as Trade fields.
- TC-023 Frontend displays dollar P&L and percentage return with exactly 2 fractional decimal places without requiring a currency symbol, locale, or grouping style unless established by the repository.
- TC-024 Failed validation or technical submission is not shown as successfully recorded.
- TC-025 Backend logs successful creation, appropriate application-level validation rejection, and unexpected technical/persistence failure with approved safe context; prohibited sensitive values are absent and client responses expose no internal exception details.
- TC-026 Contract conformance with `contracts/openapi.yaml`.
- TC-027 Feature 001 does not require authentication, market data, brokerage connectivity, or other external integrations.
- TC-028 Feature 001 does not introduce unapproved trade-history, edit, delete, open-position, charting, indicator, AI, or multi-user capabilities.
- TC-029 Feature 001 follows `specs/shared/frontend/spec.md` for responsive layout, semantic labeling, keyboard/focus accessibility, intentional Material UI composition, and stable loading/success/error presentation without changing feature behavior.

## Notes

- Backend validation is authoritative; frontend validation is for user experience only.
- Feature-specific assertions come from Feature 001 artifacts; shared presentation and interaction assertions come from `specs/shared/frontend/spec.md` and should avoid pixel-perfect styling checks.
- Exact human-readable validation and error-message wording is intentionally non-contractual; tests assert status, structure, field association, safety, and understandable content rather than verbatim prose.
- Maximum ticker length and detailed ticker-character restrictions remain intentionally unresolved.
- The exact Testcontainers dependency version and E2E framework are implementation-time decisions and must not change approved behavior.
