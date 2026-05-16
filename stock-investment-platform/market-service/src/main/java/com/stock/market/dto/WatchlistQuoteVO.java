package com.stock.market.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class WatchlistQuoteVO extends WatchlistVO {
    private BigDecimal currentPrice;
    private BigDecimal change;
    private BigDecimal changePercent;
}
