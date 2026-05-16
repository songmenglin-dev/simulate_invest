package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class Position extends BaseEntity {
    private Long userId;
    private Long fundAccountId;
    private String stockCode;
    private String stockName;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer frozenQuantity;
    private BigDecimal avgCost;
}