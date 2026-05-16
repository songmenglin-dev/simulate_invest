package com.stock.portfolio.controller;

import com.stock.portfolio.dto.PositionDetail;
import com.stock.portfolio.dto.PortfolioOverview;
import com.stock.portfolio.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping("/overview/{userId}")
    public Map<String, Object> getOverview(@PathVariable Long userId) {
        PortfolioOverview overview = portfolioService.getOverview(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", overview);
        return result;
    }

    @GetMapping("/positions/{userId}")
    public Map<String, Object> getPositions(@PathVariable Long userId) {
        List<PositionDetail> positions = portfolioService.getPositions(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", positions);
        return result;
    }

    @GetMapping("/cash/{userId}")
    public Map<String, Object> getCashBalance(@PathVariable Long userId) {
        Map<String, Object> cash = portfolioService.getCashBalance(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", cash);
        return result;
    }

    @GetMapping("/position/{userId}/{stockCode}")
    public Map<String, Object> getPositionDetail(@PathVariable Long userId, @PathVariable String stockCode) {
        PositionDetail detail = portfolioService.getPositionDetail(userId, stockCode);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", detail);
        return result;
    }
}