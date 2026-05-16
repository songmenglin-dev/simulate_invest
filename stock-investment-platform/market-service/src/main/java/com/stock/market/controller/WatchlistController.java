package com.stock.market.controller;

import com.stock.common.entity.Watchlist;
import com.stock.market.dto.WatchlistQuoteVO;
import com.stock.market.dto.WatchlistRequest;
import com.stock.market.dto.WatchlistVO;
import com.stock.market.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody WatchlistRequest req) {
        Watchlist watchlist = watchlistService.addWatchlist(req);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", watchlist);
        return result;
    }

    @DeleteMapping("/remove/{userId}/{stockCode}")
    public Map<String, Object> remove(@PathVariable Long userId, @PathVariable String stockCode) {
        watchlistService.removeWatchlist(userId, stockCode);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        return result;
    }

    @GetMapping("/{userId}")
    public Map<String, Object> list(@PathVariable Long userId) {
        List<WatchlistVO> list = watchlistService.getWatchlist(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", list);
        return result;
    }

    @GetMapping("/quotes/{userId}")
    public Map<String, Object> quotes(@PathVariable Long userId) {
        List<WatchlistQuoteVO> list = watchlistService.getWatchlistQuotes(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", list);
        return result;
    }
}
