package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class FinancialData extends BaseEntity {
    private String stockCode;
    private Integer reportType; // 1-季报，2-半年报，3-年报
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
    private BigDecimal operatingCashFlow;
    private BigDecimal investingCashFlow;
    private BigDecimal financingCashFlow;
    private BigDecimal netCashFlow;
    private BigDecimal totalShares;
    private BigDecimal dividendYield;
}