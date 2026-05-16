package com.stock.analysis.controller;

import com.stock.analysis.dto.FinancialOverview;
import com.stock.analysis.dto.RevenueTrend;
import com.stock.analysis.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/overview/{stockCode}")
    public Map<String, Object> getOverview(@PathVariable String stockCode) {
        FinancialOverview overview = analysisService.getOverview(stockCode);
        return Map.of("code", 200, "message", "success", "data", overview);
    }

    @GetMapping("/income/{stockCode}")
    public Map<String, Object> getIncomeStatement(@PathVariable String stockCode) {
        Map<String, Object> data = analysisService.getIncomeStatement(stockCode);
        return Map.of("code", 200, "message", "success", "data", data);
    }

    @GetMapping("/balance/{stockCode}")
    public Map<String, Object> getBalanceSheet(@PathVariable String stockCode) {
        Map<String, Object> data = analysisService.getBalanceSheet(stockCode);
        return Map.of("code", 200, "message", "success", "data", data);
    }

    @GetMapping("/revenue-trend/{stockCode}")
    public Map<String, Object> getRevenueTrend(@PathVariable String stockCode) {
        RevenueTrend trend = analysisService.getRevenueTrend(stockCode);
        return Map.of("code", 200, "message", "success", "data", trend);
    }
}