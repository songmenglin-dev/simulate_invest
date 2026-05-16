package com.stock.analysis;

import com.stock.analysis.dto.BacktestRequest;
import com.stock.analysis.dto.StrategyTemplateVO;
import com.stock.analysis.engine.BacktestEngine;
import com.stock.analysis.mapper.BacktestResultMapper;
import com.stock.analysis.mapper.BacktestStrategyMapper;
import com.stock.analysis.mapper.StockKLineMapper;
import com.stock.analysis.service.BacktestService;
import com.stock.common.entity.BacktestResult;
import com.stock.common.entity.StockKLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("回测服务测试")
class BacktestServiceTest {

    @Mock
    private BacktestEngine backtestEngine;
    @Mock
    private StockKLineMapper stockKLineMapper;
    @Mock
    private BacktestStrategyMapper strategyMapper;
    @Mock
    private BacktestResultMapper resultMapper;

    @InjectMocks
    private BacktestService service;

    private BacktestRequest request;
    private BacktestResult mockResult;

    @BeforeEach
    void setUp() {
        request = new BacktestRequest();
        request.setUserId(1L);
        request.setStrategyType("MA_CROSSOVER");
        request.setStockCode("600519");
        request.setStockName("贵州茅台");
        request.setStartDate("2023-01-01");
        request.setEndDate("2023-12-31");
        request.setInitialCapital(new BigDecimal("100000"));
        Map<String, Object> params = new HashMap<>();
        params.put("fast", 5);
        params.put("slow", 20);
        request.setParameters(params);

        mockResult = new BacktestResult();
        mockResult.setId(1L);
        mockResult.setResultNo("BTR001");
        mockResult.setStrategyType("MA_CROSSOVER");
        mockResult.setStockCode("600519");
        mockResult.setStockName("贵州茅台");
        mockResult.setInitialCapital(new BigDecimal("100000"));
        mockResult.setFinalCapital(new BigDecimal("115000"));
        mockResult.setTotalReturn(new BigDecimal("15.00"));
        mockResult.setAnnualReturn(new BigDecimal("15.00"));
        mockResult.setMaxDrawdown(new BigDecimal("8.50"));
        mockResult.setWinRate(new BigDecimal("60.00"));
        mockResult.setTotalTrades(10);
        mockResult.setWinningTrades(6);
        mockResult.setSharpeRatio(new BigDecimal("1.25"));
        mockResult.setEquityCurveJson("[{\"date\":\"2023-01-03\",\"value\":100000}]");
        mockResult.setTradesJson("[]");
    }

    @Test
    @DisplayName("回测执行成功返回结果")
    void runBacktest_success_returnsResult() {
        List<StockKLine> klineData = generateMockKLines(200);
        when(stockKLineMapper.selectList(any())).thenReturn(klineData);
        when(backtestEngine.run(any(), anyList())).thenReturn(mockResult);

        BacktestResult result = service.runBacktest(request);

        assertNotNull(result);
        assertEquals("MA_CROSSOVER", result.getStrategyType());
        assertEquals("600519", result.getStockCode());
        verify(resultMapper).insert(any(BacktestResult.class));
        verify(strategyMapper).insert(any());
    }

    @Test
    @DisplayName("日期范围超过3年抛出异常")
    void runBacktest_dateRangeExceeded_throwsException() {
        request.setStartDate("2020-01-01");
        request.setEndDate("2024-12-31");

        assertThrows(IllegalArgumentException.class, () -> service.runBacktest(request));
        verify(backtestEngine, never()).run(any(), any());
    }

    @Test
    @DisplayName("开始日期晚于结束日期抛出异常")
    void runBacktest_startAfterEnd_throwsException() {
        request.setStartDate("2023-12-31");
        request.setEndDate("2023-01-01");

        assertThrows(IllegalArgumentException.class, () -> service.runBacktest(request));
    }

    @Test
    @DisplayName("K线数据不足60条抛出异常")
    void runBacktest_insufficientKLine_throwsException() {
        List<StockKLine> klineData = generateMockKLines(30);
        when(stockKLineMapper.selectList(any())).thenReturn(klineData);

        assertThrows(IllegalArgumentException.class, () -> service.runBacktest(request));
    }

    @Test
    @DisplayName("查询回测结果成功")
    void getResult_existingId_returnsResult() {
        when(resultMapper.selectById(1L)).thenReturn(mockResult);

        BacktestResult result = service.getResult(1L);

        assertNotNull(result);
        assertEquals("BTR001", result.getResultNo());
    }

    @Test
    @DisplayName("查询不存在的回测结果抛出异常")
    void getResult_nonexistentId_throwsException() {
        when(resultMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> service.getResult(999L));
    }

    @Test
    @DisplayName("获取历史记录返回列表")
    void getHistory_returnsList() {
        when(resultMapper.selectList(any())).thenReturn(Arrays.asList(mockResult));

        List<BacktestResult> history = service.getHistory(1L);

        assertEquals(1, history.size());
        assertEquals("BTR001", history.get(0).getResultNo());
    }

    @Test
    @DisplayName("获取策略模板返回4种策略")
    void getStrategyTemplates_returnsFourStrategies() {
        List<StrategyTemplateVO> templates = service.getStrategyTemplates();

        assertEquals(4, templates.size());
        assertEquals("MA_CROSSOVER", templates.get(0).getType());
        assertEquals("MACD", templates.get(1).getType());
        assertEquals("MOMENTUM", templates.get(2).getType());
        assertEquals("BOLLINGER", templates.get(3).getType());
    }

    private List<StockKLine> generateMockKLines(int count) {
        List<StockKLine> data = new ArrayList<>();
        LocalDate date = LocalDate.of(2023, 1, 1);
        BigDecimal price = new BigDecimal("100");

        for (int i = 0; i < count; i++) {
            StockKLine bar = new StockKLine();
            bar.setStockCode("000001");
            bar.setTradeDate(date);
            bar.setOpenPrice(price);
            bar.setClosePrice(price.add(new BigDecimal("0.5")));
            bar.setHighPrice(price.add(new BigDecimal("1")));
            bar.setLowPrice(price.subtract(new BigDecimal("0.5")));
            data.add(bar);

            price = bar.getClosePrice();
            date = date.plusDays(1);
            while (date.getDayOfWeek().getValue() >= 6) {
                date = date.plusDays(1);
            }
        }
        return data;
    }
}
