package com.stock.market;

import com.stock.market.dto.KLineData;
import com.stock.market.dto.StockQuote;
import com.stock.market.dto.TechnicalIndicators;
import com.stock.market.mapper.StockMapper;
import com.stock.market.service.MarketDataClient;
import com.stock.market.service.MarketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("行情服务测试")
class MarketServiceTest {

    @Mock
    private StockMapper stockMapper;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private MarketDataClient marketDataClient;

    @InjectMocks
    private MarketService marketService;

    @Test
    @DisplayName("8.8 获取股票报价返回有效数据")
    void getQuote_returnsValidQuote() {
        StockQuote quote = marketService.getQuote("600519");

        assertNotNull(quote);
        assertEquals("600519", quote.getStockCode());
        assertNotNull(quote.getStockName());
    }

    @Test
    @DisplayName("8.8 K线数据包含OHLCV")
    void getKLineData_returnsOHLCV() {
        KLineData data = marketService.getKLineData("600519", "daily");

        assertNotNull(data);
        assertEquals("600519", data.getStockCode());
        assertFalse(data.getDates().isEmpty());
        assertFalse(data.getOpen().isEmpty());
        assertFalse(data.getHigh().isEmpty());
        assertFalse(data.getLow().isEmpty());
        assertFalse(data.getClose().isEmpty());
        assertFalse(data.getVolume().isEmpty());
    }

    @Test
    @DisplayName("8.8 技术指标计算返回MA/MACD/KDJ")
    void calculateIndicators_returnsAllIndicators() {
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
        TechnicalIndicators indicators = marketService.calculateIndicators("600519", "daily");

        BigDecimal macd = indicators.getMacd();
        // MACD should be within reasonable bounds for a stock around 1680
        assertTrue(macd.abs().compareTo(new BigDecimal("200")) < 0,
                "MACD should be within reasonable range");
    }
}