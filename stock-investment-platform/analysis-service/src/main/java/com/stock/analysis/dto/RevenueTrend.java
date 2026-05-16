package com.stock.analysis.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class RevenueTrend {
    private String stockCode;
    private List<LocalDate> dates;
    private List<BigDecimal> revenues;
    private List<BigDecimal> growthRates;
}