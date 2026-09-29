package com.alex.tradingjournal.trade;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TradeResponse(Long id, String ticker, TradeType tradeType, LocalDate entryDate,
                            BigDecimal entryPrice, LocalDate exitDate, BigDecimal exitPrice,
                            BigDecimal numberOfShares, BigDecimal dollarPnl, BigDecimal percentageReturn) {}

