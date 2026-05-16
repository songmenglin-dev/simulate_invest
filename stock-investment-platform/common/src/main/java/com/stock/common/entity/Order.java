package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {
    private String orderNo;
    private Long userId;
    private Long fundAccountId;
    private String stockCode;
    private String stockName;
    private Integer direction; // 1-买入，2-卖出
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
    private Integer status; // 1-待成交，2-已成交，3-已取消，4-已拒绝
    private Integer orderType; // 1-市价，2-限价
}