package com.stock.analysis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.analysis.dto.FinancialOverview;
import com.stock.analysis.dto.RevenueTrend;
import com.stock.analysis.mapper.FinancialDataMapper;
import com.stock.common.entity.FinancialData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    @Autowired
    private FinancialDataMapper financialDataMapper;

    private List<FinancialData> queryByStock(String stockCode) {
        QueryWrapper<FinancialData> qw = new QueryWrapper<>();
        qw.eq("stock_code", stockCode).orderByDesc("report_date");
        return financialDataMapper.selectList(qw);
    }

    public FinancialOverview getOverview(String stockCode) {
        List<FinancialData> dataList = queryByStock(stockCode);
        FinancialOverview overview = new FinancialOverview();
        overview.setStockCode(stockCode);

        if (dataList.isEmpty()) {
            overview.setStockName("未知");
            return overview;
        }

        FinancialData latest = dataList.get(0);
        overview.setStockName(getStockName(stockCode));
        overview.setReportDate(latest.getReportDate());
        overview.setRevenue(latest.getRevenue());
        overview.setNetProfit(latest.getNetProfit());
        overview.setTotalAssets(latest.getTotalAssets());
        overview.setTotalLiabilities(latest.getTotalLiabilities());
        overview.setShareholdersEquity(latest.getShareholdersEquity());
        overview.setRoe(latest.getRoe());
        overview.setEps(latest.getEps());
        overview.setPeRatio(latest.getPeRatio());
        overview.setPbRatio(latest.getPbRatio());
        overview.setDividendYield(latest.getDividendYield());

        return overview;
    }

    public Map<String, Object> getIncomeStatement(String stockCode) {
        List<FinancialData> dataList = queryByStock(stockCode);
        Map<String, Object> result = new HashMap<>();
        if (!dataList.isEmpty()) {
            FinancialData latest = dataList.get(0);
            result.put("revenue", latest.getRevenue());
            result.put("netProfit", latest.getNetProfit());
            result.put("eps", latest.getEps());
            result.put("reportDate", latest.getReportDate());
        }
        return result;
    }

    public Map<String, Object> getBalanceSheet(String stockCode) {
        List<FinancialData> dataList = queryByStock(stockCode);
        Map<String, Object> result = new HashMap<>();
        if (!dataList.isEmpty()) {
            FinancialData latest = dataList.get(0);
            result.put("totalAssets", latest.getTotalAssets());
            result.put("totalLiabilities", latest.getTotalLiabilities());
            result.put("shareholdersEquity", latest.getShareholdersEquity());
            result.put("reportDate", latest.getReportDate());
        }
        return result;
    }

    public Map<String, Object> getCashFlowStatement(String stockCode) {
        List<FinancialData> dataList = queryByStock(stockCode);
        Map<String, Object> result = new HashMap<>();
        if (!dataList.isEmpty()) {
            FinancialData latest = dataList.get(0);
            result.put("operatingCashFlow", latest.getOperatingCashFlow());
            result.put("investingCashFlow", latest.getInvestingCashFlow());
            result.put("financingCashFlow", latest.getFinancingCashFlow());
            result.put("netCashFlow", latest.getNetCashFlow());
            result.put("reportDate", latest.getReportDate());
        }
        return result;
    }

    public RevenueTrend getRevenueTrend(String stockCode) {
        RevenueTrend trend = new RevenueTrend();
        trend.setStockCode(stockCode);

        // Get data ordered by date ascending for trend
        QueryWrapper<FinancialData> qw = new QueryWrapper<>();
        qw.eq("stock_code", stockCode).orderByAsc("report_date");
        List<FinancialData> dataList = financialDataMapper.selectList(qw);

        if (dataList.isEmpty()) {
            return trend;
        }

        List<LocalDate> dates = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();
        List<BigDecimal> growthRates = new ArrayList<>();

        for (FinancialData data : dataList) {
            dates.add(data.getReportDate());
            revenues.add(data.getRevenue());
        }

        // Calculate YoY growth rate (same-quarter-last-year comparison)
        int quartersPerYear = 4;
        for (int i = 0; i < revenues.size(); i++) {
            if (i < quartersPerYear) {
                growthRates.add(null);
            } else {
                BigDecimal prevYear = revenues.get(i - quartersPerYear);
                BigDecimal curr = revenues.get(i);
                if (prevYear != null && prevYear.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal growth = curr.subtract(prevYear)
                            .divide(prevYear, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"));
                    growthRates.add(growth);
                } else {
                    growthRates.add(null);
                }
            }
        }

        trend.setDates(dates);
        trend.setRevenues(revenues);
        trend.setGrowthRates(growthRates);

        return trend;
    }

    private String getStockName(String stockCode) {
        switch (stockCode) {
            case "600036": return "招商银行";
            case "600519": return "贵州茅台";
            case "000858": return "五粮液";
            case "601318": return "中国平安";
            case "002594": return "比亚迪";
            case "000333": return "美的集团";
            case "600887": return "伊利股份";
            case "000001": return "平安银行";
            default: return "未知";
        }
    }
}
