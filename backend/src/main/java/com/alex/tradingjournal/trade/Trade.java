package com.alex.tradingjournal.trade;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trades")
public class Trade {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String ticker;
    @Enumerated(EnumType.STRING) @Column(name = "trade_type", nullable = false) private TradeType tradeType;
    @Column(name = "entry_date", nullable = false) private LocalDate entryDate;
    @Column(name = "entry_price", nullable = false, precision = 19, scale = 4) private BigDecimal entryPrice;
    @Column(name = "exit_date", nullable = false) private LocalDate exitDate;
    @Column(name = "exit_price", nullable = false, precision = 19, scale = 4) private BigDecimal exitPrice;
    @Column(name = "number_of_shares", nullable = false, precision = 19, scale = 6) private BigDecimal numberOfShares;

    protected Trade() {}

    public Trade(String ticker, TradeType tradeType, LocalDate entryDate, BigDecimal entryPrice,
                 LocalDate exitDate, BigDecimal exitPrice, BigDecimal numberOfShares) {
        this.ticker = ticker;
        this.tradeType = tradeType;
        this.entryDate = entryDate;
        this.entryPrice = entryPrice;
        this.exitDate = exitDate;
        this.exitPrice = exitPrice;
        this.numberOfShares = numberOfShares;
    }

    public Long getId() { return id; }
    public String getTicker() { return ticker; }
    public TradeType getTradeType() { return tradeType; }
    public LocalDate getEntryDate() { return entryDate; }
    public BigDecimal getEntryPrice() { return entryPrice; }
    public LocalDate getExitDate() { return exitDate; }
    public BigDecimal getExitPrice() { return exitPrice; }
    public BigDecimal getNumberOfShares() { return numberOfShares; }
}

