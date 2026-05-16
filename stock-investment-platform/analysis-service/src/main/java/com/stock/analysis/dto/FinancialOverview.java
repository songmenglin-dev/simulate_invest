package com.stock.analysis.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinancialOverview {
    private String stockCode;
    private String stockName;
    private LocalDate reportDate;
    private BigDecimal revenue;
    private BigDecimal netProfit;
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal shareholdersEquity;
    private BigDecimal roe;
    private BigDecimal eps;
    private BigDecimal peRatio;
    private BigDecimal pbRatio;
    private BigDecimal dividendYield;
}