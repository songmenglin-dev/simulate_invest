package com.stock.market.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WatchlistVO {
    private Long id;
    private String stockCode;
    private String stockName;
    private LocalDateTime createTime;
}
