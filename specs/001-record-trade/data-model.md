# Trading Journal and Analysis App — Data Model

## Purpose

This document defines the conceptual domain model and approved data representations for Feature 001 of the Trading Journal and Analysis App.

Feature 001 allows the user to record a completed stock trade and calculate its performance.

This Data Model is derived from the approved Intended System, Business Rules, Research, Constitution, and Architecture.

It is authoritative for the approved conceptual model and data representations described here, subject to the Business Rules and Constitution.

It does not define endpoint behavior, implementation tasks, or detailed acceptance criteria.

---

# 1. Conceptual Domain Model

Feature 001 requires one primary persisted domain concept:

```text
Trade
```

A Trade represents one completed stock trade entered manually by the user.

Each Trade contains the factual information required to describe the completed transaction:

- ticker;
- trade type;
- entry date;
- entry price;
- exit date;
- exit price;
- number of shares.

The system derives performance from those facts.

Calculated performance values are not independent source data for Feature 001.

---

# 2. Entity: Trade

## 2.1 Purpose

`Trade` represents one completed manually entered stock trade.

A Trade must contain both entry and exit information because Feature 001 does not support open positions.

## 2.2 Approved Fields

| Field | Conceptual Type | Required | Persisted | Notes |
|---|---|---:|---:|---|
| `id` | Identifier | Yes | Yes | System-generated numeric identifier |
| `ticker` | Text | Yes | Yes | Stored normalized to uppercase |
| `tradeType` | TradeType | Yes | Yes | `LONG` or `SHORT` |
| `entryDate` | Date | Yes | Yes | Calendar date |
| `entryPrice` | Decimal | Yes | Yes | Must be greater than zero |
| `exitDate` | Date | Yes | Yes | Must be same as or later than entry date |
| `exitPrice` | Decimal | Yes | Yes | Must be greater than zero |
| `numberOfShares` | Decimal | Yes | Yes | Must be greater than zero; fractional shares allowed |
| `dollarPnl` | Decimal | Derived | No | Calculated from persisted trade facts |
| `percentageReturn` | Decimal | Derived | No | Calculated from persisted trade facts |

No additional Feature 001 fields are approved by this Data Model.

---

# 3. Trade Identifier

## Decision

Each Trade uses a system-generated numeric identifier.

### Java representation

```text
Long
```

### PostgreSQL representation

```text
BIGINT
```

with database-generated identity behavior.

## Rationale

The identifier:

- uniquely identifies a persisted trade;
- has no business meaning;
- does not need to encode ticker, dates, direction, or any other domain value;
- provides a simple persistence identity for later retrieval and update-related features if those are approved.

The exact JPA generation annotation and database DDL syntax belong to implementation and migration artifacts.

---

# 4. Trade Type

## Concept

Trade direction is represented as a finite domain value:

```text
LONG
SHORT
```

### Conceptual type

```text
TradeType
```

### Java representation

A Java enum is appropriate.

### PostgreSQL representation

The persisted representation should preserve the approved symbolic values:

```text
LONG
SHORT
```

The exact database mechanism for enforcing the finite set is an implementation/schema decision, but persistence must not silently introduce alternate values.

## Business Rule Mapping

- BR-002 — Supported Trade Direction
- BR-011 — Long Trade Dollar Profit/Loss
- BR-012 — Short Trade Dollar Profit/Loss
- BR-013 — Long Trade Percentage Return
- BR-014 — Short Trade Percentage Return

---

# 5. Ticker

## Concept

Ticker identifies the stock associated with the recorded trade.

### Java representation

```text
String
```

### PostgreSQL representation

Textual column representation.

## Constraints

Ticker:

- is required;
- must not be blank;
- is normalized to uppercase before the trade is persisted;
- is displayed using the normalized uppercase representation.

Example:

```text
aapl → AAPL
```

## Intentionally Unresolved

The following are not yet approved and therefore are not invented here:

- maximum ticker length;
- exact allowed character set;
- exchange-qualified symbol rules;
- validation against an external market-data source.

These remain downstream or future decisions.

## Business Rule Mapping

- BR-003 — Ticker Required
- BR-004 — Ticker Normalization

---

# 6. Dates

Feature 001 uses calendar dates rather than timestamps for entry and exit.

## 6.1 Entry Date

### Java representation

```text
LocalDate
```

### PostgreSQL representation

```text
DATE
```

### Constraints

- required.

## 6.2 Exit Date

### Java representation

```text
LocalDate
```

### PostgreSQL representation

```text
DATE
```

### Constraints

- required;
- must be the same as or later than `entryDate`.

Formally:

```text
exitDate >= entryDate
```

Same-day trades are valid.

## Rationale

The approved Feature 001 inputs are dates, not intraday timestamps.

Introducing times or time zones would add behavior that has not been approved.

## Business Rule Mapping

- BR-008 — Entry Date Required
- BR-009 — Exit Date Required
- BR-010 — Valid Trade Date Sequence

---

# 7. Financial and Quantity Values

Financial and quantity values use exact decimal representations.

Floating-point types such as Java `float` or `double` must not be used for approved financial values.

---

# 8. Entry Price

## Java representation

```text
BigDecimal
```

## PostgreSQL representation

```text
NUMERIC(19,4)
```

## Constraints

```text
entryPrice > 0
```

## Scale Decision

Up to four decimal places are preserved for stock price entry.

This supports prices such as:

```text
100
100.25
100.125
100.1234
```

Values requiring greater precision are outside the approved Feature 001 representation unless the Data Model is intentionally revised.

## Business Rule Mapping

- BR-005 — Entry Price

---

# 9. Exit Price

## Java representation

```text
BigDecimal
```

## PostgreSQL representation

```text
NUMERIC(19,4)
```

## Constraints

```text
exitPrice > 0
```

## Scale Decision

Up to four decimal places are preserved, matching `entryPrice`.

## Business Rule Mapping

- BR-006 — Exit Price

---

# 10. Number of Shares

## Java representation

```text
BigDecimal
```

## PostgreSQL representation

```text
NUMERIC(19,6)
```

## Constraints

```text
numberOfShares > 0
```

Fractional shares are allowed.

Examples:

```text
1
10
2.5
0.75
0.123456
```

## Scale Decision

Up to six decimal places are preserved for share quantity.

This provides explicit fractional-share support while avoiding floating-point representation.

## Business Rule Mapping

- BR-007 — Number of Shares

---

# 11. Derived Performance Values

Feature 001 derives performance from persisted trade facts.

The following values are **not persisted as independent database columns**:

- dollar P&L;
- percentage return.

This avoids maintaining duplicate calculated state that could become inconsistent with entry price, exit price, trade type, or share quantity.

The backend business/application layer is responsible for calculating these values from the authoritative persisted trade facts.

---

# 12. Dollar Profit/Loss

## Conceptual Field

```text
dollarPnl
```

## Java representation

```text
BigDecimal
```

## Persistence

```text
Derived — not persisted
```

## LONG Calculation

```text
(Exit Price - Entry Price) × Number of Shares
```

## SHORT Calculation

```text
(Entry Price - Exit Price) × Number of Shares
```

## Interpretation

- positive → profit;
- negative → loss;
- zero → break-even.

Because the value is derived, the persistence model must not create a second authoritative copy of the calculation.

## Business Rule Mapping

- BR-011 — Long Trade Dollar Profit/Loss
- BR-012 — Short Trade Dollar Profit/Loss
- BR-015 — Profit, Loss, and Break-Even Interpretation

---

# 13. Percentage Return

## Conceptual Field

```text
percentageReturn
```

## Java representation

```text
BigDecimal
```

## Persistence

```text
Derived — not persisted
```

## LONG Calculation

```text
((Exit Price - Entry Price) / Entry Price) × 100
```

## SHORT Calculation

```text
((Entry Price - Exit Price) / Entry Price) × 100
```

Because `entryPrice > 0`, the approved business rules prevent division by zero.

## Business Rule Mapping

- BR-013 — Long Trade Percentage Return
- BR-014 — Short Trade Percentage Return
- BR-015 — Profit, Loss, and Break-Even Interpretation

---

# 14. Calculation Precision and Rounding

The application must calculate financial results using decimal arithmetic.

## Approved Representation Rule

Use `BigDecimal` for:

- entry price;
- exit price;
- number of shares;
- dollar P&L;
- percentage return.

## Internal Calculation

Calculations should preserve sufficient precision during intermediate operations rather than converting values to binary floating-point types.

## Persisted Input Scale

The Feature 001 persisted input scales are:

| Value | PostgreSQL Representation |
|---|---|
| Entry Price | `NUMERIC(19,4)` |
| Exit Price | `NUMERIC(19,4)` |
| Number of Shares | `NUMERIC(19,6)` |

## Derived Output Rounding

The exact number of decimal places presented through:

- REST responses;
- frontend display;
- user-facing currency formatting;
- user-facing percentage formatting

is not frozen by this Data Model.

Those presentation/contract rules belong to Supporting Requirements, Specification, and OpenAPI.

The implementation must not silently convert derived calculations to floating-point values.

---

# 15. Validation Constraints

The conceptual Trade model must satisfy all approved Feature 001 business constraints.

| Constraint | Rule |
|---|---|
| Ticker required | BR-003 |
| Ticker normalized uppercase | BR-004 |
| Entry price > 0 | BR-005 |
| Exit price > 0 | BR-006 |
| Shares > 0 | BR-007 |
| Fractional shares supported | BR-007 |
| Entry date required | BR-008 |
| Exit date required | BR-009 |
| Exit date >= entry date | BR-010 |
| Same-day trade valid | BR-010 |
| Trade type = LONG or SHORT | BR-002 |
| Completed trades only | BR-016 |

Validation may exist at more than one technical layer for usability or integrity, but backend validation remains authoritative.

Database constraints may reinforce approved invariants where appropriate.

Detailed validation response behavior belongs to Specification and OpenAPI.

---

# 16. Persistence Representation

The Feature 001 persistence model contains one primary trade record.

Conceptually:

```text
Trade
────────────────────────────────
id
ticker
trade_type
entry_date
entry_price
exit_date
exit_price
number_of_shares
```

Derived values are intentionally absent:

```text
dollar_pnl          ← calculated
percentage_return   ← calculated
```

## Conceptual Relational Representation

```text
TRADE
────────────────────────────────────────────
id                BIGINT              PK
ticker            text                required
trade_type        text/enum value     required
entry_date        DATE                required
entry_price       NUMERIC(19,4)       required
exit_date         DATE                required
exit_price        NUMERIC(19,4)       required
number_of_shares  NUMERIC(19,6)       required
```

This is a conceptual persistence representation.

Exact:

- table naming;
- column naming conventions;
- constraint names;
- identity syntax;
- migration DDL;
- indexes

belong to implementation/migration planning unless a downstream requirement makes them contractually significant.

---

# 17. Domain Representation

A conceptual Java representation is:

```text
Trade
├── id: Long
├── ticker: String
├── tradeType: TradeType
├── entryDate: LocalDate
├── entryPrice: BigDecimal
├── exitDate: LocalDate
├── exitPrice: BigDecimal
└── numberOfShares: BigDecimal
```

Derived application values:

```text
TradePerformance
├── dollarPnl: BigDecimal
└── percentageReturn: BigDecimal
```

`TradePerformance` represents a conceptual result shape, not necessarily a required Java class name.

The implementation must not treat this example as permission to invent package names or freeze class structure before repository inspection and Plan generation.

---

# 18. API / DTO Representation

The API model must be deliberate and must not expose persistence entities merely for convenience.

At the conceptual level, a trade-creation request will require the approved user-supplied trade facts:

```text
ticker
tradeType
entryDate
entryPrice
exitDate
exitPrice
numberOfShares
```

A result representation may include:

```text
persisted trade facts
+
derived dollarPnl
+
derived percentageReturn
```

Exact:

- DTO names;
- JSON property names;
- request/response separation;
- endpoint payloads;
- status codes;
- error payloads;
- output rounding

remain Specification/OpenAPI decisions.

This Data Model defines the data concepts, not the final API contract.

---

# 19. Relationships and Cardinality

Feature 001 requires no persisted entity relationship beyond the Trade itself.

Conceptually:

```text
User
  │
  │ enters
  ▼
Trade
```

The application is currently single-user, and no persisted `User` entity is required for Feature 001.

Therefore Feature 001 does **not** introduce:

- user-to-trade foreign keys;
- strategy relationships;
- indicator relationships;
- portfolio relationships;
- brokerage-account relationships;
- asset-type inheritance hierarchies.

Those concepts require future approved features before entering the Data Model.

---

# 20. Model Diagram

```text
┌────────────────────────────────────┐
│               Trade                │
├────────────────────────────────────┤
│ id                                 │
│ ticker                             │
│ tradeType                          │
│ entryDate                          │
│ entryPrice                         │
│ exitDate                           │
│ exitPrice                          │
│ numberOfShares                     │
├────────────────────────────────────┤
│ Derived:                           │
│ dollarPnl                          │
│ percentageReturn                   │
└────────────────────────────────────┘
                  │
                  │ calculated using
                  ▼
┌────────────────────────────────────┐
│       Approved Business Rules      │
│ LONG / SHORT performance formulas  │
└────────────────────────────────────┘
```

There are no additional Feature 001 domain relationships.

---

# 21. Future Extensibility Boundary

The broader product direction includes future possibilities such as:

- strategy/setup tracking;
- technical-indicator context;
- trade history analysis;
- dashboards;
- additional asset types;
- trade imports;
- brokerage integration;
- market-data integration;
- AI-assisted analysis.

The Feature 001 Data Model does not create speculative fields, tables, inheritance hierarchies, or relationships for those capabilities.

Future features may extend the model when their requirements are approved.

The current model should evolve through explicit migrations and approved SDD artifacts rather than pre-modeling unknown future structures.

---

# 22. Explicit Non-Modelled Concepts

The following are intentionally absent from Feature 001:

- open-position state;
- transaction fees;
- commissions;
- dividends;
- multiple entries;
- multiple exits;
- strategy;
- setup;
- MACD;
- RSI;
- 9-period moving average;
- 200-period moving average;
- market price history;
- broker identifiers;
- brokerage accounts;
- authentication users;
- portfolios;
- AI-generated analysis.

Their absence is intentional and consistent with approved Feature 001 scope.

---

# 23. Remaining Open Questions

The following remain unresolved because they are not required to freeze the Feature 001 conceptual model:

1. maximum ticker length;
2. detailed ticker character rules;
3. exact API/display rounding for P&L;
4. exact API/display rounding for percentage return;
5. Flyway versus Liquibase;
6. exact PostgreSQL migration DDL;
7. future multi-asset representation;
8. future handling of fees, commissions, dividends, multiple entries, or multiple exits;
9. future user/account relationships if multi-user functionality is approved.

These must not be silently resolved by implementation.

---

# 24. Data Model Decision Summary

| Area | Decision |
|---|---|
| Primary entity | `Trade` |
| Trade identifier | Generated numeric ID |
| Java ID type | `Long` |
| PostgreSQL ID type | `BIGINT` |
| Trade direction | `TradeType` = `LONG` or `SHORT` |
| Ticker | Required uppercase `String` |
| Dates | `LocalDate` / PostgreSQL `DATE` |
| Prices | `BigDecimal` / `NUMERIC(19,4)` |
| Share quantity | `BigDecimal` / `NUMERIC(19,6)` |
| Fractional shares | Supported |
| Dollar P&L | Derived `BigDecimal`, not persisted |
| Percentage return | Derived `BigDecimal`, not persisted |
| Floating-point financial values | Not approved |
| User entity | Not required for Feature 001 |
| Future analytics fields | Not included |
| Future integrations | Not modelled |

---

# 25. Artifact Relationships

## Upstream Inputs

This Data Model is derived from:

- `trading_journal_project_brief.md`
- `specs/001-record-trade/supporting/intended-system.md`
- `specs/001-record-trade/supporting/business-rules.md`
- `specs/001-record-trade/research.md`
- `.specify/memory/constitution.md`
- `specs/001-record-trade/supporting/architecture.md`
- approved Data Model decisions made during Feature 001 refinement

## Downstream Consumers

This Data Model must be consumed by:

- `specs/001-record-trade/supporting/requirements.md`
- `specs/001-record-trade/spec.md`
- `specs/001-record-trade/plan.md`
- `specs/001-record-trade/contracts/openapi.yaml`
- `specs/001-record-trade/tasks.md`
- `specs/001-record-trade/supporting/test-spec.md`
- `specs/001-record-trade/supporting/traceability-matrix.md`
- implementation and review artifacts

## Authority Boundary

This artifact is authoritative for:

- approved Feature 001 data concepts;
- entity and value representation;
- field types;
- persistence representation;
- cardinality;
- derived-field status;
- approved numeric persistence precision and scale.

It is subject to:

- approved Business Rules;
- Intended System scope;
- project Constitution;
- approved Architecture.

It is not authoritative for:

- endpoint behavior;
- HTTP status codes;
- final JSON schemas;
- UI behavior;
- implementation sequencing;
- exact repository package placement;
- feature acceptance criteria.

## Conflict Handling

If this Data Model conflicts with an approved Business Rule, Intended System boundary, Constitution rule, or Architecture constraint, the conflict must be surfaced and corrected before downstream artifacts are frozen.

Supporting Requirements and Specification must not silently redefine the model.

If implementation reveals that the approved representation is unsuitable, the Data Model must be amended and downstream impact reviewed before implementation continues.

---

# 26. Review Status

**Status:** Draft — ready for human review.

Once reviewed and accepted, this Data Model becomes authoritative upstream input for `supporting/requirements.md` and `spec.md`.
