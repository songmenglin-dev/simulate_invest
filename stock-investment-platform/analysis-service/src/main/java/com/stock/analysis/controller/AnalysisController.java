package com.stock.analysis.controller;

import com.stock.analysis.dto.FinancialOverview;
import com.stock.analysis.dto.RevenueTrend;
import com.stock.analysis.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/overview/{stockCode}")
    public Map<String, Object> getOverview(@PathVariable String stockCode) {
        FinancialOverview overview = analysisService.getOverview(stockCode);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", overview);
        return result;
    }

    @GetMapping("/income/{stockCode}")
    public Map<String, Object> getIncomeStatement(@PathVariable String stockCode) {
        Map<String, Object> data = analysisService.getIncomeStatement(stockCode);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    @GetMapping("/balance/{stockCode}")
    public Map<String, Object> getBalanceSheet(@PathVariable String stockCode) {
        Map<String, Object> data = analysisService.getBalanceSheet(stockCode);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    @GetMapping("/cashflow/{stockCode}")
    public Map<String, Object> getCashFlow(@PathVariable String stockCode) {
        Map<String, Object> data = analysisService.getCashFlowStatement(stockCode);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    @GetMapping("/revenue-trend/{stockCode}")
    public Map<String, Object> getRevenueTrend(@PathVariable String stockCode) {
        RevenueTrend trend = analysisService.getRevenueTrend(stockCode);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", trend);
        return result;
    }
}