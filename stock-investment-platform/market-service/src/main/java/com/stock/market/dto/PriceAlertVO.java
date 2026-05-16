package com.stock.market.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PriceAlertVO {
    private Long id;
    private String alertNo;
    private Long userId;
    private String stockCode;
    private String stockName;
    private String alertType;
    private BigDecimal targetPrice;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
