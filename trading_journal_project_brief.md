# Trading Journal and Analysis App — Project Brief

## Application Name

**Trading Journal and Analysis App**

## Business Goal / Problem

Individual traders can record their trades, but simply knowing whether an individual trade made or lost money does not make it easy to understand **which trading strategies, setups, and indicators are actually working over time**.

The application will provide a structured way to record completed trades, measure their performance, and eventually analyze trading history to identify patterns in successful and unsuccessful trades.

## Intended User

The initial user is an **individual retail trader** who wants to journal and analyze their own trades.

The first version can be designed as a single-user application, while avoiding unnecessary decisions that would prevent expansion later.

## Core Capabilities / Intended Outcomes

The application should eventually allow a user to:

- Record completed trades.
- Calculate and view trade performance.
- Maintain a history of trades.
- Record information about the strategy or setup used for a trade.
- Record relevant technical-indicator conditions associated with a trade.
- Analyze performance across different strategies, setups, and indicator conditions.
- View useful summaries and visualizations of trading performance.

Potential technical indicators include **MACD, RSI, 9-period moving average, and 200-period moving average**. These are future analysis capabilities rather than requirements for the first feature.

## Initial Feature — Feature 001

**Record a completed trade and calculate its performance.**

For a completed trade, the user should initially be able to provide:

```text
Ticker
Entry Date
Entry Price
Exit Date
Exit Price
Number of Shares
```

The application calculates:

```text
Dollar P&L = (Exit Price - Entry Price) × Number of Shares

Percentage Return = ((Exit Price - Entry Price) / Entry Price) × 100
```

Example:

```text
Ticker: AAPL
Entry Date: September 1, 2026
Entry Price: $220
Exit Date: September 10, 2026
Exit Price: $230
Shares: 10

P&L: +$100
Return: +4.55%
```

Including entry and exit dates provides a foundation for future functionality such as calculating holding periods, filtering trades by time period, and analyzing performance over time.

## Product Direction

The application should be designed so that future features can build on the trade journal rather than requiring the application to be redesigned.

Likely future capabilities include:

```text
Trade history
        ↓
Strategy/setup tracking
        ↓
Technical-indicator data
        ↓
Performance dashboard
        ↓
Strategy and indicator analysis
        ↓
Market-data integration / automatic indicator calculation
        ↓
Potential AI-assisted analysis
```

## Constraints / Expectations

This is a **greenfield, portfolio-quality web application**.

The project should:

- Use a modern frontend and backend architecture.
- Have a clean, professional, responsive user interface.
- Keep frontend and backend responsibilities clearly separated.
- Expose backend functionality through well-defined APIs.
- Use appropriate persistence for trade data.
- Include automated testing appropriate to the application.
- Be maintainable and extensible for future trading-analysis features.
- Use Spec-Driven Development with Spec Kit.
- Use Codex for AI-assisted implementation.
- Use the Spec Kit Review Profiles extension for implementation review.
- Keep the initial implementation intentionally small rather than prematurely implementing future analytics features.

## Initial Non-Goals

Feature 001 does **not** need:

```text
Live market data
Automatic stock-price retrieval
Automatic MACD/RSI calculations
Advanced charting
AI trade recommendations
Automated trading
Brokerage integration
Portfolio synchronization
Multi-user authentication
```

Those can be considered in later features if they support the product direction.

## Technology Decisions

Specific technology choices should be made during the architecture and planning stages rather than being prematurely fixed in the business brief.

Decisions to make later include:

- Frontend framework and frontend architecture.
- UI/component library and design approach.
- Backend architecture.
- Database technology.
- Charting and visualization libraries.
- Testing frameworks and strategy.
- Containerization and deployment approach.
- Exact API design.

This allows the technology stack to be selected based on the application's requirements while keeping the project brief focused on **what the application should accomplish and why**.
