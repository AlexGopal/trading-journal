package com.alex.tradingjournal.trade;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTradeRequest(
        @NotBlank String ticker,
        @NotNull TradeType tradeType,
        @NotNull LocalDate entryDate,
        @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 15, fraction = 4) BigDecimal entryPrice,
        @NotNull LocalDate exitDate,
        @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 15, fraction = 4) BigDecimal exitPrice,
        @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 13, fraction = 6) BigDecimal numberOfShares
) {}

