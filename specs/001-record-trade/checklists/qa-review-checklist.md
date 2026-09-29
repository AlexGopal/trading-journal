# QA Review Checklist - 001 Record Completed Trade

- [X] Valid LONG trade returns 201 with persisted trade facts and calculated performance.
- [X] Valid SHORT trade returns 201 with persisted trade facts and calculated performance.
- [X] Same-day trades are accepted.
- [X] Valid fractional shares are accepted.
- [X] Ticker input is trimmed and normalized to uppercase.
- [X] Missing or whitespace-only ticker returns 400.
- [X] Invalid or missing trade type returns 400.
- [X] Missing dates or exit date before entry date returns 400.
- [X] Nonpositive entry price, exit price, or share quantity returns 400.
- [X] Price values with more than 4 fractional decimal places are rejected without silent rounding.
- [X] Share quantities with more than 6 fractional decimal places are rejected without silent rounding.
- [X] Duplicate valid submissions create separate persisted trades with distinct generated IDs.
- [X] LONG and SHORT dollar P&L and percentage-return calculations are correct.
- [X] Percentage-return division uses intermediate scale 10 with `HALF_UP`; API `dollarPnl` uses 2 decimals with `HALF_UP` and `percentageReturn` uses 4 decimals with `HALF_UP`.
- [X] Frontend displays dollar P&L and percentage return with exactly 2 fractional decimal places without imposing symbol, locale, or grouping requirements absent an established repository convention.
- [X] Frontend remains usable at representative desktop and narrower widths without normal-form horizontal scrolling.
- [X] Semantic labels, keyboard operation, visible focus, heading hierarchy, contrast, and non-color-only meaning follow `specs/shared/frontend/spec.md`.
- [X] Loading, success, validation, and technical-error states are visible, understandable, and spatially stable without pixel-perfect assertions.
- [X] PostgreSQL-sensitive persistence and Flyway tests run against Testcontainers PostgreSQL rather than H2.
- [X] Persistence failures return the approved 500 technical-failure outcome without false success.
- [X] Flyway migration successfully creates the approved Trade schema in PostgreSQL.
- [X] Derived `dollarPnl` and `percentageReturn` values are not persisted as authoritative Trade columns.
- [X] API responses and logs do not expose stack traces, credentials, secrets, or unnecessary sensitive values.
- [X] No external brokerage, market-data, authentication, AI, or other unapproved capability is required.
- [X] OpenAPI contract conformance for `POST /api/v1/trades` and approved schemas is validated.

## Final QA Decision

- [X] PASS
- PASS WITH WARNINGS — not selected
- FAIL — not selected
- BLOCKED — not selected
- PENDING — not selected

## Evidence

- Automated verification on 2026-09-29: backend 16/16 with no skips, frontend 7/7, and successful production build.
- Live stack verification through `http://127.0.0.1:5173`: UI `200`; LONG/SHORT `201`; same-day and fractional shares accepted; normalization and exact response scales confirmed; duplicate IDs distinct.
- Invalid date order, price scale, share scale, blank ticker, unsupported type, missing date, and nonpositive price each returned `400`; database counts confirmed invalid requests were not persisted.
- A deliberate PostgreSQL outage returned the approved safe `500`; PostgreSQL was restored and a subsequent break-even trade returned `201` with `0.00` / `0.0000`.
- Desktop and narrow-width browser renders were inspected after responsive correction; semantic labeling, focusability, heading hierarchy, Material UI contrast, and non-color-only result meaning were also reviewed in implementation/tests.
