package com.alex.tradingjournal.trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class TradeCalculator {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public TradePerformance calculate(TradeType type, BigDecimal entry, BigDecimal exit, BigDecimal shares) {
        BigDecimal priceDifference = type == TradeType.LONG ? exit.subtract(entry) : entry.subtract(exit);
        BigDecimal dollarPnl = priceDifference.multiply(shares);
        BigDecimal percentageReturn = priceDifference.divide(entry, 10, RoundingMode.HALF_UP).multiply(ONE_HUNDRED);
        return new TradePerformance(dollarPnl, percentageReturn);
    }
}

