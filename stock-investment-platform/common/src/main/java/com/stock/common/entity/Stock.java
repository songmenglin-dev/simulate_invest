package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class Stock extends BaseEntity {
    private String stockCode;
    private String stockName;
    private String industry;
    private Integer marketType; // 1-A股，2-港股，3-美股
    private LocalDate listDate;
    private Integer status; // 1-正常，0-停牌
}