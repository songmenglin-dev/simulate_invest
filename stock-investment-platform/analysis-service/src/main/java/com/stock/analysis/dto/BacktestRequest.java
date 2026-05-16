package com.stock.analysis.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class BacktestRequest {
    private Long userId;
    private String strategyType;
    private String stockCode;
    private String stockName;
    private String startDate;
    private String endDate;
    private BigDecimal initialCapital;
    private Map<String, Object> parameters;
}
