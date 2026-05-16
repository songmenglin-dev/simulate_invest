package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("price_alerts")
public class PriceAlert extends BaseEntity {
    private String alertNo;
    private Long userId;
    private String stockCode;
    private String stockName;
    private String alertType;
    private BigDecimal targetPrice;
    private String status;
}
