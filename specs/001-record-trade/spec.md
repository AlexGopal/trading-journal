# Trading Journal and Analysis App — Feature 001 Specification

## Feature Overview

Feature 001 enables the user to manually record a completed stock trade and obtain its calculated performance.

The feature supports completed stock trades only and accepts the following trade information:

- ticker;
- trade type (`LONG` or `SHORT`);
- entry date;
- entry price;
- exit date;
- exit price;
- number of shares.

The system validates the submitted trade, normalizes approved values, calculates dollar profit/loss and percentage return using the approved LONG/SHORT formulas, persists the approved trade facts, and makes the calculated performance available to the user.

Feature 001 does not include open positions, market-data integration, brokerage integration, authentication, charting, strategy tracking, indicator tracking, or other future capabilities.

---

# 1. Objective

The objective of Feature 001 is to establish the first usable trading-journal workflow:

```text
Enter completed stock trade
        ↓
Validate approved trade data
        ↓
Calculate trade performance
        ↓
Persist the completed trade
        ↓
Present the recorded trade and its calculated result
```

The feature must provide a reliable foundation for later trade-history and analysis features without implementing those future features now.

---

# 2. Scope

## 2.1 In Scope

Feature 001 includes:

- manual entry of a completed stock trade;
- stock trades only;
- `LONG` and `SHORT` trade types;
- ticker entry;
- ticker uppercase normalization;
- entry date;
- entry price;
- exit date;
- exit price;
- number of shares;
- fractional shares;
- approved validation;
- dollar P&L calculation;
- percentage return calculation;
- persistence of approved trade facts;
- presentation of the recorded trade and calculated performance;
- React + TypeScript frontend;
- Spring Boot backend;
- REST communication;
- PostgreSQL persistence.

## 2.2 Out of Scope

Feature 001 does not include:

- open positions;
- trade editing;
- trade deletion;
- advanced trade-history browsing;
- strategy/setup tracking;
- MACD;
- RSI;
- moving-average calculations;
- dashboards;
- charts;
- live market data;
- automatic price retrieval;
- automatic trade import;
- brokerage integration;
- multi-user functionality;
- authentication;
- authorization;
- AI-assisted analysis;
- automated trading;
- additional asset types;
- commissions;
- fees;
- dividends;
- multiple entries;
- multiple exits.

Future capabilities require separate approval and specification.

---

# 3. Actors

## Primary Actor

**Individual Trader**

The initial user is an individual retail trader using the application to record and review their own trading activity.

Feature 001 assumes a single-user application.

## External Actors

Feature 001 has no required external system actor.

There is no:

- brokerage;
- market-data provider;
- authentication provider;
- trading platform;
- AI service

in the Feature 001 runtime flow.

---

# 4. Preconditions

Before the Feature 001 workflow can complete successfully:

1. the frontend application is available;
2. the backend application is available;
3. PostgreSQL is available to the backend;
4. the user supplies all required Feature 001 trade information;
5. the supplied information satisfies the approved validation rules.

Authentication is not a precondition.

---

# 5. User Stories

## US-001 — Record a Completed Long Trade

As an individual trader, I want to record a completed `LONG` stock trade so that I can preserve the trade in my journal and see whether it made or lost money.

### Acceptance Intent

The system must:

- accept approved LONG trade input;
- validate the trade;
- normalize the ticker;
- calculate LONG dollar P&L;
- calculate LONG percentage return;
- persist the trade facts;
- present the recorded trade and its calculated performance.

---

## US-002 — Record a Completed Short Trade

As an individual trader, I want to record a completed `SHORT` stock trade so that I can preserve the trade in my journal and see whether it made or lost money.

### Acceptance Intent

The system must:

- accept approved SHORT trade input;
- validate the trade;
- normalize the ticker;
- calculate SHORT dollar P&L;
- calculate SHORT percentage return;
- persist the trade facts;
- present the recorded trade and its calculated performance.

---

## US-003 — Receive Validation Feedback

As an individual trader, I want invalid trade information to be rejected so that the journal does not store trades that violate the approved Feature 001 rules.

### Acceptance Intent

The system must reject data that violates approved Feature 001 validation requirements.

---

# 6. Behavioral Specification

## 6.1 Successful Trade Submission

When the user submits a valid completed stock trade:

1. the application accepts the submitted values;
2. the backend validates the approved business constraints;
3. the ticker is normalized to uppercase;
4. the backend determines the calculation path from `tradeType`;
5. dollar P&L is calculated;
6. percentage return is calculated;
7. the approved trade facts are persisted;
8. the calculated performance is made available to the frontend;
9. the frontend presents the successful recorded-trade result.

The backend is authoritative for acceptance, normalization, and calculation.

Frontend validation may provide earlier feedback but cannot override backend validation.

---

## 6.2 LONG Trade Behavior

When:

```text
tradeType = LONG
```

the system must calculate:

```text
Dollar P&L =
(Exit Price - Entry Price) × Number of Shares
```

and:

```text
Percentage Return =
((Exit Price - Entry Price) / Entry Price) × 100
```

---

## 6.3 SHORT Trade Behavior

When:

```text
tradeType = SHORT
```

the system must calculate:

```text
Dollar P&L =
(Entry Price - Exit Price) × Number of Shares
```

and:

```text
Percentage Return =
((Entry Price - Exit Price) / Entry Price) × 100
```

---

## 6.4 Performance Interpretation

After the appropriate LONG or SHORT calculation:

- positive P&L/return means profit;
- negative P&L/return means loss;
- zero P&L/return means break-even.

---

## 6.5 Persistence Behavior

On successful processing, the system persists:

- generated trade identifier;
- ticker;
- trade type;
- entry date;
- entry price;
- exit date;
- exit price;
- number of shares.

The system does not persist dollar P&L or percentage return as independent authoritative fields.

Those values are derived from the persisted trade facts.

---

# 7. Business Rules

The following approved Business Rules apply directly to Feature 001:

| Rule ID | Behavior |
|---|---|
| BR-001 | Feature 001 supports completed stock trades |
| BR-002 | Trade type is LONG or SHORT |
| BR-003 | Ticker is required |
| BR-004 | Ticker is normalized to uppercase |
| BR-005 | Entry price > 0 |
| BR-006 | Exit price > 0 |
| BR-007 | Shares > 0 and fractional shares are allowed |
| BR-008 | Entry date is required |
| BR-009 | Exit date is required |
| BR-010 | Exit date >= entry date; same-day trades are valid |
| BR-011 | LONG dollar P&L formula |
| BR-012 | SHORT dollar P&L formula |
| BR-013 | LONG percentage-return formula |
| BR-014 | SHORT percentage-return formula |
| BR-015 | Positive/negative/zero result interpretation |
| BR-016 | Completed trades only |
| BR-017 | Manual trade entry |
| BR-018 | Duplicate valid manual submissions are recorded separately |
| BR-019 | Trim ticker whitespace before uppercase normalization |

The Specification does not redefine these rules.

---

# 8. Validation Rules

## VAL-001 — Ticker Required

Ticker must:

- be present;
- not be blank.

If ticker is missing or blank, the trade must not be recorded.

---

## VAL-002 — Trade Type Required

Trade type must be present and must be one of:

```text
LONG
SHORT
```

Any other value is invalid.

---

## VAL-003 — Entry Date Required

Entry date must be present.

---

## VAL-004 — Exit Date Required

Exit date must be present.

---

## VAL-005 — Valid Date Order

The required relationship is:

```text
exitDate >= entryDate
```

If:

```text
exitDate < entryDate
```

the trade must be rejected.

Same-day trades are valid.

---

## VAL-006 — Entry Price Greater Than Zero

Entry price must satisfy:

```text
entryPrice > 0
```

Zero and negative values are invalid.

---

## VAL-007 — Exit Price Greater Than Zero

Exit price must satisfy:

```text
exitPrice > 0
```

Zero and negative values are invalid.

---

## VAL-008 — Number of Shares Greater Than Zero

Number of shares must satisfy:

```text
numberOfShares > 0
```

Zero and negative values are invalid.

---

## VAL-009 — Fractional Shares Allowed

Fractional share quantities must be accepted within the approved Data Model representation.

Examples of valid conceptual quantities include:

```text
1
10
2.5
0.75
0.123456
```

---

## VAL-010 — Ticker Normalization

Leading and trailing whitespace must be removed from ticker input before final non-blank validation and uppercase normalization.

Examples:

```text
aapl → AAPL
"  aapl  " → AAPL
```

If trimming results in an empty value, the ticker is invalid.

No external ticker-existence validation is required.

---

## VAL-011 — Approved Decimal Scale

Trade input must fit the approved persisted scale.

The system must reject:

- entry price values with more than 4 fractional decimal places;
- exit price values with more than 4 fractional decimal places;
- number-of-shares values with more than 6 fractional decimal places.

The system must not silently round these authoritative submitted trade facts.

---

## 8.1 Duplicate Submission Behavior

Feature 001 does not perform automatic duplicate detection.

If the same valid completed-trade information is submitted more than once, each successful submission creates a separate trade with its own generated identifier.

This behavior applies to manual Feature 001 submissions only and does not define future import-deduplication behavior.

---

# 9. API Behavior

Feature 001 exposes trade creation through:

```text
POST /api/v1/trades
```

The `/api/v1` prefix is the approved initial path-based API versioning convention.

No additional trade endpoint is required by Feature 001.

## 9.1 Request Contract

The request body contains:

```text
ticker
tradeType
entryDate
entryPrice
exitDate
exitPrice
numberOfShares
```

Dates use ISO local-date form `YYYY-MM-DD`.

## 9.2 Successful Creation

A successfully recorded trade returns:

```text
201 Created
```

The response body contains:

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

The returned ticker is the normalized authoritative ticker value.

Derived performance values are calculated authoritatively with `BigDecimal` without premature precision reduction. Percentage-return division uses intermediate scale 10 with `HALF_UP` rounding. In the API response:

- `dollarPnl` is serialized to 2 decimal places using `HALF_UP`;
- `percentageReturn` is serialized to 4 decimal places using `HALF_UP`.

The frontend displays dollar P&L and percentage return with exactly 2 fractional decimal places. Currency symbol, locale, and thousands-grouping style are non-contractual presentation choices unless already established by the repository. These rules apply only to derived performance and do not change approved price or share input/persistence precision.

## 9.3 Validation Failure

A request that violates an approved Feature 001 validation rule returns:

```text
400 Bad Request
```

The trade must not be persisted.

The response contains:

```text
message
fieldErrors
```

`fieldErrors` associates invalid request fields with user-meaningful validation messages.

Exact human-readable validation wording is intentionally non-contractual. Messages must be understandable, but tests and clients must rely on the `message` / `fieldErrors` structure and field association rather than verbatim prose.

## 9.4 Technical Failure

An unexpected technical or persistence failure that prevents successful completion returns:

```text
500 Internal Server Error
```

The response contains:

```text
message
```

The message must be safe for client exposure and must not reveal stack traces, credentials, or sensitive runtime details.

Exact human-readable technical-error wording is intentionally non-contractual. The client-safe `message` structure and failure status are contractual; verbatim prose is not.

---

# 10. Request Parameters / Data

The conceptual Feature 001 request contains:

```text
ticker
tradeType
entryDate
entryPrice
exitDate
exitPrice
numberOfShares
```

## Conceptual Types

| Field | Conceptual Representation |
|---|---|
| ticker | String |
| tradeType | LONG / SHORT |
| entryDate | Date |
| entryPrice | Decimal |
| exitDate | Date |
| exitPrice | Decimal |
| numberOfShares | Decimal |

Detailed wire-format behavior belongs to OpenAPI after contract decisions are finalized.

---

# 11. Success Response Behavior

On successful trade creation, the system must provide enough information for the frontend to present:

- the recorded trade;
- normalized ticker;
- trade type;
- entry information;
- exit information;
- share quantity;
- generated identifier;
- dollar P&L;
- percentage return.

The success representation must reflect the accepted backend state.

The system must not present a successful result if persistence fails.

---

# 12. Empty-Result Behavior

Feature 001 is a create/record workflow rather than a query workflow.

There is therefore no meaningful empty-result case equivalent to an empty search result.

A valid trade submission either:

- succeeds and creates a trade; or
- fails and produces an error outcome.

---

# 13. Error Responses

Feature 001 must distinguish invalid client input from unexpected technical failure at the behavioral level.

## 13.1 Validation Failure

When submitted input violates an approved validation rule:

- the trade must not be persisted;
- the user must receive an error outcome;
- the application must not report success.

The HTTP status and payload shape are defined by Section 9.3 and must be reflected consistently in OpenAPI.

## 13.2 Persistence or Technical Failure

When the backend cannot complete the operation because of a technical or persistence failure:

- the application must not report success;
- incomplete state must not be presented as a successfully recorded trade;
- internal stack traces or sensitive runtime details must not be exposed to the user.

The external technical-error contract is defined by Section 9.4 and must be reflected consistently in OpenAPI.

---

# 14. Examples

## Example 1 — Profitable LONG Trade

Input:

```text
Ticker: AAPL
Trade Type: LONG
Entry Date: 2026-09-01
Entry Price: 220
Exit Date: 2026-09-15
Exit Price: 230
Number of Shares: 10
```

Normalized ticker:

```text
AAPL
```

Dollar P&L:

```text
(230 - 220) × 10 = 100
```

Percentage Return:

```text
((230 - 220) / 220) × 100
≈ 4.5454...
```

Behavioral interpretation:

```text
Profit
```

The API serializes this percentage return to 4 decimal places, and the UI displays it with 2 decimal places.

---

## Example 2 — Losing LONG Trade

Input:

```text
Ticker: AAPL
Trade Type: LONG
Entry Price: 220
Exit Price: 210
Number of Shares: 10
```

Dollar P&L:

```text
(210 - 220) × 10 = -100
```

Percentage Return:

```text
((210 - 220) / 220) × 100
≈ -4.5454...
```

Behavioral interpretation:

```text
Loss
```

---

## Example 3 — Profitable SHORT Trade

Input:

```text
Ticker: AAPL
Trade Type: SHORT
Entry Price: 100
Exit Price: 90
Number of Shares: 10
```

Dollar P&L:

```text
(100 - 90) × 10 = 100
```

Percentage Return:

```text
((100 - 90) / 100) × 100 = 10
```

Behavioral interpretation:

```text
Profit
```

---

## Example 4 — Losing SHORT Trade

Input:

```text
Ticker: AAPL
Trade Type: SHORT
Entry Price: 100
Exit Price: 110
Number of Shares: 10
```

Dollar P&L:

```text
(100 - 110) × 10 = -100
```

Percentage Return:

```text
((100 - 110) / 100) × 100 = -10
```

Behavioral interpretation:

```text
Loss
```

---

## Example 5 — Break-Even Trade

For either LONG or SHORT:

```text
Entry Price: 100
Exit Price: 100
```

Dollar P&L:

```text
0
```

Percentage Return:

```text
0
```

Behavioral interpretation:

```text
Break-even
```

---

## Example 6 — Same-Day Trade

Input:

```text
Entry Date: 2026-09-28
Exit Date: 2026-09-28
```

Expected behavior:

```text
Valid
```

Same-day trades must not be rejected solely because entry and exit dates are equal.

---

## Example 7 — Invalid Date Sequence

Input:

```text
Entry Date: 2026-09-28
Exit Date: 2026-09-27
```

Expected behavior:

```text
Rejected
```

The trade must not be persisted.

---

## Example 8 — Fractional Shares

Input:

```text
Number of Shares: 0.75
```

Expected behavior:

```text
Valid
```

provided all other validation rules pass.

---

# 15. Edge Cases

## EDGE-001 — Lowercase Ticker

Input:

```text
aapl
```

Expected behavior:

```text
stored/displayed trading record uses AAPL
```

---

## EDGE-002 — Blank Ticker

Input:

```text
""
```

or whitespace-only input.

Expected behavior:

```text
Rejected
```

---

## EDGE-003 — Zero Entry Price

Input:

```text
entryPrice = 0
```

Expected behavior:

```text
Rejected
```

---

## EDGE-004 — Negative Entry Price

Input:

```text
entryPrice < 0
```

Expected behavior:

```text
Rejected
```

---

## EDGE-005 — Zero Exit Price

Input:

```text
exitPrice = 0
```

Expected behavior:

```text
Rejected
```

---

## EDGE-006 — Zero Shares

Input:

```text
numberOfShares = 0
```

Expected behavior:

```text
Rejected
```

---

## EDGE-007 — Negative Shares

Input:

```text
numberOfShares < 0
```

Expected behavior:

```text
Rejected
```

---

## EDGE-008 — Unsupported Trade Type

Input:

```text
tradeType = OTHER
```

Expected behavior:

```text
Rejected
```

---

## EDGE-009 — Missing Exit Information

A trade without required exit information is not a completed trade.

Expected behavior:

```text
Rejected
```

---

## EDGE-010 — Maximum Approved Decimal Precision

Values within the approved Data Model precision must be representable.

Values outside approved persistence precision must not be silently altered in a way that changes business meaning.

Values exceeding the approved input scale must be rejected rather than silently rounded.

---

## EDGE-011 — Ticker With Surrounding Whitespace

Input:

```text
"  aapl  "
```

Expected behavior:

```text
AAPL
```

The surrounding whitespace is removed before uppercase normalization.

---

## EDGE-012 — Duplicate Valid Submission

Given the same valid completed-trade information is successfully submitted twice:

Expected behavior:

```text
two separately persisted trades
two distinct generated identifiers
```

Feature 001 does not automatically reject or merge the second submission as a duplicate.

---

## EDGE-013 — Excess Price Scale

Input:

```text
entryPrice = 100.12345
```

Expected behavior:

```text
Rejected
```

because Feature 001 prices support at most 4 fractional decimal places.

---

## EDGE-014 — Excess Share Scale

Input:

```text
numberOfShares = 1.1234567
```

Expected behavior:

```text
Rejected
```

because Feature 001 share quantities support at most 6 fractional decimal places.

---

# 16. Feature Invariants

The following invariants must remain true for every successfully recorded Feature 001 trade:

1. the trade represents a stock trade;
2. the trade is completed;
3. the trade type is `LONG` or `SHORT`;
4. ticker is non-blank;
5. persisted ticker is uppercase;
6. entry price is greater than zero;
7. exit price is greater than zero;
8. number of shares is greater than zero;
9. fractional shares are permitted;
10. entry date is present;
11. exit date is present;
12. exit date is the same as or later than entry date;
13. P&L is calculated according to the approved trade type formula;
14. percentage return is calculated according to the approved trade type formula;
15. performance values are derived from authoritative trade facts;
16. calculated performance is not stored as independent authoritative database state;
17. no external market or brokerage system is required;
18. authentication is not required for Feature 001;
19. leading and trailing ticker whitespace is removed before uppercase normalization;
20. submitted price values do not exceed 4 fractional decimal places;
21. submitted share quantities do not exceed 6 fractional decimal places;
22. repeated valid manual submissions are stored as separate trades rather than automatically deduplicated.

---

# 17. Acceptance Criteria

## AC-001 — Valid LONG Trade

Given a valid completed LONG stock trade,

when the user submits it,

then the system:

- accepts the trade;
- normalizes the ticker;
- calculates LONG dollar P&L;
- calculates LONG percentage return;
- persists the trade facts;
- returns/presents the recorded trade and calculated performance.

---

## AC-002 — Valid SHORT Trade

Given a valid completed SHORT stock trade,

when the user submits it,

then the system:

- accepts the trade;
- normalizes the ticker;
- calculates SHORT dollar P&L;
- calculates SHORT percentage return;
- persists the trade facts;
- returns/presents the recorded trade and calculated performance.

---

## AC-003 — Fractional Shares

Given a valid trade with a positive fractional share quantity,

when the user submits it,

then the trade is accepted if all other validation rules pass.

---

## AC-004 — Same-Day Trade

Given:

```text
exitDate = entryDate
```

when all other input is valid,

then the trade is accepted.

---

## AC-005 — Invalid Date Sequence

Given:

```text
exitDate < entryDate
```

when the user submits the trade,

then:

- the trade is rejected;
- the trade is not persisted;
- the application does not report success.

---

## AC-006 — Invalid Price

Given an entry price or exit price that is zero or negative,

when the user submits the trade,

then:

- the trade is rejected;
- the trade is not persisted.

---

## AC-007 — Invalid Share Quantity

Given:

```text
numberOfShares <= 0
```

when the user submits the trade,

then:

- the trade is rejected;
- the trade is not persisted.

---

## AC-008 — Missing or Blank Ticker

Given a missing or blank ticker,

when the user submits the trade,

then:

- the trade is rejected;
- the trade is not persisted.

---

## AC-009 — Unsupported Trade Type

Given a trade type other than `LONG` or `SHORT`,

when the user submits the trade,

then:

- the trade is rejected;
- the trade is not persisted.

---

## AC-010 — Ticker Normalization

Given:

```text
ticker = aapl
```

when a valid trade is recorded,

then the authoritative stored ticker is:

```text
AAPL
```

---

## AC-011 — Profitable Trade Interpretation

Given a trade whose approved calculation produces a positive result,

then the trade performance represents a profit.

---

## AC-012 — Losing Trade Interpretation

Given a trade whose approved calculation produces a negative result,

then the trade performance represents a loss.

---

## AC-013 — Break-Even Interpretation

Given a trade whose approved calculation produces zero,

then the trade performance represents break-even.

---

## AC-014 — Persistence Failure

Given valid trade input,

when the backend cannot successfully persist the trade,

then:

- the operation must not be reported as successful;
- the frontend must not present the trade as successfully recorded.

---

## AC-015 — No External Dependency Required

Given the Feature 001 application is running,

then recording a trade must not require a brokerage, market-data provider, authentication provider, or other external service.

---

## AC-016 — Ticker Whitespace Normalization

Given `ticker = "  aapl  "`, when an otherwise valid trade is recorded, then the authoritative ticker is `AAPL`.

---

## AC-017 — Duplicate Valid Submission

Given the same valid completed-trade information has already been recorded, when the user submits that valid trade information again, then:

- the second submission is accepted;
- a second trade is persisted;
- the second trade receives its own generated identifier.

---

## AC-018 — Excess Decimal Scale

Given a submitted price has more than 4 fractional decimal places or a submitted share quantity has more than 6 fractional decimal places, when the user submits the trade, then:

- the trade is rejected;
- the value is not silently rounded into an accepted authoritative trade;
- the trade is not persisted.

---

# 18. Non-Goals

Feature 001 does not attempt to solve:

- complete trade-history analysis;
- portfolio management;
- live market monitoring;
- automatic enrichment;
- strategy analytics;
- indicator analytics;
- charting;
- AI recommendations;
- brokerage connectivity;
- authentication;
- multi-user data isolation;
- deployment architecture.

These are separate future concerns.

---

# 19. Contract Decisions

Exact human-readable validation and technical-error wording is intentionally non-contractual. Wording must be understandable but does not need to be frozen verbatim before implementation completion.

The following contract decisions are now approved by this Specification:

- path-based API versioning using `/api/v1`;
- `POST /api/v1/trades` for Feature 001 trade creation;
- `201 Created` for successful creation;
- `400 Bad Request` for validation failure;
- `500 Internal Server Error` for unexpected technical/persistence failure;
- request fields defined in Section 9.1;
- success-response fields defined in Section 9.2;
- validation-error structure defined in Section 9.3;
- technical-error structure defined in Section 9.4;
- rejection rather than silent rounding when submitted price/share values exceed approved persisted scale;
- percentage-return division at intermediate scale 10 using `HALF_UP`;
- API dollar P&L serialization at 2 decimal places using `HALF_UP`;
- API percentage-return serialization at 4 decimal places using `HALF_UP`;
- UI dollar P&L display with exactly 2 fractional decimal places;
- UI percentage-return display with exactly 2 fractional decimal places.

---

# 20. Traceability

## 20.1 User Story Traceability

| User Story | Requirements | Business Rules |
|---|---|---|
| US-001 | FR-001–FR-024 | BR-001–BR-019 as applicable |
| US-002 | FR-001–FR-024 | BR-001–BR-019 as applicable |
| US-003 | FR-004–FR-011, FR-024, TEST-004, SEC-002 | BR-003–BR-010, BR-019 |

## 20.2 Behavioral Traceability

| Specification Area | Upstream Authority |
|---|---|
| Completed stock trade scope | Intended System, BR-001, BR-016 |
| Manual entry | BR-017 |
| Duplicate submission behavior | BR-018 |
| Ticker whitespace normalization | BR-019 |
| LONG/SHORT support | BR-002 |
| Ticker behavior | BR-003, BR-004 |
| Price validation | BR-005, BR-006 |
| Share validation | BR-007 |
| Date validation | BR-008, BR-009, BR-010 |
| LONG P&L | BR-011 |
| SHORT P&L | BR-012 |
| LONG return | BR-013 |
| SHORT return | BR-014 |
| Profit/loss interpretation | BR-015 |
| Persistence model | Data Model, FR-017–FR-019 |
| Layering/API boundary | Architecture, Constitution |
| No auth | Research, Constitution, SEC-001 |
| No external integrations | Intended System, Research, INT-001 |

---

# 21. Artifact Relationships

## Upstream Inputs

This Specification is derived from:

- `trading_journal_project_brief.md`
- `specs/001-record-trade/supporting/intended-system.md`
- `specs/001-record-trade/supporting/business-rules.md`
- `specs/001-record-trade/research.md`
- `.specify/memory/constitution.md`
- `specs/001-record-trade/supporting/architecture.md`
- `specs/001-record-trade/data-model.md`
- `specs/001-record-trade/supporting/requirements.md`
- `specs/shared/frontend/spec.md` for project-wide frontend presentation and interaction conventions

## Downstream Consumers

This Specification must be consumed by:

- `specs/001-record-trade/plan.md`
- `specs/001-record-trade/tasks.md`
- `specs/001-record-trade/contracts/openapi.yaml`
- `specs/001-record-trade/supporting/test-spec.md`
- `specs/001-record-trade/supporting/traceability-matrix.md`
- `specs/001-record-trade/supporting/implementation-build-prompt.md`
- implementation and review activities

## Authority Boundary

This Specification is authoritative for observable Feature 001 behavior.

It is subject to:

- Constitution;
- approved Business Rules;
- Supporting Requirements;
- Intended System scope;
- approved Data Model;
- the shared frontend specification within its presentation and interaction authority.

It is not authoritative for:

- project-wide governance;
- exact code structure;
- package placement;
- implementation sequencing;
- infrastructure beyond approved scope;
- detailed task breakdown;
- project-wide frontend presentation conventions.

The shared frontend specification cannot override Feature 001 business behavior, API contracts, validation, calculations, precision rules, or acceptance criteria defined by this package.

## Conflict Handling

If this Specification conflicts with:

- Constitution;
- Business Rules;
- Supporting Requirements;
- Intended System;
- approved Data Model;

the conflict must be surfaced and reconciled before Plan generation.

The Specification must not silently override upstream authority.

Downstream artifacts must not silently reinterpret approved Specification behavior.

---

# 22. Review Status

**Status:** Human reviewed and approved as authoritative input to `plan.md`.

The Specification was generated for review, reconciled against its approved upstream artifacts, and then approved to drive Plan development. Later downstream artifacts do not alter this approval and must remain consistent with this Specification.
