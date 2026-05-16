package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("backtest_results")
public class BacktestResult extends BaseEntity {
    private String resultNo;
    private Long strategyId;
    private Long userId;
    private String stockCode;
    private String stockName;
    private String strategyType;
    private String parameterJson;
    private Date startDate;
    private Date endDate;
    private BigDecimal initialCapital;
    private BigDecimal finalCapital;
    private BigDecimal totalReturn;
    private BigDecimal annualReturn;
    private BigDecimal maxDrawdown;
    private BigDecimal winRate;
    private Integer totalTrades;
    private Integer winningTrades;
    private BigDecimal sharpeRatio;
    private String equityCurveJson;
    private String tradesJson;
}
