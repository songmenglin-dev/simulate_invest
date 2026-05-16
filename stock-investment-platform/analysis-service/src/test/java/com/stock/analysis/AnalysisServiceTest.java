package com.stock.analysis;

import com.stock.analysis.dto.FinancialOverview;
import com.stock.analysis.dto.RevenueTrend;
import com.stock.analysis.mapper.FinancialDataMapper;
import com.stock.analysis.service.AnalysisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("财务分析服务测试")
class AnalysisServiceTest {

    @Mock
    private FinancialDataMapper financialDataMapper;

    @InjectMocks
    private AnalysisService analysisService;

    @Test
    @DisplayName("8.7 财务概览：返回估值指标")
    void getOverview_returnsValuationMetrics() {
        FinancialOverview overview = analysisService.getOverview("600519");

        assertNotNull(overview);
        assertEquals("600519", overview.getStockCode());
        assertNotNull(overview.getStockName());
        assertNotNull(overview.getRevenue());
        assertNotNull(overview.getNetProfit());
    }

    @Test
    @DisplayName("8.7 财务概览：PE和PB计算不为空")
    void getOverview_calculatesPEAndPB() {
        FinancialOverview overview = analysisService.getOverview("600519");

        assertNotNull(overview.getPeRatio(), "PE ratio should be calculated");
        assertNotNull(overview.getPbRatio(), "PB ratio should be calculated");
        assertNotNull(overview.getRoe(), "ROE should be present");
        assertNotNull(overview.getDividendYield(), "Dividend yield should be present");
    }

    @Test
    @DisplayName("8.7 未知股票返回默认数据")
    void getOverview_unknownStock_returnsDefault() {
        FinancialOverview overview = analysisService.getOverview("000000");

        assertNotNull(overview);
        assertEquals("未知", overview.getStockName());
    }

    @Test
    @DisplayName("8.7 利润表返回收入和净利润")
    void getIncomeStatement_returnsRevenueAndProfit() {
        Map<String, Object> result = analysisService.getIncomeStatement("600519");

        assertNotNull(result.get("revenue"));
        assertNotNull(result.get("netProfit"));
        assertNotNull(result.get("eps"));
    }

    @Test
    @DisplayName("8.7 资产负债表返回资产负债数据")
    void getBalanceSheet_returnsAssetsAndLiabilities() {
        Map<String, Object> result = analysisService.getBalanceSheet("600519");

        assertNotNull(result.get("totalAssets"));
        assertNotNull(result.get("totalLiabilities"));
        assertNotNull(result.get("shareholdersEquity"));
    }

    @Test
    @DisplayName("8.7 现金流量表返回现金流数据")
    void getCashFlow_returnsCashFlowData() {
        Map<String, Object> result = analysisService.getCashFlowStatement("600519");

        assertNotNull(result.get("operatingCashFlow"));
        assertNotNull(result.get("investingCashFlow"));
        assertNotNull(result.get("financingCashFlow"));
    }

    @Test
    @DisplayName("8.7 营收趋势包含增长率和历史数据")
    void getRevenueTrend_returnsGrowthRates() {
        RevenueTrend trend = analysisService.getRevenueTrend("600519");

        assertNotNull(trend);
        assertEquals("600519", trend.getStockCode());
        assertFalse(trend.getDates().isEmpty());
        assertFalse(trend.getRevenues().isEmpty());
    }
}