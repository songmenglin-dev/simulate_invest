package com.stock.market.service;

import cn.hutool.core.util.StrUtil;
import com.stock.common.entity.Stock;
import com.stock.market.dto.KLineData;
import com.stock.market.dto.StockQuote;
import com.stock.market.dto.TechnicalIndicators;
import com.stock.market.mapper.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarketService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MarketDataClient marketDataClient;

    // 模拟股票数据（实际应从东方财富/新浪API获取）
    private static final Map<String, StockQuote> MOCK_QUOTES = new HashMap<>();

    static {
        initMockData();
    }

    private static void initMockData() {
        String[][] stocks = {
            {"600036", "招商银行", "35.50"},
            {"600519", "贵州茅台", "1680.00"},
            {"000858", "五粮液", "145.30"},
            {"601318", "中国平安", "48.50"},
            {"000001", "平安银行", "12.30"},
            {"600887", "伊利股份", "28.90"},
            {"000333", "美的集团", "58.20"},
            {"002594", "比亚迪", "268.00"}
        };

        Random random = new Random();
        for (String[] stock : stocks) {
            StockQuote quote = new StockQuote();
            quote.setStockCode(stock[0]);
            quote.setStockName(stock[1]);
            BigDecimal basePrice = new BigDecimal(stock[2]);
            quote.setCurrentPrice(basePrice);

            BigDecimal change = basePrice.multiply(new BigDecimal(random.nextDouble() * 0.1 - 0.05));
            quote.setChange(change.setScale(2, RoundingMode.HALF_UP));
            quote.setChangePercent(change.divide(basePrice, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));

            quote.setOpen(basePrice.multiply(new BigDecimal("0.98")).setScale(2, RoundingMode.HALF_UP));
            quote.setHigh(basePrice.multiply(new BigDecimal("1.05")).setScale(2, RoundingMode.HALF_UP));
            quote.setLow(basePrice.multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_UP));
            quote.setClose(basePrice);
            quote.setVolume((long) (random.nextInt(10000000) + 1000000));
            quote.setTurnover(basePrice.multiply(new BigDecimal(quote.getVolume())).setScale(2, RoundingMode.HALF_UP));
            quote.setDate(LocalDate.now());

            MOCK_QUOTES.put(stock[0], quote);
        }
    }

    public List<Stock> searchStocks(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return stockMapper.selectList(null);
        }

        List<Stock> allStocks = stockMapper.selectList(null);
        return allStocks.stream()
                .filter(s -> s.getStockCode().contains(keyword) || s.getStockName().contains(keyword))
                .collect(Collectors.toList());
    }

    public StockQuote getQuote(String stockCode) {
        // Try real API first
        StockQuote quote = marketDataClient.fetchQuote(stockCode);
        if (quote != null) {
            return quote;
        }

        // Fall back to mock data
        Stock stock = stockMapper.selectList(null).stream()
                .filter(s -> s.getStockCode().equals(stockCode))
                .findFirst()
                .orElse(null);

        quote = MOCK_QUOTES.get(stockCode);
        if (quote == null) {
            quote = new StockQuote();
            quote.setStockCode(stockCode);
            quote.setStockName(stock != null ? stock.getStockName() : "未知");
            quote.setCurrentPrice(new BigDecimal("10.00"));
            quote.setChange(BigDecimal.ZERO);
            quote.setChangePercent(BigDecimal.ZERO);
        }
        return quote;
    }

    public KLineData getKLineData(String stockCode, String period) {
        // Try real API first
        KLineData kLineData = marketDataClient.fetchKLine(stockCode, period);
        if (kLineData != null) {
            return kLineData;
        }

        // Fall back to mock data
        kLineData = new KLineData();
        kLineData.setStockCode(stockCode);

        List<String> dates = new ArrayList<>();
        List<BigDecimal> open = new ArrayList<>();
        List<BigDecimal> high = new ArrayList<>();
        List<BigDecimal> low = new ArrayList<>();
        List<BigDecimal> close = new ArrayList<>();
        List<Long> volume = new ArrayList<>();

        StockQuote quote = MOCK_QUOTES.get(stockCode);
        BigDecimal basePrice = quote != null ? quote.getCurrentPrice() : new BigDecimal("10.00");

        Random random = new Random();
        LocalDate today = LocalDate.now();

        int days = "daily".equals(period) ? 30 : ("weekly".equals(period) ? 20 : 60);

        for (int i = days; i > 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.toString());

            BigDecimal base = basePrice.multiply(new BigDecimal("0.9")).add(
                    basePrice.multiply(new BigDecimal("0.2")).multiply(new BigDecimal(random.nextDouble())));

            open.add(base.setScale(2, RoundingMode.HALF_UP));
            high.add(base.multiply(new BigDecimal("1.05")).setScale(2, RoundingMode.HALF_UP));
            low.add(base.multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_UP));
            close.add(base.setScale(2, RoundingMode.HALF_UP));
            volume.add((long) (random.nextInt(10000000) + 1000000));
        }

        kLineData.setDates(dates);
        kLineData.setOpen(open);
        kLineData.setHigh(high);
        kLineData.setLow(low);
        kLineData.setClose(close);
        kLineData.setVolume(volume);

        return kLineData;
    }

    public TechnicalIndicators calculateIndicators(String stockCode, String period) {
        KLineData kLineData = getKLineData(stockCode, period);
        List<BigDecimal> closes = kLineData.getClose();

        TechnicalIndicators indicators = new TechnicalIndicators();

        // 计算MA
        indicators.setMa5(calculateMA(closes, 5));
        indicators.setMa10(calculateMA(closes, 10));
        indicators.setMa20(calculateMA(closes, 20));

        // 计算MACD
        Map<String, BigDecimal> macdResult = calculateMACD(closes);
        indicators.setMacd(macdResult.get("macd"));
        indicators.setSignal(macdResult.get("signal"));
        indicators.setHistogram(macdResult.get("histogram"));

        // 计算KDJ
        Map<String, BigDecimal> kdjResult = calculateKDJ(kLineData);
        indicators.setK(kdjResult.get("k"));
        indicators.setD(kdjResult.get("d"));
        indicators.setJ(kdjResult.get("j"));

        return indicators;
    }

    private Map<String, BigDecimal> calculateMA(List<BigDecimal> prices, int period) {
        Map<String, BigDecimal> ma = new HashMap<>();
        for (int i = 0; i < prices.size(); i++) {
            if (i >= period - 1) {
                BigDecimal sum = BigDecimal.ZERO;
                for (int j = i - period + 1; j <= i; j++) {
                    sum = sum.add(prices.get(j));
                }
                ma.put("day" + i, sum.divide(new BigDecimal(period), 2, RoundingMode.HALF_UP));
            }
        }
        return ma;
    }

    private Map<String, BigDecimal> calculateMACD(List<BigDecimal> prices) {
        Map<String, BigDecimal> result = new HashMap<>();

        // 计算EMA(12)和EMA(26)
        BigDecimal ema12 = prices.get(0);
        BigDecimal ema26 = prices.get(0);

        for (int i = 1; i < prices.size(); i++) {
            ema12 = ema12.multiply(new BigDecimal("11")).add(prices.get(i)).divide(new BigDecimal("13"), 2, RoundingMode.HALF_UP);
            ema26 = ema26.multiply(new BigDecimal("25")).add(prices.get(i)).divide(new BigDecimal("27"), 2, RoundingMode.HALF_UP);
        }

        BigDecimal macd = ema12.subtract(ema26);
        result.put("macd", macd);
        result.put("signal", macd.multiply(new BigDecimal("0.8")));
        result.put("histogram", macd.subtract(macd.multiply(new BigDecimal("0.8"))));

        return result;
    }

    private Map<String, BigDecimal> calculateKDJ(KLineData kLineData) {
        Map<String, BigDecimal> result = new HashMap<>();

        List<BigDecimal> highs = kLineData.getHigh();
        List<BigDecimal> lows = kLineData.getLow();
        List<BigDecimal> closes = kLineData.getClose();

        int n = 9;
        if (closes.size() < n) {
            result.put("k", new BigDecimal("50"));
            result.put("d", new BigDecimal("50"));
            result.put("j", new BigDecimal("50"));
            return result;
        }

        BigDecimal k = new BigDecimal("50");
        BigDecimal d = new BigDecimal("50");
        BigDecimal jValue = new BigDecimal("50");

        for (int i = n - 1; i < closes.size(); i++) {
            BigDecimal maxHigh = highs.get(i);
            BigDecimal minLow = lows.get(i);

            for (int idx = i - n + 1; idx <= i; idx++) {
                if (highs.get(idx).compareTo(maxHigh) > 0) maxHigh = highs.get(idx);
                if (lows.get(idx).compareTo(minLow) < 0) minLow = lows.get(idx);
            }

            BigDecimal rsv = maxHigh.subtract(minLow).compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : closes.get(i).subtract(minLow).divide(maxHigh.subtract(minLow), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

            k = rsv.multiply(new BigDecimal("1")).add(k.multiply(new BigDecimal("2"))).divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
            d = k.multiply(new BigDecimal("1")).add(d.multiply(new BigDecimal("2"))).divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
            jValue = k.multiply(new BigDecimal("3")).subtract(d.multiply(new BigDecimal("2")));
        }

        result.put("k", k);
        result.put("d", d);
        result.put("j", jValue);

        return result;
    }
}