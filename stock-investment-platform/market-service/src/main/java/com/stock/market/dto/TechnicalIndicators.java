package com.stock.market.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class TechnicalIndicators {
    private Map<String, BigDecimal> ma5;
    private Map<String, BigDecimal> ma10;
    private Map<String, BigDecimal> ma20;
    private BigDecimal macd;
    private BigDecimal signal;
    private BigDecimal histogram;
    private BigDecimal k;
    private BigDecimal d;
    private BigDecimal j;
}