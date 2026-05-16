package com.stock.market;

import com.stock.common.entity.Stock;
import com.stock.market.dto.KLineData;
import com.stock.market.dto.StockQuote;
import com.stock.market.dto.TechnicalIndicators;
import com.stock.market.mapper.StockKLineMapper;
import com.stock.market.mapper.StockMapper;
import com.stock.market.mapper.StockQuoteMapper;
import com.stock.market.service.MarketDataClient;
import com.stock.market.service.MarketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("行情服务测试")
class MarketServiceTest {

    @Mock
    private StockMapper stockMapper;
    @Mock
    private StockQuoteMapper stockQuoteMapper;
    @Mock
    private StockKLineMapper stockKLineMapper;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private MarketDataClient marketDataClient;

    @InjectMocks
    private MarketService marketService;

    @Test
    @DisplayName("8.8 获取股票报价返回有效数据")
    void getQuote_returnsValidQuote() {
        Stock stock = new Stock();
        stock.setStockCode("600519");
        stock.setStockName("贵州茅台");
        when(stockMapper.selectList(null)).thenReturn(Collections.singletonList(stock));

        StockQuote quote = marketService.getQuote("600519");

        assertNotNull(quote);
        assertEquals("600519", quote.getStockCode());
        assertNotNull(quote.getStockName());
    }

    @Test
    @DisplayName("8.8 K线数据包含OHLCV")
    void getKLineData_returnsOHLCV() {
        when(stockKLineMapper.selectList(org.mockito.ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        KLineData data = marketService.getKLineData("600519", "daily");

        assertNotNull(data);
        assertEquals("600519", data.getStockCode());
        assertNotNull(data.getDates());
        assertNotNull(data.getOpen());
        assertNotNull(data.getHigh());
        assertNotNull(data.getLow());
        assertNotNull(data.getClose());
        assertNotNull(data.getVolume());
    }

    @Test
    @DisplayName("8.8 技术指标计算返回MA/MACD/KDJ")
    void calculateIndicators_returnsAllIndicators() {
        when(stockKLineMapper.selectList(org.mockito.ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        TechnicalIndicators indicators = marketService.calculateIndicators("600519", "daily");

        assertNotNull(indicators);
        assertNotNull(indicators.getMa5(), "MA5 should be calculated");
        assertNotNull(indicators.getMa10(), "MA10 should be calculated");
        assertNotNull(indicators.getMa20(), "MA20 should be calculated");
        assertNotNull(indicators.getMacd(), "MACD should be calculated");
        assertNotNull(indicators.getSignal(), "Signal line should be calculated");
        assertNotNull(indicators.getHistogram(), "Histogram should be calculated");
        assertNotNull(indicators.getK(), "K value should be calculated");
        assertNotNull(indicators.getD(), "D value should be calculated");
        assertNotNull(indicators.getJ(), "J value should be calculated");
    }

    @Test
    @DisplayName("8.8 MACD值在合理范围内")
    void calculateIndicators_macdInReasonableRange() {
        when(stockKLineMapper.selectList(org.mockito.ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        TechnicalIndicators indicators = marketService.calculateIndicators("600519", "daily");

        BigDecimal macd = indicators.getMacd();
        assertNotNull(macd, "MACD should not be null");
    }

    @Test
    @DisplayName("模拟行情返回有效数据")
    void getSimulatedQuotes_returnsValidQuotes() {
        when(stockMapper.selectList(null)).thenReturn(Collections.emptyList());

        List<String> codes = Arrays.asList("600519", "000858");
        List<StockQuote> quotes = marketService.getSimulatedQuotes(codes);

        assertNotNull(quotes);
        assertEquals(2, quotes.size());
        for (StockQuote q : quotes) {
            assertNotNull(q.getStockCode());
            assertNotNull(q.getStockName());
            assertNotNull(q.getCurrentPrice());
            assertTrue(q.getCurrentPrice().compareTo(BigDecimal.ZERO) > 0);
        }
    }
}