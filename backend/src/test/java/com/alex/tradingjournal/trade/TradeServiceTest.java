package com.alex.tradingjournal.trade;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TradeServiceTest {
    private final TradeRepository repository = mock(TradeRepository.class);
    private final TradeService service = new TradeService(repository, new TradeCalculator());

    @Test void normalizesPersistsAndReturnsFormattedPerformance() throws Exception {
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
            Trade trade = invocation.getArgument(0);
            var id = Trade.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(trade, 7L);
            return trade;
        });
        TradeResponse response = service.create(request("  aapl  ", LocalDate.of(2026, 9, 15)));
        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.ticker()).isEqualTo("AAPL");
        assertThat(response.dollarPnl()).isEqualByComparingTo("100.00").hasScaleOf(2);
        assertThat(response.percentageReturn()).isEqualByComparingTo("4.5455").hasScaleOf(4);
    }

    @Test void rejectsExitBeforeEntryWithoutPersistence() {
        assertThatThrownBy(() -> service.create(request("AAPL", LocalDate.of(2026, 8, 31))))
                .isInstanceOf(TradeValidationException.class);
        verifyNoInteractions(repository);
    }

    @Test void propagatesPersistenceFailure() {
        when(repository.saveAndFlush(any())).thenThrow(new RuntimeException("database unavailable"));
        assertThatThrownBy(() -> service.create(request("AAPL", LocalDate.of(2026, 9, 15))))
                .isInstanceOf(RuntimeException.class);
    }

    @Test void repeatedSameDayFractionalTradeCreatesDistinctResults() throws Exception {
        var nextId = new java.util.concurrent.atomic.AtomicLong(10);
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
            Trade trade = invocation.getArgument(0);
            var id = Trade.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(trade, nextId.getAndIncrement());
            return trade;
        });
        var request = new CreateTradeRequest(" msft ", TradeType.SHORT, LocalDate.of(2026, 9, 1),
                new BigDecimal("100.0000"), LocalDate.of(2026, 9, 1), new BigDecimal("80.0000"),
                new BigDecimal("2.500000"));
        TradeResponse first = service.create(request);
        TradeResponse second = service.create(request);
        assertThat(first.id()).isNotEqualTo(second.id());
        assertThat(first.ticker()).isEqualTo("MSFT");
        assertThat(first.dollarPnl()).isEqualByComparingTo("50.00");
        verify(repository, times(2)).saveAndFlush(any());
    }

    private CreateTradeRequest request(String ticker, LocalDate exitDate) {
        return new CreateTradeRequest(ticker, TradeType.LONG, LocalDate.of(2026, 9, 1), new BigDecimal("220.0000"),
                exitDate, new BigDecimal("230.0000"), new BigDecimal("10.000000"));
    }
}

