package com.alex.tradingjournal.trade;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class TradeCalculatorTest {
    private final TradeCalculator calculator = new TradeCalculator();

    @Test void calculatesLongAndShortPerformanceAtApprovedPrecision() {
        var longResult = calculator.calculate(TradeType.LONG, bd("220"), bd("230"), bd("10"));
        assertThat(longResult.dollarPnl()).isEqualByComparingTo("100");
        assertThat(longResult.percentageReturn()).isEqualByComparingTo("4.5454545500");
        var shortResult = calculator.calculate(TradeType.SHORT, bd("100"), bd("80"), bd("2.5"));
        assertThat(shortResult.dollarPnl()).isEqualByComparingTo("50");
        assertThat(shortResult.percentageReturn()).isEqualByComparingTo("20.0000000000");
    }

    @Test void preservesLossAndBreakEvenSigns() {
        assertThat(calculator.calculate(TradeType.LONG, bd("10"), bd("9"), bd("1")).dollarPnl()).isNegative();
        assertThat(calculator.calculate(TradeType.SHORT, bd("10"), bd("11"), bd("1")).dollarPnl()).isNegative();
        assertThat(calculator.calculate(TradeType.LONG, bd("10"), bd("10"), bd("1")).dollarPnl()).isZero();
    }

    private BigDecimal bd(String value) { return new BigDecimal(value); }
}

