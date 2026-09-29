package com.alex.tradingjournal.trade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class TradeLoggingTest {
    @Test void logsApprovedEventsWithoutPayloadOrSensitiveValues(CapturedOutput output) throws Exception {
        TradeRepository repository = mock(TradeRepository.class);
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
            Trade trade = invocation.getArgument(0);
            var id = Trade.class.getDeclaredField("id"); id.setAccessible(true); id.set(trade, 9L); return trade;
        });
        TradeService service = new TradeService(repository, new TradeCalculator());
        service.create(new CreateTradeRequest(" aapl ", TradeType.LONG, LocalDate.parse("2026-09-01"),
                new BigDecimal("220.0000"), LocalDate.parse("2026-09-15"), new BigDecimal("230.0000"), new BigDecimal("10.000000")));
        try { service.create(new CreateTradeRequest("AAPL", TradeType.LONG, LocalDate.parse("2026-09-15"),
                new BigDecimal("1"), LocalDate.parse("2026-09-01"), new BigDecimal("1"), BigDecimal.ONE)); }
        catch (TradeValidationException ignored) {}
        new ApiExceptionHandler().technical(new RuntimeException("password=secret"));

        assertThat(output).contains("outcome=success", "tradeId=9", "ticker=AAPL", "category=validation", "category=technical")
                .doesNotContain("password=secret", "220.0000", "230.0000", "10.000000");
    }
}
