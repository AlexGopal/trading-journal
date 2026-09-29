package com.alex.tradingjournal.trade;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

class TradeControllerTest {
    private MockMvc mvc;
    private TradeService service;

    @BeforeEach void setUp() {
        service = mock(TradeService.class);
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        mvc = MockMvcBuilders.standaloneSetup(new TradeController(service))
                .setControllerAdvice(new ApiExceptionHandler()).setValidator(new SpringValidatorAdapter(validator)).build();
    }

    @Test void returnsCreatedContract() throws Exception {
        when(service.create(any())).thenReturn(new TradeResponse(1L, "AAPL", TradeType.LONG,
                LocalDate.parse("2026-09-01"), new BigDecimal("220.0000"), LocalDate.parse("2026-09-15"),
                new BigDecimal("230.0000"), new BigDecimal("10.000000"), new BigDecimal("100.00"), new BigDecimal("4.5455")));
        mvc.perform(post("/api/v1/trades").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ticker").value("AAPL"))
                .andExpect(jsonPath("$.dollarPnl").value(100.00))
                .andExpect(jsonPath("$.percentageReturn").value(4.5455));
    }

    @Test void returnsCreatedContractForShortTrade() throws Exception {
        when(service.create(any())).thenReturn(new TradeResponse(2L, "TSLA", TradeType.SHORT,
                LocalDate.parse("2026-09-01"), new BigDecimal("100.0000"), LocalDate.parse("2026-09-01"),
                new BigDecimal("80.0000"), new BigDecimal("2.500000"), new BigDecimal("50.00"), new BigDecimal("20.0000")));
        mvc.perform(post("/api/v1/trades").contentType(MediaType.APPLICATION_JSON)
                        .content(validJson().replace("AAPL", "TSLA").replace("LONG", "SHORT")
                                .replace("220.0000", "100.0000").replace("2026-09-15", "2026-09-01")
                                .replace("230.0000", "80.0000").replace("10.000000", "2.500000")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.tradeType").value("SHORT"))
                .andExpect(jsonPath("$.dollarPnl").value(50.00)).andExpect(jsonPath("$.percentageReturn").value(20.0000));
    }

    @Test void returnsValidationShapeWithoutCallingService() throws Exception {
        mvc.perform(post("/api/v1/trades").contentType(MediaType.APPLICATION_JSON)
                        .content(validJson().replace("\"AAPL\"", "\"   \"")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.fieldErrors.ticker").exists());
        verifyNoInteractions(service);
    }

    @Test void unsupportedTradeTypeIsBadRequest() throws Exception {
        mvc.perform(post("/api/v1/trades").contentType(MediaType.APPLICATION_JSON)
                        .content(validJson().replace("LONG", "SIDEWAYS")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test void rejectsNonpositiveAndExcessScaleNumbersWithoutCallingService() throws Exception {
        for (String invalidJson : new String[] {
                validJson().replace("220.0000", "0"),
                validJson().replace("230.0000", "-1"),
                validJson().replace("10.000000", "0"),
                validJson().replace("220.0000", "220.00001"),
                validJson().replace("10.000000", "10.0000001")
        }) {
            mvc.perform(post("/api/v1/trades").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.fieldErrors").exists());
        }
        verifyNoInteractions(service);
    }

    @Test void rejectsMissingTypeAndDatesWithoutCallingService() throws Exception {
        for (String invalidJson : new String[] {
                validJson().replace("\"tradeType\":\"LONG\"", "\"tradeType\":null"),
                validJson().replace("\"ticker\":\"AAPL\"", "\"ticker\":null"),
                validJson().replace("\"entryDate\":\"2026-09-01\"", "\"entryDate\":null"),
                validJson().replace("\"exitDate\":\"2026-09-15\"", "\"exitDate\":null")
        }) {
            mvc.perform(post("/api/v1/trades").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors").exists());
        }
        verifyNoInteractions(service);
    }

    @Test void technicalFailureIsClientSafe() throws Exception {
        when(service.create(any())).thenThrow(new RuntimeException("password=secret"));
        mvc.perform(post("/api/v1/trades").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isInternalServerError()).andExpect(jsonPath("$.message").value("Unable to record trade"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("secret"))));
    }

    private String validJson() { return """
            {"ticker":"AAPL","tradeType":"LONG","entryDate":"2026-09-01","entryPrice":220.0000,
             "exitDate":"2026-09-15","exitPrice":230.0000,"numberOfShares":10.000000}
            """; }
}

