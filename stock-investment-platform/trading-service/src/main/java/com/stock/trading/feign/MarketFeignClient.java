package com.stock.trading.feign;

import com.stock.trading.feign.fallback.MarketFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "market-service", fallback = MarketFeignClientFallback.class)
public interface MarketFeignClient {

    @GetMapping("/market/quote/{stockCode}")
    Map<String, Object> getQuote(@PathVariable("stockCode") String stockCode);

    default BigDecimal getCurrentPrice(String stockCode) {
        try {
            Map<String, Object> response = getQuote(stockCode);
            if (response != null && response.get("data") != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.get("currentPrice") != null) {
                    return new BigDecimal(data.get("currentPrice").toString());
                }
            }
        } catch (Exception e) {
            // fallback handled below
        }
        return BigDecimal.ZERO;
    }
}