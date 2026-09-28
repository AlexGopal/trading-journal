# QA Review Checklist - 001 Record Completed Trade

- [ ] Valid LONG trade returns 201 with persisted trade facts and calculated performance.
- [ ] Valid SHORT trade returns 201 with persisted trade facts and calculated performance.
- [ ] Same-day trades are accepted.
- [ ] Valid fractional shares are accepted.
- [ ] Ticker input is trimmed and normalized to uppercase.
- [ ] Missing or whitespace-only ticker returns 400.
- [ ] Invalid or missing trade type returns 400.
- [ ] Missing dates or exit date before entry date returns 400.
- [ ] Nonpositive entry price, exit price, or share quantity returns 400.
- [ ] Price values with more than 4 fractional decimal places are rejected without silent rounding.
- [ ] Share quantities with more than 6 fractional decimal places are rejected without silent rounding.
- [ ] Duplicate valid submissions create separate persisted trades with distinct generated IDs.
- [ ] LONG and SHORT dollar P&L and percentage-return calculations are correct.
- [ ] Percentage-return division uses intermediate scale 10 with `HALF_UP`; API `dollarPnl` uses 2 decimals with `HALF_UP` and `percentageReturn` uses 4 decimals with `HALF_UP`.
- [ ] Frontend displays dollar P&L and percentage return with exactly 2 fractional decimal places without imposing symbol, locale, or grouping requirements absent an established repository convention.
- [ ] Frontend remains usable at representative desktop and narrower widths without normal-form horizontal scrolling.
- [ ] Semantic labels, keyboard operation, visible focus, heading hierarchy, contrast, and non-color-only meaning follow `specs/shared/frontend/spec.md`.
- [ ] Loading, success, validation, and technical-error states are visible, understandable, and spatially stable without pixel-perfect assertions.
- [ ] PostgreSQL-sensitive persistence and Flyway tests run against Testcontainers PostgreSQL rather than H2.
- [ ] Persistence failures return the approved 500 technical-failure outcome without false success.
- [ ] Flyway migration successfully creates the approved Trade schema in PostgreSQL.
- [ ] Derived `dollarPnl` and `percentageReturn` values are not persisted as authoritative Trade columns.
- [ ] API responses and logs do not expose stack traces, credentials, secrets, or unnecessary sensitive values.
- [ ] No external brokerage, market-data, authentication, AI, or other unapproved capability is required.
- [ ] OpenAPI contract conformance for `POST /api/v1/trades` and approved schemas is validated.

## Final QA Decision

- [ ] PASS
- [ ] PASS WITH WARNINGS
- [ ] FAIL
- [ ] BLOCKED
- [ ] PENDING
