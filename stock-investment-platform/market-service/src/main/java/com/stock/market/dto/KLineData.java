package com.stock.market.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class KLineData {
    private String stockCode;
    private List<BigDecimal> open;
    private List<BigDecimal> high;
    private List<BigDecimal> low;
    private List<BigDecimal> close;
    private List<Long> volume;
    private List<String> dates;
}