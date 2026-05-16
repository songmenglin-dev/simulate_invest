package com.stock.portfolio.controller;

import com.stock.portfolio.dto.PositionDetail;
import com.stock.portfolio.dto.PortfolioOverview;
import com.stock.portfolio.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return Map.of("code", 200, "message", "success", "data", overview);
    }

    @GetMapping("/positions/{userId}")
    public Map<String, Object> getPositions(@PathVariable Long userId) {
        List<PositionDetail> positions = portfolioService.getPositions(userId);
        return Map.of("code", 200, "message", "success", "data", positions);
    }

    @GetMapping("/position/{userId}/{stockCode}")
    public Map<String, Object> getPositionDetail(@PathVariable Long userId, @PathVariable String stockCode) {
        PositionDetail detail = portfolioService.getPositionDetail(userId, stockCode);
        return Map.of("code", 200, "message", "success", "data", detail);
    }
}