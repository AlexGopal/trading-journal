package com.alex.tradingjournal.trade;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ValidationErrorResponse beanValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        log.info("Trade creation rejected category=validation fields={}", fields.keySet());
        return new ValidationErrorResponse("Validation failed", fields);
    }

    @ExceptionHandler(TradeValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ValidationErrorResponse tradeValidation(TradeValidationException exception) {
        return new ValidationErrorResponse(exception.getMessage(), exception.getFieldErrors());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ValidationErrorResponse unreadableRequest(HttpMessageNotReadableException exception) {
        log.info("Trade creation rejected category=validation field=request");
        return new ValidationErrorResponse("Validation failed", Map.of("request", "Request body contains an invalid value"));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    TechnicalErrorResponse technical(Exception exception) {
        log.error("Trade creation failed category=technical exceptionType={}", exception.getClass().getSimpleName());
        return new TechnicalErrorResponse("Unable to record trade");
    }
}

