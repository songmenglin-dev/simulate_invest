package com.stock.analysis.controller;

import com.alibaba.fastjson2.JSON;
import com.stock.analysis.dto.BacktestRequest;
import com.stock.analysis.dto.StrategyTemplateVO;
import com.stock.analysis.dto.TradeRecordVO;
import com.stock.analysis.service.BacktestService;
import com.stock.common.entity.BacktestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/backtest")
public class BacktestController {

    @Autowired
    private BacktestService backtestService;

    @PostMapping("/run")
    public Map<String, Object> runBacktest(@RequestBody BacktestRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (request.getUserId() == null) {
                result.put("code", 400);
                result.put("message", "userId is required");
                return result;
            }
            if (request.getStrategyType() == null || request.getStrategyType().isEmpty()) {
                result.put("code", 400);
                result.put("message", "strategyType is required");
                return result;
            }
            if (request.getStockCode() == null || request.getStockCode().isEmpty()) {
                result.put("code", 400);
                result.put("message", "stockCode is required");
                return result;
            }
            if (request.getInitialCapital() == null) {
                request.setInitialCapital(new BigDecimal("100000"));
            }

            BacktestResult backtestResult = backtestService.runBacktest(request);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", convertToVO(backtestResult));
        } catch (IllegalArgumentException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "Internal error: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/result/{id}")
    public Map<String, Object> getResult(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            BacktestResult backtestResult = backtestService.getResult(id);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", convertToVO(backtestResult));
        } catch (RuntimeException e) {
            result.put("code", 404);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "Internal error: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/history/{userId}")
    public Map<String, Object> getHistory(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<BacktestResult> history = backtestService.getHistory(userId);
            List<Map<String, Object>> list = new ArrayList<>();
            for (BacktestResult r : history) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", r.getId());
                item.put("resultNo", r.getResultNo());
                item.put("strategyType", r.getStrategyType());
                item.put("stockCode", r.getStockCode());
                item.put("stockName", r.getStockName());
                item.put("startDate", r.getStartDate() != null ? r.getStartDate().toString() : null);
                item.put("endDate", r.getEndDate() != null ? r.getEndDate().toString() : null);
                item.put("totalReturn", r.getTotalReturn());
                item.put("totalTrades", r.getTotalTrades());
                item.put("createTime", r.getCreateTime());
                list.add(item);
            }
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "Internal error: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/strategies/templates")
    public Map<String, Object> getStrategyTemplates() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<StrategyTemplateVO> templates = backtestService.getStrategyTemplates();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", templates);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "Internal error: " + e.getMessage());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToVO(BacktestResult entity) {
        Map<String, Object> vo = new LinkedHashMap<>();
        vo.put("resultNo", entity.getResultNo());
        vo.put("strategyType", entity.getStrategyType());
        vo.put("stockCode", entity.getStockCode());
        vo.put("stockName", entity.getStockName());
        vo.put("startDate", entity.getStartDate() != null ? entity.getStartDate().toString() : null);
        vo.put("endDate", entity.getEndDate() != null ? entity.getEndDate().toString() : null);
        vo.put("initialCapital", entity.getInitialCapital());
        vo.put("finalCapital", entity.getFinalCapital());
        vo.put("totalReturn", entity.getTotalReturn());
        vo.put("annualReturn", entity.getAnnualReturn());
        vo.put("maxDrawdown", entity.getMaxDrawdown());
        vo.put("winRate", entity.getWinRate());
        vo.put("totalTrades", entity.getTotalTrades());
        vo.put("winningTrades", entity.getWinningTrades());
        vo.put("sharpeRatio", entity.getSharpeRatio());

        try {
            if (entity.getEquityCurveJson() != null && !entity.getEquityCurveJson().isEmpty()) {
                List<Map<String, Object>> equityCurve =
                        JSON.parseObject(entity.getEquityCurveJson(), List.class);
                vo.put("equityCurve", equityCurve);
            }
        } catch (Exception e) {
            vo.put("equityCurve", Collections.emptyList());
        }

        try {
            if (entity.getTradesJson() != null && !entity.getTradesJson().isEmpty()) {
                List<Map<String, Object>> tradesRaw =
                        JSON.parseObject(entity.getTradesJson(), List.class);
                List<TradeRecordVO> trades = new ArrayList<>();
                for (Map<String, Object> raw : tradesRaw) {
                    TradeRecordVO trade = new TradeRecordVO();
                    trade.setEntryDate((String) raw.get("entryDate"));
                    trade.setExitDate((String) raw.get("exitDate"));
                    Object ep = raw.get("entryPrice");
                    if (ep != null) trade.setEntryPrice(new BigDecimal(ep.toString()));
                    Object xp = raw.get("exitPrice");
                    if (xp != null) trade.setExitPrice(new BigDecimal(xp.toString()));
                    Object qty = raw.get("quantity");
                    if (qty != null) trade.setQuantity(((Number) qty).intValue());
                    Object ret = raw.get("return");
                    if (ret != null) trade.setReturnPct(new BigDecimal(ret.toString()));
                    Object pnl = raw.get("pnl");
                    if (pnl != null) trade.setPnl(new BigDecimal(pnl.toString()));
                    trades.add(trade);
                }
                vo.put("trades", trades);
            }
        } catch (Exception e) {
            vo.put("trades", Collections.emptyList());
        }

        return vo;
    }
}
