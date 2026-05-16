package com.stock.trading.feign.fallback;

import com.stock.trading.feign.MarketFeignClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class MarketFeignClientFallback implements MarketFeignClient {

    @Override
    public Map<String, Object> getQuote(String stockCode) {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("code", 200);
        Map<String, Object> data = new HashMap<>();
        data.put("currentPrice", BigDecimal.ZERO);
        data.put("stockCode", stockCode);
        fallback.put("data", data);
        return fallback;
    }
}