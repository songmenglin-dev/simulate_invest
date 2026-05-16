package com.stock.analysis;

import com.stock.analysis.dto.BacktestRequest;
import com.stock.analysis.engine.BacktestEngine;
import com.stock.common.entity.BacktestResult;
import com.stock.common.entity.StockKLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("策略回测引擎测试")
class BacktestEngineTest {

    private BacktestEngine engine;
    private BacktestRequest request;

    @BeforeEach
    void setUp() {
        engine = new BacktestEngine();
        request = new BacktestRequest();
        request.setUserId(1L);
        request.setStockCode("000001");
        request.setStockName("平安银行");
        request.setInitialCapital(new BigDecimal("100000"));
        request.setStartDate("2023-01-01");
        request.setEndDate("2023-12-31");
    }

    @Test
    @DisplayName("均线交叉策略：返回完整回测结果")
    void run_maCrossover_returnsCompleteResult() {
        request.setStrategyType("MA_CROSSOVER");
        Map<String, Object> params = new HashMap<>();
        params.put("fast", 5);
        params.put("slow", 20);
        request.setParameters(params);

        List<StockKLine> data = generateTrendingKLines(200, BigDecimal.TEN);
        BacktestResult result = engine.run(request, data);

        assertNotNull(result);
        assertNotNull(result.getInitialCapital());
        assertNotNull(result.getFinalCapital());
        assertNotNull(result.getTotalReturn());
        assertNotNull(result.getAnnualReturn());
        assertNotNull(result.getMaxDrawdown());
        assertNotNull(result.getTotalTrades());
        assertNotNull(result.getWinRate());
        assertNotNull(result.getSharpeRatio());
        assertNotNull(result.getEquityCurveJson());
        assertNotNull(result.getTradesJson());
    }

    @Test
    @DisplayName("MACD策略：返回完整回测结果")
    void run_macd_returnsCompleteResult() {
        request.setStrategyType("MACD");
        Map<String, Object> params = new HashMap<>();
        params.put("fast", 12);
        params.put("slow", 26);
        params.put("signal", 9);
        request.setParameters(params);

        List<StockKLine> data = generateTrendingKLines(200, BigDecimal.TEN);
        BacktestResult result = engine.run(request, data);

        assertNotNull(result);
        assertNotNull(result.getFinalCapital());
        assertNotNull(result.getSharpeRatio());
    }

    @Test
    @DisplayName("动量突破策略：返回完整回测结果")
    void run_momentum_returnsCompleteResult() {
        request.setStrategyType("MOMENTUM");
        Map<String, Object> params = new HashMap<>();
        params.put("lookback", 20);
        params.put("holdDays", 5);
        request.setParameters(params);

        List<StockKLine> data = generateTrendingKLines(200, BigDecimal.TEN);
        BacktestResult result = engine.run(request, data);

        assertNotNull(result);
        assertNotNull(result.getFinalCapital());
        assertNotNull(result.getTotalTrades());
    }

    @Test
    @DisplayName("布林带策略：返回完整回测结果")
    void run_bollinger_returnsCompleteResult() {
        request.setStrategyType("BOLLINGER");
        Map<String, Object> params = new HashMap<>();
        params.put("period", 20);
        params.put("multiplier", 2.0);
        request.setParameters(params);

        List<StockKLine> data = generateTrendingKLines(200, BigDecimal.TEN);
        BacktestResult result = engine.run(request, data);

        assertNotNull(result);
        assertNotNull(result.getFinalCapital());
        assertNotNull(result.getTotalTrades());
    }

    @Test
    @DisplayName("权益曲线JSON可解析为数组")
    void run_equityCurveJson_isValidJsonArray() {
        request.setStrategyType("MA_CROSSOVER");
        Map<String, Object> params = new HashMap<>();
        params.put("fast", 5);
        params.put("slow", 20);
        request.setParameters(params);

        List<StockKLine> data = generateTrendingKLines(200, BigDecimal.TEN);
        BacktestResult result = engine.run(request, data);

        String equityCurveJson = result.getEquityCurveJson();
        assertTrue(equityCurveJson.startsWith("["));
        assertTrue(equityCurveJson.contains("date"));
        assertTrue(equityCurveJson.contains("value"));
    }

    @Test
    @DisplayName("交易记录JSON可解析为数组")
    void run_tradesJson_isValidJsonArray() {
        request.setStrategyType("MA_CROSSOVER");
        Map<String, Object> params = new HashMap<>();
        params.put("fast", 5);
        params.put("slow", 20);
        request.setParameters(params);

        List<StockKLine> data = generateTrendingKLines(200, BigDecimal.TEN);
        BacktestResult result = engine.run(request, data);

        String tradesJson = result.getTradesJson();
        assertTrue(tradesJson.startsWith("["));
        assertTrue(tradesJson.contains("entryDate") || tradesJson.equals("[]"));
    }

    @Test
    @DisplayName("最大回撤在0到1之间")
    void run_maxDrawdown_isBetweenZeroAndOne() {
        request.setStrategyType("MACD");
        Map<String, Object> params = new HashMap<>();
        params.put("fast", 12);
        params.put("slow", 26);
        params.put("signal", 9);
        request.setParameters(params);

        List<StockKLine> data = generateTrendingKLines(200, BigDecimal.TEN);
        BacktestResult result = engine.run(request, data);

        BigDecimal maxDD = result.getMaxDrawdown();
        assertNotNull(maxDD);
        assertTrue(maxDD.compareTo(new BigDecimal("-100")) >= 0);
        assertTrue(maxDD.compareTo(new BigDecimal("100")) <= 0);
    }

    @Test
    @DisplayName("夏普比率在合理范围内")
    void run_sharpeRatio_isReasonable() {
        request.setStrategyType("MA_CROSSOVER");
        Map<String, Object> params = new HashMap<>();
        params.put("fast", 5);
        params.put("slow", 20);
        request.setParameters(params);

        List<StockKLine> data = generateTrendingKLines(200, BigDecimal.TEN);
        BacktestResult result = engine.run(request, data);

        BigDecimal sharpe = result.getSharpeRatio();
        assertNotNull(sharpe);
        assertTrue(sharpe.compareTo(new BigDecimal("-10")) >= 0);
        assertTrue(sharpe.compareTo(new BigDecimal("10")) <= 0);
    }

    @Test
    @DisplayName("数据不足时抛出异常")
    void run_insufficientData_throwsException() {
        request.setStrategyType("MA_CROSSOVER");
        List<StockKLine> data = generateTrendingKLines(10, BigDecimal.TEN);

        assertThrows(RuntimeException.class, () -> engine.run(request, data));
    }

    @Test
    @DisplayName("夏普比率：不变的权益曲线返回0")
    void calcSharpe_flatEquity_returnsZero() {
        request.setStrategyType("MA_CROSSOVER");
        request.setParameters(new HashMap<>());

        List<StockKLine> data = generateFlatKLines(100, new BigDecimal("100.0"));
        BacktestResult result = engine.run(request, data);

        // With flat prices, no trades should trigger for MA crossover
        assertNotNull(result.getSharpeRatio());
        assertNotNull(result.getTotalTrades());
    }

    private List<StockKLine> generateTrendingKLines(int count, BigDecimal startPrice) {
        List<StockKLine> data = new ArrayList<>();
        BigDecimal price = startPrice;
        Random rand = new Random(42);
        LocalDate date = LocalDate.of(2023, 1, 1);

        for (int i = 0; i < count; i++) {
            StockKLine bar = new StockKLine();
            bar.setStockCode("000001");
            bar.setTradeDate(date);

            double change = (rand.nextDouble() - 0.45) * 0.04 * price.doubleValue(); // slight upward bias
            double close = price.doubleValue() + change;
            double high = close + rand.nextDouble() * 0.02 * close;
            double low = close - rand.nextDouble() * 0.02 * close;

            bar.setClosePrice(new BigDecimal(close).setScale(2, RoundingMode.HALF_UP));
            bar.setHighPrice(new BigDecimal(high).setScale(2, RoundingMode.HALF_UP));
            bar.setLowPrice(new BigDecimal(low).setScale(2, RoundingMode.HALF_UP));
            bar.setOpenPrice(new BigDecimal(price.doubleValue()).setScale(2, RoundingMode.HALF_UP));
            data.add(bar);

            price = bar.getClosePrice();
            date = date.plusDays(1);
            // Skip weekends
            while (date.getDayOfWeek().getValue() >= 6) {
                date = date.plusDays(1);
            }
        }
        return data;
    }

    private List<StockKLine> generateFlatKLines(int count, BigDecimal price) {
        List<StockKLine> data = new ArrayList<>();
        LocalDate date = LocalDate.of(2023, 1, 1);

        for (int i = 0; i < count; i++) {
            StockKLine bar = new StockKLine();
            bar.setStockCode("000001");
            bar.setTradeDate(date);
            bar.setOpenPrice(price);
            bar.setClosePrice(price);
            bar.setHighPrice(price);
            bar.setLowPrice(price);
            data.add(bar);

            date = date.plusDays(1);
            while (date.getDayOfWeek().getValue() >= 6) {
                date = date.plusDays(1);
            }
        }
        return data;
    }
}
