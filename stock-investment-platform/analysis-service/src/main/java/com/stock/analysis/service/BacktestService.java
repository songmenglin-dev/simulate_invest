package com.stock.analysis.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.analysis.dto.*;
import com.stock.analysis.engine.BacktestEngine;
import com.stock.analysis.mapper.BacktestResultMapper;
import com.stock.analysis.mapper.BacktestStrategyMapper;
import com.stock.analysis.mapper.StockKLineMapper;
import com.stock.common.entity.BacktestResult;
import com.stock.common.entity.BacktestStrategy;
import com.stock.common.entity.StockKLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class BacktestService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private BacktestEngine backtestEngine;

    @Autowired
    private StockKLineMapper stockKLineMapper;

    @Autowired
    private BacktestStrategyMapper strategyMapper;

    @Autowired
    private BacktestResultMapper resultMapper;

    public BacktestResult runBacktest(BacktestRequest request) {
        // Validate dates
        LocalDate start = LocalDate.parse(request.getStartDate(), DATE_FMT);
        LocalDate end = LocalDate.parse(request.getEndDate(), DATE_FMT);
        if (ChronoUnit.DAYS.between(start, end) > 3L * 365) {
            throw new IllegalArgumentException("Date range must not exceed 3 years");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Load K-line data
        QueryWrapper<StockKLine> qw = new QueryWrapper<>();
        qw.eq("stock_code", request.getStockCode())
                .ge("trade_date", start)
                .le("trade_date", end)
                .orderByAsc("trade_date");
        List<StockKLine> klineData = stockKLineMapper.selectList(qw);

        if (klineData.size() < 60) {
            throw new IllegalArgumentException("Need at least 60 trading days of data, got " + klineData.size());
        }

        // Save strategy
        BacktestStrategy strategy = new BacktestStrategy();
        strategy.setStrategyNo("BTS" + System.currentTimeMillis() + RandomUtil.randomNumbers(4));
        strategy.setUserId(request.getUserId());
        strategy.setName(getStrategyName(request.getStrategyType()));
        strategy.setStrategyType(request.getStrategyType());
        strategy.setStockCode(request.getStockCode());
        strategy.setParameterJson(request.getParameters() != null
                ? JSON.toJSONString(request.getParameters()) : "{}");
        strategyMapper.insert(strategy);

        // Run engine
        BacktestResult result = backtestEngine.run(request, klineData);

        // Set persistence fields
        result.setResultNo("BTR" + System.currentTimeMillis() + RandomUtil.randomNumbers(4));
        result.setUserId(request.getUserId());
        result.setStrategyId(strategy.getId());
        result.setStockCode(request.getStockCode());
        result.setStockName(request.getStockName());
        result.setStrategyType(request.getStrategyType());
        result.setParameterJson(strategy.getParameterJson());
        result.setStartDate(java.sql.Date.valueOf(start));
        result.setEndDate(java.sql.Date.valueOf(end));

        // Save and return
        resultMapper.insert(result);
        return result;
    }

    public BacktestResult getResult(Long id) {
        BacktestResult result = resultMapper.selectById(id);
        if (result == null) {
            throw new RuntimeException("Backtest result not found: id=" + id);
        }
        return result;
    }

    public List<BacktestResult> getHistory(Long userId) {
        QueryWrapper<BacktestResult> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).orderByDesc("create_time");
        return resultMapper.selectList(qw);
    }

    public List<StrategyTemplateVO> getStrategyTemplates() {
        List<StrategyTemplateVO> templates = new ArrayList<>();

        StrategyTemplateVO ma = new StrategyTemplateVO();
        ma.setType("MA_CROSSOVER");
        ma.setName("均线交叉");
        ma.setDescription("短期均线上穿长期均线时买入，下穿时卖出");
        ma.setParameters(buildParams(
                paramDef("fast", "快线周期", "number", 5),
                paramDef("slow", "慢线周期", "number", 20)));
        templates.add(ma);

        StrategyTemplateVO macd = new StrategyTemplateVO();
        macd.setType("MACD");
        macd.setName("MACD信号");
        macd.setDescription("MACD线上穿信号线时买入，下穿时卖出");
        macd.setParameters(buildParams(
                paramDef("fast", "快线周期", "number", 12),
                paramDef("slow", "慢线周期", "number", 26),
                paramDef("signal", "信号线周期", "number", 9)));
        templates.add(macd);

        StrategyTemplateVO mom = new StrategyTemplateVO();
        mom.setType("MOMENTUM");
        mom.setName("动量突破");
        mom.setDescription("价格突破N日最高价时买入，持有指定天数后卖出");
        mom.setParameters(buildParams(
                paramDef("lookback", "回看周期", "number", 20),
                paramDef("holdDays", "持仓天数", "number", 10)));
        templates.add(mom);

        StrategyTemplateVO bb = new StrategyTemplateVO();
        bb.setType("BOLLINGER");
        bb.setName("布林带");
        bb.setDescription("价格触及下轨时买入，触及上轨时卖出");
        bb.setParameters(buildParams(
                paramDef("period", "计算周期", "number", 20),
                paramDef("multiplier", "标准差倍数", "number", 2.0)));
        templates.add(bb);

        return templates;
    }

    private StrategyTemplateVO.ParamDef paramDef(String name, String label, String type, Object defaultValue) {
        StrategyTemplateVO.ParamDef pd = new StrategyTemplateVO.ParamDef();
        pd.setName(name);
        pd.setLabel(label);
        pd.setType(type);
        pd.setDefaultValue(defaultValue);
        return pd;
    }

    private List<StrategyTemplateVO.ParamDef> buildParams(StrategyTemplateVO.ParamDef... defs) {
        List<StrategyTemplateVO.ParamDef> list = new ArrayList<>();
        Collections.addAll(list, defs);
        return list;
    }

    private String getStrategyName(String type) {
        switch (type) {
            case "MA_CROSSOVER": return "均线交叉策略";
            case "MACD": return "MACD策略";
            case "MOMENTUM": return "动量突破策略";
            case "BOLLINGER": return "布林带策略";
            default: return "自定义策略";
        }
    }
}
