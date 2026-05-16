package com.stock.market.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class NotificationVO {
    private Long id;
    private Long alertId;
    private Long userId;
    private String stockCode;
    private String stockName;
    private String alertType;
    private BigDecimal targetPrice;
    private BigDecimal triggeredPrice;
    private Integer isRead;
    private LocalDateTime createTime;
}
