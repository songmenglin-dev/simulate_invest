package com.stock.portfolio.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PortfolioOverview {
    private BigDecimal totalMarketValue; // 总市值
    private BigDecimal totalProfitLoss;   // 总盈亏
    private BigDecimal profitLossPercent;// 盈亏比例
    private BigDecimal availableCash;    // 可用资金
    private BigDecimal frozenCash;       // 冻结资金
    private BigDecimal totalAssets;      // 总资产
}