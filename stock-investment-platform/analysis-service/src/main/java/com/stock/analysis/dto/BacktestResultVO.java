package com.stock.analysis.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class BacktestResultVO {
    private String resultNo;
    private String strategyType;
    private String stockCode;
    private String stockName;
    private String startDate;
    private String endDate;
    private BigDecimal initialCapital;
    private BigDecimal finalCapital;
    private BigDecimal totalReturn;
    private BigDecimal annualReturn;
    private BigDecimal maxDrawdown;
    private BigDecimal winRate;
    private Integer totalTrades;
    private Integer winningTrades;
    private BigDecimal sharpeRatio;
    private List<Map<String, Object>> equityCurve;
    private List<TradeRecordVO> trades;
    private String equityCurveJson;
    private String tradesJson;
}
