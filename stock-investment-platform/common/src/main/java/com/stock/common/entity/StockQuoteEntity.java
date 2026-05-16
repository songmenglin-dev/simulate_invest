package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stock_quotes")
public class StockQuoteEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String stockCode;
    private BigDecimal currentPrice;
    private BigDecimal priceChange;
    private BigDecimal changePercent;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private Long volume;
    private BigDecimal turnover;
    private LocalDateTime updateTime;
}
