package com.alex.tradingjournal.trade;

import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeService {
    private static final Logger log = LoggerFactory.getLogger(TradeService.class);
    private final TradeRepository repository;
    private final TradeCalculator calculator;

    public TradeService(TradeRepository repository, TradeCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    @Transactional
    public TradeResponse create(CreateTradeRequest request) {
        String ticker = request.ticker().trim().toUpperCase(Locale.ROOT);
        if (ticker.isBlank()) {
            log.info("Trade creation rejected category=validation field=ticker");
            throw new TradeValidationException(Map.of("ticker", "Ticker is required"));
        }
        if (request.exitDate().isBefore(request.entryDate())) {
            log.info("Trade creation rejected category=validation field=exitDate ticker={}", ticker);
            throw new TradeValidationException(Map.of("exitDate", "Exit date must not precede entry date"));
        }

        Trade saved = repository.saveAndFlush(new Trade(ticker, request.tradeType(), request.entryDate(), request.entryPrice(),
                request.exitDate(), request.exitPrice(), request.numberOfShares()));
        TradePerformance performance = calculator.calculate(saved.getTradeType(), saved.getEntryPrice(),
                saved.getExitPrice(), saved.getNumberOfShares());
        log.info("Trade created outcome=success tradeId={} ticker={}", saved.getId(), saved.getTicker());
        return new TradeResponse(saved.getId(), saved.getTicker(), saved.getTradeType(), saved.getEntryDate(),
                saved.getEntryPrice(), saved.getExitDate(), saved.getExitPrice(), saved.getNumberOfShares(),
                performance.dollarPnl().setScale(2, RoundingMode.HALF_UP),
                performance.percentageReturn().setScale(4, RoundingMode.HALF_UP));
    }
}

