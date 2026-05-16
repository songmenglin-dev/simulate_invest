package com.stock.analysis.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TradeRecordVO {
    private String entryDate;
    private String exitDate;
    private BigDecimal entryPrice;
    private BigDecimal exitPrice;
    private Integer quantity;
    private BigDecimal returnPct;
    private BigDecimal pnl;
}
