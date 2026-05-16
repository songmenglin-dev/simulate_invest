package com.stock.market.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.stock.market.dto.KLineData;
import com.stock.market.dto.StockQuote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class MarketDataClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String QUOTE_URL = "https://push2.eastmoney.com/api/qt/stock/get?" +
            "secid=%s&fields=f43,f44,f45,f46,f47,f48,f50,f51,f52,f57,f58,f116,f117,f162,f167,f168,f169,f170,f171";

    private static final String KLIN_URL = "https://push2his.eastmoney.com/api/qt/stock/kline/get?" +
            "secid=%s&fields1=f1,f2,f3,f4,f5,f6&fields2=f51,f52,f53,f54,f55,f56,f57&klt=%s&fqt=1&end=20500101&lmt=%d";

    @Cacheable(value = "stockQuote", unless = "#result == null")
    public StockQuote fetchQuote(String stockCode) {
        try {
            String market = stockCode.startsWith("6") ? "1" : "0";
            String secid = market + "." + stockCode;

            String response = restTemplate.getForObject(
                    String.format(QUOTE_URL, secid), String.class);

            if (response == null) return null;

            JSONObject json = JSON.parseObject(response);
            JSONObject data = json.getJSONObject("data");
            if (data == null) return null;

            StockQuote quote = new StockQuote();
            quote.setStockCode(stockCode);
            quote.setStockName(data.getString("f58"));
            quote.setCurrentPrice(bd(data, "f43"));
            quote.setChange(bd(data, "f169"));
            quote.setChangePercent(bd(data, "f170"));
            quote.setOpen(bd(data, "f46"));
            quote.setHigh(bd(data, "f44"));
            quote.setLow(bd(data, "f45"));
            quote.setClose(bd(data, "f43"));
            quote.setVolume(data.getLong("f47"));
            quote.setTurnover(bd(data, "f48"));
            quote.setDate(LocalDate.now());

            return quote;
        } catch (Exception e) {
            return null;
        }
    }

    @Cacheable(value = "klineData", unless = "#result == null")
    public KLineData fetchKLine(String stockCode, String period) {
        try {
            String market = stockCode.startsWith("6") ? "1" : "0";
            String secid = market + "." + stockCode;

            String klt = mapPeriod(period);
            int limit = "daily".equals(period) ? 30 : ("weekly".equals(period) ? 20 : 60);

            String response = restTemplate.getForObject(
                    String.format(KLIN_URL, secid, klt, limit), String.class);

            if (response == null) return null;

            JSONObject json = JSON.parseObject(response);
            JSONObject data = json.getJSONObject("data");
            if (data == null) return null;

            JSONArray klines = data.getJSONArray("klines");
            if (klines == null || klines.isEmpty()) return null;

            KLineData kLineData = new KLineData();
            kLineData.setStockCode(stockCode);

            List<String> dates = new ArrayList<>();
            List<BigDecimal> open = new ArrayList<>();
            List<BigDecimal> high = new ArrayList<>();
            List<BigDecimal> low = new ArrayList<>();
            List<BigDecimal> close = new ArrayList<>();
            List<Long> volume = new ArrayList<>();

            for (int i = 0; i < klines.size(); i++) {
                String kline = klines.getString(i);
                String[] parts = kline.split(",");
                if (parts.length >= 7) {
                    dates.add(parts[0]);
                    open.add(new BigDecimal(parts[1]));
                    high.add(new BigDecimal(parts[3]));
                    low.add(new BigDecimal(parts[4]));
                    close.add(new BigDecimal(parts[2]));
                    volume.add(Long.parseLong(parts[5]));
                }
            }

            kLineData.setDates(dates);
            kLineData.setOpen(open);
            kLineData.setHigh(high);
            kLineData.setLow(low);
            kLineData.setClose(close);
            kLineData.setVolume(volume);

            return kLineData;
        } catch (Exception e) {
            return null;
        }
    }

    private String mapPeriod(String period) {
        switch (period) {
            case "weekly": return "102";
            case "monthly": return "103";
            default: return "101";
        }
    }

    private BigDecimal bd(JSONObject json, String key) {
        if (json.get(key) == null) return BigDecimal.ZERO;
        return json.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP);
    }
}