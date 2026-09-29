package com.alex.tradingjournal.trade;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class TradePostgresIntegrationTest {
    @Container static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17.6-alpine");
    @DynamicPropertySource static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
    @Autowired TradeRepository repository;
    @Autowired JdbcTemplate jdbc;

    @Test void flywaySchemaPersistsDuplicatesWithGeneratedIds() {
        Trade first = repository.saveAndFlush(trade());
        Trade second = repository.saveAndFlush(trade());
        assertThat(first.getId()).isNotNull();
        assertThat(second.getId()).isNotEqualTo(first.getId());
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test void flywaySchemaHasApprovedNumericScalesAndNoDerivedColumns() {
        var columns = jdbc.queryForList("""
                select column_name, data_type, numeric_precision, numeric_scale
                  from information_schema.columns
                 where table_schema = 'public' and table_name = 'trades'
                """);
        assertThat(columns).extracting(row -> row.get("column_name"))
                .contains("id", "ticker", "trade_type", "entry_date", "entry_price", "exit_date", "exit_price", "number_of_shares")
                .doesNotContain("dollar_pnl", "percentage_return");
        assertNumericColumn(columns, "entry_price", 19, 4);
        assertNumericColumn(columns, "exit_price", 19, 4);
        assertNumericColumn(columns, "number_of_shares", 19, 6);
    }

    private void assertNumericColumn(java.util.List<java.util.Map<String, Object>> columns, String name, int precision, int scale) {
        var column = columns.stream().filter(row -> name.equals(row.get("column_name"))).findFirst().orElseThrow();
        assertThat(column.get("data_type")).isEqualTo("numeric");
        assertThat(((Number) column.get("numeric_precision")).intValue()).isEqualTo(precision);
        assertThat(((Number) column.get("numeric_scale")).intValue()).isEqualTo(scale);
    }

    private Trade trade() {
        return new Trade("AAPL", TradeType.LONG, LocalDate.parse("2026-09-01"), new BigDecimal("220.0000"),
                LocalDate.parse("2026-09-15"), new BigDecimal("230.0000"), new BigDecimal("10.000000"));
    }
}
