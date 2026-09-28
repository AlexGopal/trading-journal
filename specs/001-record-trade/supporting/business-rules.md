# Trading Journal and Analysis App — Business Rules

## Purpose and Ownership

This document records the approved domain and business rules that govern the Trading Journal and Analysis App, with the current rule set focused on Feature 001: recording a completed stock trade and calculating its performance.

These rules define business behavior and constraints. They do not define detailed architecture, API contracts, database design, UI implementation, or implementation tasks.

## Rule Classification

Rules in this document use the following statuses:

- **Approved** — explicitly approved for the current product/feature scope.
- **Proposed** — identified as potentially useful but requires approval before becoming authoritative.
- **Remaining Uncertainty** — a business decision has not yet been made.

The current Feature 001 rules below are approved unless otherwise stated.

## Approved Business Rules

### BR-001 — Supported Initial Asset Type

**Rule Statement:**  
Feature 001 supports completed **stock trades**.

**Source or Approval Basis:**  
Approved project scope and Intended System.

**Status:** Approved

**Notes / Uncertainty:**  
Additional asset or trade types may be supported by future features, but they are not part of Feature 001.

---

### BR-002 — Supported Trade Direction

**Rule Statement:**  
A recorded trade must identify its trade direction as either:

- `LONG`
- `SHORT`

**Source or Approval Basis:**  
Approved Feature 001 decision.

**Status:** Approved

**Notes / Uncertainty:**  
The selected trade direction determines how dollar profit/loss and percentage return are calculated.

---

### BR-003 — Ticker Required

**Rule Statement:**  
Every recorded trade must include a ticker symbol. The ticker must not be blank.

**Source or Approval Basis:**  
Approved Feature 001 decision.

**Status:** Approved

**Notes / Uncertainty:**  
Detailed ticker-format restrictions beyond being required and non-blank have not yet been approved.

---

### BR-004 — Ticker Normalization

**Rule Statement:**  
Ticker symbols must be normalized to uppercase for the application's stored and displayed trading record.

For example:

```text
aapl → AAPL
```

**Source or Approval Basis:**  
Approved Feature 001 decision.

**Status:** Approved

**Notes / Uncertainty:**  
No external market-data validation of the ticker is required for Feature 001.

---

### BR-005 — Entry Price

**Rule Statement:**  
Every recorded trade must have an entry price greater than zero.

```text
Entry Price > 0
```

**Source or Approval Basis:**  
Approved Feature 001 decision.

**Status:** Approved

**Notes / Uncertainty:**  
None for the current scope.

---

### BR-006 — Exit Price

**Rule Statement:**  
Every recorded completed trade must have an exit price greater than zero.

```text
Exit Price > 0
```

**Source or Approval Basis:**  
Approved Feature 001 decision.

**Status:** Approved

**Notes / Uncertainty:**  
Feature 001 records completed trades, so an exit price is required.

---

### BR-007 — Number of Shares

**Rule Statement:**  
The number of shares for a recorded trade must be greater than zero.

```text
Number of Shares > 0
```

Fractional shares are allowed.

Examples of valid quantities include:

```text
1
10
2.5
0.75
```

**Source or Approval Basis:**  
Approved Feature 001 decision.

**Status:** Approved

**Notes / Uncertainty:**  
The exact numeric precision supported for fractional shares is a downstream data-model and requirements decision.

---

### BR-008 — Entry Date Required

**Rule Statement:**  
Every recorded trade must include an entry date.

**Source or Approval Basis:**  
Approved Feature 001 scope.

**Status:** Approved

**Notes / Uncertainty:**  
Detailed date representation and storage format are downstream technical decisions.

---

### BR-009 — Exit Date Required

**Rule Statement:**  
Every recorded completed trade must include an exit date.

**Source or Approval Basis:**  
Approved Feature 001 scope.

**Status:** Approved

**Notes / Uncertainty:**  
Feature 001 records completed trades; open positions are not part of the current scope.

---

### BR-010 — Valid Trade Date Sequence

**Rule Statement:**  
The exit date must be the same as or later than the entry date.

```text
Exit Date >= Entry Date
```

Same-day trades are valid.

**Source or Approval Basis:**  
Approved Feature 001 decision.

**Status:** Approved

**Notes / Uncertainty:**  
None for the current scope.

---

### BR-011 — Long Trade Dollar Profit/Loss

**Rule Statement:**  
For a `LONG` trade, dollar profit or loss is calculated as:

```text
Dollar P&L = (Exit Price - Entry Price) × Number of Shares
```

**Source or Approval Basis:**  
Approved Feature 001 calculation, extended with the approved LONG/SHORT distinction.

**Status:** Approved

**Notes / Uncertainty:**  
A positive result represents profit, a negative result represents loss, and zero represents break-even.

---

### BR-012 — Short Trade Dollar Profit/Loss

**Rule Statement:**  
For a `SHORT` trade, dollar profit or loss is calculated as:

```text
Dollar P&L = (Entry Price - Exit Price) × Number of Shares
```

**Source or Approval Basis:**  
Approved Feature 001 decision to support short trades.

**Status:** Approved

**Notes / Uncertainty:**  
A positive result represents profit, a negative result represents loss, and zero represents break-even.

---

### BR-013 — Long Trade Percentage Return

**Rule Statement:**  
For a `LONG` trade, percentage return is calculated as:

```text
Percentage Return =
((Exit Price - Entry Price) / Entry Price) × 100
```

**Source or Approval Basis:**  
Approved Feature 001 calculation, extended with the approved LONG/SHORT distinction.

**Status:** Approved

**Notes / Uncertainty:**  
Entry price cannot be zero under BR-005.

---

### BR-014 — Short Trade Percentage Return

**Rule Statement:**  
For a `SHORT` trade, percentage return is calculated as:

```text
Percentage Return =
((Entry Price - Exit Price) / Entry Price) × 100
```

**Source or Approval Basis:**  
Approved Feature 001 decision to support short trades.

**Status:** Approved

**Notes / Uncertainty:**  
Entry price cannot be zero under BR-005.

---

### BR-015 — Profit, Loss, and Break-Even Interpretation

**Rule Statement:**  
Calculated trade performance is interpreted as follows:

- positive P&L / return → profitable trade;
- negative P&L / return → losing trade;
- zero P&L / return → break-even trade.

This interpretation applies after the appropriate LONG or SHORT calculation has been used.

**Source or Approval Basis:**  
Approved Feature 001 decision.

**Status:** Approved

**Notes / Uncertainty:**  
None for the current scope.

---

### BR-016 — Completed Trades Only

**Rule Statement:**  
Feature 001 records completed trades. A trade must therefore contain both entry and exit information before it is recorded as a completed trade.

**Source or Approval Basis:**  
Approved project brief and Intended System scope.

**Status:** Approved

**Notes / Uncertainty:**  
Support for open positions may be considered as a future feature but is not part of Feature 001.

---

### BR-017 — Manual Trade Entry

**Rule Statement:**  
Feature 001 trade information is supplied manually by the user.

**Source or Approval Basis:**  
Approved Intended System decision.

**Status:** Approved

**Notes / Uncertainty:**  
Automatic trade import, brokerage synchronization, and other external trade-ingestion mechanisms are future possibilities and are not part of Feature 001.

## Calculation Examples

### Example 1 — Profitable Long Trade

```text
Ticker: AAPL
Trade Type: LONG
Entry Price: $220
Exit Price: $230
Number of Shares: 10

Dollar P&L:
($230 - $220) × 10 = +$100

Percentage Return:
(($230 - $220) / $220) × 100 ≈ +4.55%
```

### Example 2 — Losing Long Trade

```text
Ticker: AAPL
Trade Type: LONG
Entry Price: $220
Exit Price: $210
Number of Shares: 10

Dollar P&L:
($210 - $220) × 10 = -$100

Percentage Return:
(($210 - $220) / $220) × 100 ≈ -4.55%
```

### Example 3 — Profitable Short Trade

```text
Ticker: AAPL
Trade Type: SHORT
Entry Price: $100
Exit Price: $90
Number of Shares: 10

Dollar P&L:
($100 - $90) × 10 = +$100

Percentage Return:
(($100 - $90) / $100) × 100 = +10%
```

### Example 4 — Losing Short Trade

```text
Ticker: AAPL
Trade Type: SHORT
Entry Price: $100
Exit Price: $110
Number of Shares: 10

Dollar P&L:
($100 - $110) × 10 = -$100

Percentage Return:
(($100 - $110) / $100) × 100 = -10%
```

### Example 5 — Break-Even Trade

For either LONG or SHORT:

```text
Entry Price: $100
Exit Price: $100

Dollar P&L = $0
Percentage Return = 0%
```

## Proposed Rules Requiring Approval

No additional proposed business rules are being promoted into the approved Feature 001 rule set at this time.

Potential future areas that may require new business rules include:

- open positions;
- transaction fees or commissions;
- dividends;
- multiple entries or exits for a single trade;
- additional asset types;
- strategy and setup classification;
- technical-indicator data;
- imported trades;
- brokerage synchronization.

These items are future considerations only and must not be treated as current requirements.

## Remaining Uncertainties

The following details are intentionally unresolved and should be handled by appropriate downstream artifacts or future feature decisions:

- numeric precision and rounding rules for prices, share quantities, P&L, and percentage return;
- maximum permitted ticker length and any detailed ticker-character rules;
- whether future features will support open positions;
- how future features will represent trades with multiple entries or exits;
- how transaction fees, commissions, dividends, or other adjustments would affect performance calculations if later introduced;
- which additional asset or trade types, if any, will be supported after stocks.

These uncertainties do not prevent the approved Feature 001 business rules from being used as upstream input.

## Rule-to-Input Matrix

| Rule ID | Rule Area | Approval Basis |
|---|---|---|
| BR-001 | Initial asset type | Project scope / Intended System |
| BR-002 | LONG / SHORT direction | Approved Feature 001 decision |
| BR-003 | Ticker required | Approved Feature 001 decision |
| BR-004 | Ticker uppercase normalization | Approved Feature 001 decision |
| BR-005 | Entry price > 0 | Approved Feature 001 decision |
| BR-006 | Exit price > 0 | Approved Feature 001 decision |
| BR-007 | Shares > 0; fractional allowed | Approved Feature 001 decision |
| BR-008 | Entry date required | Project scope |
| BR-009 | Exit date required | Project scope |
| BR-010 | Exit date >= entry date | Approved Feature 001 decision |
| BR-011 | LONG dollar P&L | Approved calculation |
| BR-012 | SHORT dollar P&L | Approved Feature 001 decision |
| BR-013 | LONG percentage return | Approved calculation |
| BR-014 | SHORT percentage return | Approved Feature 001 decision |
| BR-015 | Profit/loss/break-even interpretation | Approved Feature 001 decision |
| BR-016 | Completed trades only | Project scope / Intended System |
| BR-017 | Manual trade entry | Intended System |

## Artifact Relationships

### Upstream Inputs

- `trading_journal_project_brief.md`
- `supporting/intended-system.md`
- Approved Feature 001 business-rule decisions captured during project refinement

### Downstream Consumers

This artifact is intended to provide approved domain rules to downstream Greenfield artifacts including:

- Dependency Map when applicable
- Research
- Constitution where a rule has project-wide governance implications
- Architecture
- Data Model
- Supporting Requirements
- Specification
- Test Specification
- Traceability Matrix

### Authority Boundary

This document is authoritative for the approved business and domain rules listed above.

It is not authoritative for:

- detailed system architecture;
- technology selection;
- database schema;
- API contracts;
- UI design;
- implementation tasks;
- detailed feature acceptance criteria.

If a downstream artifact conflicts with an approved business rule, the conflict must be surfaced and resolved rather than silently changing the rule.
