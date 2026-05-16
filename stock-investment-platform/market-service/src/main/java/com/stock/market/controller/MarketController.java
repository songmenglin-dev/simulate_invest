package com.stock.market.controller;

import com.stock.common.entity.Stock;
import com.stock.market.dto.KLineData;
import com.stock.market.dto.StockQuote;
import com.stock.market.dto.TechnicalIndicators;
import com.stock.market.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/market")
public class MarketController {

    @Autowired
    private MarketService marketService;

    @GetMapping("/search")
    public Map<String, Object> searchStocks(@RequestParam(required = false) String keyword) {
        List<Stock> stocks = marketService.searchStocks(keyword);
        return Map.of("code", 200, "message", "success", "data", stocks);
    }

    @GetMapping("/quote/{stockCode}")
    public Map<String, Object> getQuote(@PathVariable String stockCode) {
        StockQuote quote = marketService.getQuote(stockCode);
        return Map.of("code", 200, "message", "success", "data", quote);
    }

    @GetMapping("/kline/{stockCode}")
    public Map<String, Object> getKLineData(@PathVariable String stockCode,
                                            @RequestParam(defaultValue = "daily") String period) {
        KLineData kLineData = marketService.getKLineData(stockCode, period);
        return Map.of("code", 200, "message", "success", "data", kLineData);
    }

    @GetMapping("/indicators/{stockCode}")
    public Map<String, Object> getIndicators(@PathVariable String stockCode,
                                             @RequestParam(defaultValue = "daily") String period) {
        TechnicalIndicators indicators = marketService.calculateIndicators(stockCode, period);
        return Map.of("code", 200, "message", "success", "data", indicators);
    }
}