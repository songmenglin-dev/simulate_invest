package com.stock.market.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StockQuote {
    private String stockCode;
    private String stockName;
    private BigDecimal currentPrice;
    private BigDecimal change;
    private BigDecimal changePercent;
    private Long volume;
    private BigDecimal turnover;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private LocalDate date;
}