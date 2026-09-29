package com.alex.tradingjournal.trade;

import java.util.Map;

public class TradeValidationException extends RuntimeException {
    private final Map<String, String> fieldErrors;
    public TradeValidationException(Map<String, String> fieldErrors) {
        super("Validation failed");
        this.fieldErrors = Map.copyOf(fieldErrors);
    }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
}

