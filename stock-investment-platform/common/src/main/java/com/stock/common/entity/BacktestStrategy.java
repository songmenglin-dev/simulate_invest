package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("backtest_strategies")
public class BacktestStrategy extends BaseEntity {
    private String strategyNo;
    private Long userId;
    private String name;
    private String strategyType;
    private String stockCode;
    private String parameterJson;
}
