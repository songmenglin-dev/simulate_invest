package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("watchlist")
public class Watchlist extends BaseEntity {
    private Long userId;
    private String stockCode;
    private String stockName;
}
