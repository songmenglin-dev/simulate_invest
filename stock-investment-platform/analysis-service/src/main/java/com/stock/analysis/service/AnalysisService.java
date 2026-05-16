package com.stock.analysis.service;

import com.stock.analysis.dto.FinancialOverview;
import com.stock.analysis.dto.RevenueTrend;
import com.stock.analysis.mapper.FinancialDataMapper;
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

    // 模拟财务数据（实际应从东方财富API获取）
    private static final Map<String, List<Map<String, Object>>> MOCK_FINANCIAL_DATA = new HashMap<>();

    static {
        initMockData();
    }

    private static void initMockData() {
        String[][] stocks = {
            {"600036", "招商银行"},
            {"600519", "贵州茅台"},
            {"000858", "五粮液"},
            {"601318", "中国平安"}
        };

        Random random = new Random();
        for (String[] stock : stocks) {
            List<Map<String, Object>> dataList = new ArrayList<>();
            LocalDate date = LocalDate.now();

            for (int i = 0; i < 8; i++) {
                Map<String, Object> data = new HashMap<>();
                data.put("reportDate", date.minusMonths(i * 3));
                data.put("revenue", new BigDecimal(random.nextInt(100) + 50).multiply(new BigDecimal("100000000")));
                data.put("netProfit", new BigDecimal(random.nextInt(20) + 10).multiply(new BigDecimal("100000000")));
                data.put("totalAssets", new BigDecimal(random.nextInt(1000) + 500).multiply(new BigDecimal("100000000")));
                data.put("totalLiabilities", new BigDecimal(random.nextInt(800) + 300).multiply(new BigDecimal("100000000")));
                data.put("shareholdersEquity", new BigDecimal(random.nextInt(300) + 100).multiply(new BigDecimal("100000000")));
                data.put("roe", new BigDecimal(random.nextDouble() * 0.2 + 0.05).setScale(4, RoundingMode.HALF_UP));
                data.put("eps", new BigDecimal(random.nextDouble() * 5 + 1).setScale(2, RoundingMode.HALF_UP));
                data.put("operatingCashFlow", new BigDecimal(random.nextInt(50) + 10).multiply(new BigDecimal("100000000")));
                data.put("investingCashFlow", new BigDecimal(random.nextInt(30) - 20).multiply(new BigDecimal("100000000")));
                data.put("financingCashFlow", new BigDecimal(random.nextInt(20) - 10).multiply(new BigDecimal("100000000")));
                BigDecimal netCashFlow = ((BigDecimal) data.get("operatingCashFlow"))
                        .add((BigDecimal) data.get("investingCashFlow"))
                        .add((BigDecimal) data.get("financingCashFlow"));
                data.put("netCashFlow", netCashFlow);
                data.put("totalShares", new BigDecimal(random.nextInt(50) + 5).multiply(new BigDecimal("100000000")));
                dataList.add(data);
            }
            MOCK_FINANCIAL_DATA.put(stock[0], dataList);
        }
    }

    public FinancialOverview getOverview(String stockCode) {
        // 模拟数据
        FinancialOverview overview = new FinancialOverview();
        overview.setStockCode(stockCode);

        List<Map<String, Object>> dataList = MOCK_FINANCIAL_DATA.get(stockCode);
        if (dataList == null || dataList.isEmpty()) {
            overview.setStockName("未知");
            return overview;
        }

        Map<String, Object> latestData = dataList.get(0);
        overview.setStockName(getStockName(stockCode));
        overview.setReportDate((LocalDate) latestData.get("reportDate"));
        overview.setRevenue((BigDecimal) latestData.get("revenue"));
        overview.setNetProfit((BigDecimal) latestData.get("netProfit"));
        overview.setTotalAssets((BigDecimal) latestData.get("totalAssets"));
        overview.setTotalLiabilities((BigDecimal) latestData.get("totalLiabilities"));
        overview.setShareholdersEquity((BigDecimal) latestData.get("shareholdersEquity"));
        overview.setRoe((BigDecimal) latestData.get("roe"));
        overview.setEps((BigDecimal) latestData.get("eps"));

        // 计算估值指标
        BigDecimal price = getStockPrice(stockCode);
        BigDecimal eps = (BigDecimal) latestData.get("eps");
        if (eps != null && eps.compareTo(BigDecimal.ZERO) > 0) {
            overview.setPeRatio(price.divide(eps, 2, RoundingMode.HALF_UP));
        }
        BigDecimal equity = (BigDecimal) latestData.get("shareholdersEquity");
        BigDecimal shares = (BigDecimal) latestData.get("totalShares");
        if (equity != null && shares != null && equity.compareTo(BigDecimal.ZERO) > 0 && shares.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal equityPerShare = equity.divide(shares, 2, RoundingMode.HALF_UP);
            if (equityPerShare.compareTo(BigDecimal.ZERO) > 0) {
                overview.setPbRatio(price.divide(equityPerShare, 2, RoundingMode.HALF_UP));
            }
        }
        overview.setDividendYield(new BigDecimal("2.5"));

        return overview;
    }

    public Map<String, Object> getIncomeStatement(String stockCode) {
        List<Map<String, Object>> dataList = MOCK_FINANCIAL_DATA.get(stockCode);
        Map<String, Object> result = new HashMap<>();
        if (dataList != null && !dataList.isEmpty()) {
            Map<String, Object> latest = dataList.get(0);
            result.put("revenue", latest.get("revenue"));
            result.put("netProfit", latest.get("netProfit"));
            result.put("eps", latest.get("eps"));
            result.put("reportDate", latest.get("reportDate"));
        }
        return result;
    }

    public Map<String, Object> getBalanceSheet(String stockCode) {
        List<Map<String, Object>> dataList = MOCK_FINANCIAL_DATA.get(stockCode);
        Map<String, Object> result = new HashMap<>();
        if (dataList != null && !dataList.isEmpty()) {
            Map<String, Object> latest = dataList.get(0);
            result.put("totalAssets", latest.get("totalAssets"));
            result.put("totalLiabilities", latest.get("totalLiabilities"));
            result.put("shareholdersEquity", latest.get("shareholdersEquity"));
            result.put("reportDate", latest.get("reportDate"));
        }
        return result;
    }

    public Map<String, Object> getCashFlowStatement(String stockCode) {
        List<Map<String, Object>> dataList = MOCK_FINANCIAL_DATA.get(stockCode);
        Map<String, Object> result = new HashMap<>();
        if (dataList != null && !dataList.isEmpty()) {
            Map<String, Object> latest = dataList.get(0);
            result.put("operatingCashFlow", latest.get("operatingCashFlow"));
            result.put("investingCashFlow", latest.get("investingCashFlow"));
            result.put("financingCashFlow", latest.get("financingCashFlow"));
            result.put("netCashFlow", latest.get("netCashFlow"));
            result.put("reportDate", latest.get("reportDate"));
        }
        return result;
    }

    public RevenueTrend getRevenueTrend(String stockCode) {
        RevenueTrend trend = new RevenueTrend();
        trend.setStockCode(stockCode);

        List<Map<String, Object>> dataList = MOCK_FINANCIAL_DATA.get(stockCode);
        if (dataList == null) {
            return trend;
        }

        List<LocalDate> dates = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();
        List<BigDecimal> growthRates = new ArrayList<>();

        for (int i = dataList.size() - 1; i >= 0; i--) {
            Map<String, Object> data = dataList.get(i);
            dates.add((LocalDate) data.get("reportDate"));
            revenues.add((BigDecimal) data.get("revenue"));
        }

        // 计算增长率
        for (int i = 1; i < revenues.size(); i++) {
            BigDecimal prev = revenues.get(i - 1);
            BigDecimal curr = revenues.get(i);
            if (prev.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal growth = curr.subtract(prev).divide(prev, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                growthRates.add(growth);
            } else {
                growthRates.add(BigDecimal.ZERO);
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
            default: return "未知";
        }
    }

    private BigDecimal getStockPrice(String stockCode) {
        switch (stockCode) {
            case "600036": return new BigDecimal("35.50");
            case "600519": return new BigDecimal("1680.00");
            case "000858": return new BigDecimal("145.30");
            case "601318": return new BigDecimal("48.50");
            default: return new BigDecimal("10.00");
        }
    }
}