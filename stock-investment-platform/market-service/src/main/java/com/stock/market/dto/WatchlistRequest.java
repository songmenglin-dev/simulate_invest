package com.stock.market.dto;

import lombok.Data;

@Data
public class WatchlistRequest {
    private Long userId;
    private String stockCode;
    private String stockName;
}
