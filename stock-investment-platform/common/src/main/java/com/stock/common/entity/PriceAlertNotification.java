package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("price_alert_notifications")
public class PriceAlertNotification extends BaseEntity {
    private Long userId;
    private Long alertId;
    private String stockCode;
    private String stockName;
    private String alertType;
    private BigDecimal targetPrice;
    private BigDecimal triggeredPrice;
    private Integer isRead;
}
