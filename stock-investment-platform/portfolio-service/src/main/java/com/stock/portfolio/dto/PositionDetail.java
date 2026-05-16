package com.stock.portfolio.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PositionDetail {
    private Long positionId;
    private String stockCode;
    private String stockName;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer frozenQuantity;
    private BigDecimal avgCost;
    private BigDecimal currentPrice;
    private BigDecimal marketValue;
    private BigDecimal profitLoss;
    private BigDecimal profitLossPercent;
}