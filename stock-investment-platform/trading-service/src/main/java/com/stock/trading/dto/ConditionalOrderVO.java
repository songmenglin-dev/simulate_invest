package com.stock.trading.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ConditionalOrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long fundAccountId;
    private String stockCode;
    private String stockName;
    private String conditionType;
    private BigDecimal triggerPrice;
    private BigDecimal orderPrice;
    private Integer quantity;
    private Integer direction;
    private String status;
    private Long triggeredOrderId;
    private String triggeredOrderNo;
    private String failReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
