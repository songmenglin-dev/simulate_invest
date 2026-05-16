package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("conditional_orders")
public class ConditionalOrder extends BaseEntity {
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
    private String failReason;
}
