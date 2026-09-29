package com.alex.tradingjournal.trade;

import java.util.Map;

public record ValidationErrorResponse(String message, Map<String, String> fieldErrors) {}

