package com.stock.market.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PriceAlertRequest {
    private Long userId;
    private String stockCode;
    private String stockName;
    private String alertType;
    private BigDecimal targetPrice;
}
