package com.stock.market.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class TechnicalIndicators {
    private Map<String, BigDecimal> ma5;
    private Map<String, BigDecimal> ma10;
    private Map<String, BigDecimal> ma20;
    private Map<String, BigDecimal> macd;
    private Map<String, BigDecimal> signal;
    private Map<String, BigDecimal> histogram;
    private Map<String, BigDecimal> k;
    private Map<String, BigDecimal> d;
    private Map<String, BigDecimal> j;
}