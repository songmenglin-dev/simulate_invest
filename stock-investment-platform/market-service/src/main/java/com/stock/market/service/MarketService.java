package com.stock.market.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.common.entity.Stock;
import com.stock.common.entity.StockKLine;
import com.stock.common.entity.StockQuoteEntity;
import com.stock.market.dto.KLineData;
import com.stock.market.dto.StockQuote;
import com.stock.market.dto.TechnicalIndicators;
import com.stock.market.mapper.StockKLineMapper;
import com.stock.market.mapper.StockMapper;
import com.stock.market.mapper.StockQuoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class MarketService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockQuoteMapper stockQuoteMapper;

    @Autowired
    private StockKLineMapper stockKLineMapper;

    @Autowired
    private MarketDataClient marketDataClient;

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
        QueryWrapper<StockQuoteEntity> qw = new QueryWrapper<>();
        qw.eq("stock_code", stockCode);
        StockQuoteEntity entity = stockQuoteMapper.selectOne(qw);

        StockQuote quote = new StockQuote();
        quote.setStockCode(stockCode);

        Stock stock = stockMapper.selectList(null).stream()
                .filter(s -> s.getStockCode().equals(stockCode))
                .findFirst().orElse(null);

        if (entity != null) {
            quote.setStockName(stock != null ? stock.getStockName() : stockCode);
            quote.setCurrentPrice(entity.getCurrentPrice());
            quote.setChange(entity.getPriceChange());
            quote.setChangePercent(entity.getChangePercent());
            quote.setOpen(entity.getOpenPrice());
            quote.setHigh(entity.getHighPrice());
            quote.setLow(entity.getLowPrice());
            quote.setClose(entity.getClosePrice());
            quote.setVolume(entity.getVolume());
            quote.setTurnover(entity.getTurnover());
        } else if (stock != null) {
            quote.setStockName(stock.getStockName());
            quote.setCurrentPrice(new BigDecimal("10.00"));
            quote.setChange(BigDecimal.ZERO);
            quote.setChangePercent(BigDecimal.ZERO);
        }
        return quote;
    }

    public List<StockQuote> getSimulatedQuotes(List<String> stockCodes) {
        List<StockQuote> results = new ArrayList<>();
        for (String stockCode : stockCodes) {
            QueryWrapper<StockQuoteEntity> qw = new QueryWrapper<>();
            qw.eq("stock_code", stockCode);
            StockQuoteEntity entity = stockQuoteMapper.selectOne(qw);

            BigDecimal basePrice;
            if (entity != null && entity.getCurrentPrice() != null) {
                basePrice = entity.getCurrentPrice();
            } else {
                basePrice = new BigDecimal("10.00");
            }

            double fluctuationPercent = ThreadLocalRandom.current().nextDouble(-1.5, 1.5);
            if (Math.abs(fluctuationPercent) < 0.3) {
                fluctuationPercent = fluctuationPercent >= 0 ? 0.3 : -0.3;
            }
            BigDecimal change = basePrice.multiply(BigDecimal.valueOf(fluctuationPercent / 100.0))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal newPrice = basePrice.add(change);
            BigDecimal changePercent = change.divide(basePrice, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));

            StockQuote quote = new StockQuote();
            quote.setStockCode(stockCode);
            Stock stock = stockMapper.selectList(null).stream()
                    .filter(s -> s.getStockCode().equals(stockCode))
                    .findFirst().orElse(null);
            quote.setStockName(stock != null ? stock.getStockName() : stockCode);
            quote.setCurrentPrice(newPrice);
            quote.setChange(change);
            quote.setChangePercent(changePercent);
            quote.setOpen(basePrice);
            quote.setHigh(newPrice.compareTo(basePrice) > 0 ? newPrice : basePrice);
            quote.setLow(newPrice.compareTo(basePrice) < 0 ? newPrice : basePrice);
            quote.setClose(newPrice);
            quote.setVolume((long) (1000000 + ThreadLocalRandom.current().nextDouble() * 5000000));
            quote.setTurnover(new BigDecimal(quote.getVolume()).multiply(newPrice));

            results.add(quote);
        }
        return results;
    }

    public KLineData getKLineData(String stockCode, String period) {
        // Try remote API first for non-daily periods
        if (!"daily".equals(period)) {
            KLineData remote = marketDataClient.fetchKLine(stockCode, period);
            if (remote != null && remote.getDates() != null && !remote.getDates().isEmpty()) {
                return remote;
            }
        }

        QueryWrapper<StockKLine> qw = new QueryWrapper<>();
        qw.eq("stock_code", stockCode).orderByAsc("trade_date");
        List<StockKLine> entities = stockKLineMapper.selectList(qw);

        KLineData kLineData = new KLineData();
        kLineData.setStockCode(stockCode);

        if (entities.isEmpty()) {
            return kLineData;
        }

        if ("daily".equals(period)) {
            return buildKLineFromEntities(entities);
        }
        return aggregateKLine(entities, "weekly".equals(period));
    }

    private KLineData buildKLineFromEntities(List<StockKLine> entities) {
        KLineData kLineData = new KLineData();
        kLineData.setStockCode(entities.get(0).getStockCode());

        List<String> dates = new ArrayList<>();
        List<BigDecimal> open = new ArrayList<>();
        List<BigDecimal> high = new ArrayList<>();
        List<BigDecimal> low = new ArrayList<>();
        List<BigDecimal> close = new ArrayList<>();
        List<Long> volume = new ArrayList<>();

        for (StockKLine e : entities) {
            dates.add(e.getTradeDate().toString());
            open.add(e.getOpenPrice());
            high.add(e.getHighPrice());
            low.add(e.getLowPrice());
            close.add(e.getClosePrice());
            volume.add(e.getVolume());
        }

        kLineData.setDates(dates);
        kLineData.setOpen(open);
        kLineData.setHigh(high);
        kLineData.setLow(low);
        kLineData.setClose(close);
        kLineData.setVolume(volume);

        return kLineData;
    }

    private KLineData aggregateKLine(List<StockKLine> entities, boolean isWeekly) {
        KLineData result = new KLineData();
        result.setStockCode(entities.get(0).getStockCode());

        List<String> dates = new ArrayList<>();
        List<BigDecimal> open = new ArrayList<>();
        List<BigDecimal> high = new ArrayList<>();
        List<BigDecimal> low = new ArrayList<>();
        List<BigDecimal> close = new ArrayList<>();
        List<Long> volume = new ArrayList<>();

        int i = 0;
        while (i < entities.size()) {
            StockKLine first = entities.get(i);
            java.time.LocalDate startDate = first.getTradeDate();
            java.time.LocalDate endDate = startDate;
            if (isWeekly) {
                endDate = startDate.plusDays(6);
            } else {
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            }

            BigDecimal periodOpen = first.getOpenPrice();
            BigDecimal periodHigh = first.getHighPrice();
            BigDecimal periodLow = first.getLowPrice();
            BigDecimal periodClose = first.getClosePrice();
            long periodVolume = first.getVolume();

            int j = i + 1;
            while (j < entities.size()) {
                StockKLine next = entities.get(j);
                if (next.getTradeDate().isAfter(endDate)) {
                    break;
                }
                if (next.getHighPrice().compareTo(periodHigh) > 0) periodHigh = next.getHighPrice();
                if (next.getLowPrice().compareTo(periodLow) < 0) periodLow = next.getLowPrice();
                periodClose = next.getClosePrice();
                periodVolume += next.getVolume();
                j++;
            }

            dates.add(startDate.toString());
            open.add(periodOpen);
            high.add(periodHigh);
            low.add(periodLow);
            close.add(periodClose);
            volume.add(periodVolume);

            i = j;
        }

        result.setDates(dates);
        result.setOpen(open);
        result.setHigh(high);
        result.setLow(low);
        result.setClose(close);
        result.setVolume(volume);

        return result;
    }

    public TechnicalIndicators calculateIndicators(String stockCode, String period) {
        KLineData kLineData = getKLineData(stockCode, period);
        List<BigDecimal> closes = kLineData.getClose();

        TechnicalIndicators indicators = new TechnicalIndicators();

        indicators.setMa5(calculateMA(closes, 5));
        indicators.setMa10(calculateMA(closes, 10));
        indicators.setMa20(calculateMA(closes, 20));

        Map<String, BigDecimal> macdResult = calculateMACD(closes);
        indicators.setMacd(macdResult.get("macd"));
        indicators.setSignal(macdResult.get("signal"));
        indicators.setHistogram(macdResult.get("histogram"));

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
        if (prices.isEmpty()) {
            result.put("macd", BigDecimal.ZERO);
            result.put("signal", BigDecimal.ZERO);
            result.put("histogram", BigDecimal.ZERO);
            return result;
        }
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
