package com.stock.analysis.engine;

import com.alibaba.fastjson2.JSON;
import com.stock.analysis.dto.BacktestRequest;
import com.stock.common.entity.BacktestResult;
import com.stock.common.entity.StockKLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class BacktestEngine {

    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.001");
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int SCALE = 8;

    public BacktestResult run(BacktestRequest request, List<StockKLine> klineData) {
        klineData.sort(Comparator.comparing(StockKLine::getTradeDate));
        int n = klineData.size();

        Map<String, Object> params = request.getParameters();
        if (params == null) params = new HashMap<>();
        String strategyType = request.getStrategyType();

        // Extract price arrays
        BigDecimal[] closes = new BigDecimal[n];
        BigDecimal[] highs = new BigDecimal[n];
        String[] dateStrs = new String[n];

        for (int i = 0; i < n; i++) {
            StockKLine bar = klineData.get(i);
            closes[i] = bar.getClosePrice();
            highs[i] = bar.getHighPrice();
            dateStrs[i] = bar.getTradeDate().toString();
        }

        // Determine start index
        int startIdx = getMinLookback(strategyType, params);
        if (n <= startIdx) {
            throw new RuntimeException("Not enough data, need at least " + (startIdx + 1) + " bars");
        }

        // Pre-compute indicators
        IndicatorData ind = precomputeIndicators(strategyType, closes, highs, params, n);

        // Backtest state
        BigDecimal cash = request.getInitialCapital();
        BigDecimal initialCapital = cash;
        int shares = 0;
        boolean inPosition = false;
        BigDecimal entryPrice = BigDecimal.ZERO;
        String entryDate = "";
        int entryIdx = 0;
        BigDecimal entryCommission = BigDecimal.ZERO;

        List<Map<String, Object>> trades = new ArrayList<>();
        List<Map<String, Object>> equityPoints = new ArrayList<>();

        // Main bar-by-bar loop
        for (int i = startIdx; i < n; i++) {
            BigDecimal close = closes[i];

            if (!inPosition) {
                boolean buy = checkBuy(strategyType, closes, highs, ind, i, params);
                if (buy) {
                    BigDecimal priceWithComm = close.multiply(BigDecimal.ONE.add(COMMISSION_RATE));
                    int buyQty = cash.divide(priceWithComm, 0, RoundingMode.DOWN).intValue();
                    if (buyQty >= 100) {
                        BigDecimal cost = close.multiply(BigDecimal.valueOf(buyQty));
                        entryCommission = cost.multiply(COMMISSION_RATE);
                        cash = cash.subtract(cost).subtract(entryCommission);
                        shares = buyQty;
                        inPosition = true;
                        entryPrice = close;
                        entryDate = dateStrs[i];
                        entryIdx = i;
                    }
                }
            } else {
                boolean sell = checkSell(strategyType, closes, highs, ind, i,
                        dateStrs[i], dateStrs[entryIdx], params);
                if (sell) {
                    BigDecimal proceeds = close.multiply(BigDecimal.valueOf(shares));
                    BigDecimal exitCommission = proceeds.multiply(COMMISSION_RATE);
                    cash = cash.add(proceeds).subtract(exitCommission);

                    Map<String, Object> trade = buildTrade(entryDate, dateStrs[i],
                            entryPrice, close, shares, entryCommission, exitCommission);
                    trades.add(trade);
                    shares = 0;
                    inPosition = false;
                }
            }

            // Equity point
            BigDecimal equity = cash;
            if (inPosition) {
                equity = equity.add(close.multiply(BigDecimal.valueOf(shares)));
            }
            Map<String, Object> pt = new HashMap<>();
            pt.put("date", dateStrs[i]);
            pt.put("value", equity);
            equityPoints.add(pt);
        }

        // Force liquidate
        if (inPosition) {
            BigDecimal lastClose = closes[n - 1];
            BigDecimal proceeds = lastClose.multiply(BigDecimal.valueOf(shares));
            BigDecimal exitCommission = proceeds.multiply(COMMISSION_RATE);
            cash = cash.add(proceeds).subtract(exitCommission);

            Map<String, Object> trade = buildTrade(entryDate, dateStrs[n - 1],
                    entryPrice, lastClose, shares, entryCommission, exitCommission);
            trades.add(trade);
        }

        BigDecimal finalCapital = cash;

        // Metrics
        BigDecimal totalReturn = finalCapital.subtract(initialCapital)
                .divide(initialCapital, SCALE, RoundingMode.HALF_UP);
        BigDecimal annualReturn = calcAnnualReturn(totalReturn, dateStrs[0], dateStrs[dateStrs.length - 1]);
        BigDecimal maxDrawdown = calcMaxDrawdown(equityPoints);

        int totalTrades = trades.size();
        int winningTrades = 0;
        for (Map<String, Object> t : trades) {
            BigDecimal pnl = (BigDecimal) t.get("pnl");
            if (pnl != null && pnl.compareTo(BigDecimal.ZERO) > 0) winningTrades++;
        }
        BigDecimal winRate = totalTrades > 0
                ? BigDecimal.valueOf(winningTrades).divide(BigDecimal.valueOf(totalTrades), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal sharpe = calcSharpe(equityPoints);

        // Build result entity
        BacktestResult result = new BacktestResult();
        result.setInitialCapital(initialCapital);
        result.setFinalCapital(finalCapital.setScale(2, RoundingMode.HALF_UP));
        result.setTotalReturn(totalReturn.multiply(HUNDRED).setScale(2, RoundingMode.HALF_UP));
        result.setAnnualReturn(annualReturn.setScale(2, RoundingMode.HALF_UP));
        result.setMaxDrawdown(maxDrawdown.multiply(HUNDRED).setScale(2, RoundingMode.HALF_UP));
        result.setTotalTrades(totalTrades);
        result.setWinningTrades(winningTrades);
        result.setWinRate(winRate.multiply(HUNDRED).setScale(2, RoundingMode.HALF_UP));
        result.setSharpeRatio(sharpe.setScale(4, RoundingMode.HALF_UP));
        result.setEquityCurveJson(JSON.toJSONString(equityPoints));
        result.setTradesJson(JSON.toJSONString(trades));

        return result;
    }

    // ==================== Trade Builder ====================

    private Map<String, Object> buildTrade(String entryDate, String exitDate,
                                            BigDecimal entryPrice, BigDecimal exitPrice,
                                            int shares, BigDecimal entryComm, BigDecimal exitComm) {
        BigDecimal entryCost = entryPrice.multiply(BigDecimal.valueOf(shares));
        BigDecimal proceeds = exitPrice.multiply(BigDecimal.valueOf(shares));
        BigDecimal grossPnl = proceeds.subtract(entryCost);
        BigDecimal netPnl = grossPnl.subtract(entryComm).subtract(exitComm);
        BigDecimal returnPct = entryCost.compareTo(BigDecimal.ZERO) > 0
                ? netPnl.divide(entryCost, 6, RoundingMode.HALF_UP).multiply(HUNDRED)
                : BigDecimal.ZERO;

        Map<String, Object> trade = new HashMap<>();
        trade.put("entryDate", entryDate);
        trade.put("exitDate", exitDate);
        trade.put("entryPrice", entryPrice);
        trade.put("exitPrice", exitPrice);
        trade.put("quantity", shares);
        trade.put("return", returnPct);
        trade.put("pnl", netPnl);
        return trade;
    }

    // ==================== Indicator Pre-computation ====================

    private static class IndicatorData {
        BigDecimal[] maFast, maSlow;
        BigDecimal[] macdLine, macdSignal;
        BigDecimal[] bollMa, bollUpper, bollLower;
    }

    private IndicatorData precomputeIndicators(String type, BigDecimal[] close, BigDecimal[] high,
                                                Map<String, Object> params, int n) {
        IndicatorData ind = new IndicatorData();
        switch (type) {
            case "MA_CROSSOVER": {
                int fast = getIntParam(params, "fast", 5);
                int slow = getIntParam(params, "slow", 20);
                ind.maFast = computeSMA(close, n, fast);
                ind.maSlow = computeSMA(close, n, slow);
                break;
            }
            case "MACD": {
                int fast = getIntParam(params, "fast", 12);
                int slow = getIntParam(params, "slow", 26);
                int sig = getIntParam(params, "signal", 9);
                BigDecimal[] emaFast = computeEMA(close, n, fast);
                BigDecimal[] emaSlow = computeEMA(close, n, slow);
                ind.macdLine = new BigDecimal[n];
                for (int i = 0; i < n; i++) {
                    if (emaFast[i] != null && emaSlow[i] != null) {
                        ind.macdLine[i] = emaFast[i].subtract(emaSlow[i]).setScale(SCALE, RoundingMode.HALF_UP);
                    }
                }
                ind.macdSignal = computeEMA(ind.macdLine, n, sig);
                break;
            }
            case "BOLLINGER": {
                int period = getIntParam(params, "period", 20);
                double mult = getDoubleParam(params, "multiplier", 2.0);
                ind.bollMa = computeSMA(close, n, period);
                ind.bollUpper = new BigDecimal[n];
                ind.bollLower = new BigDecimal[n];
                for (int i = 0; i < n; i++) {
                    if (ind.bollMa[i] != null) {
                        BigDecimal std = calcStdDev(close, i, period, ind.bollMa[i]);
                        BigDecimal band = std.multiply(BigDecimal.valueOf(mult));
                        ind.bollUpper[i] = ind.bollMa[i].add(band).setScale(SCALE, RoundingMode.HALF_UP);
                        ind.bollLower[i] = ind.bollMa[i].subtract(band).setScale(SCALE, RoundingMode.HALF_UP);
                    }
                }
                break;
            }
            default: break;
        }
        return ind;
    }

    private BigDecimal[] computeSMA(BigDecimal[] data, int n, int period) {
        BigDecimal[] result = new BigDecimal[n];
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < n; i++) {
            sum = sum.add(data[i]);
            if (i >= period) {
                sum = sum.subtract(data[i - period]);
                result[i] = sum.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
            } else if (i == period - 1) {
                result[i] = sum.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
            }
        }
        return result;
    }

    private BigDecimal[] computeEMA(BigDecimal[] data, int n, int period) {
        BigDecimal[] result = new BigDecimal[n];
        int firstValid = -1;
        for (int i = 0; i < n; i++) {
            if (data[i] != null) { firstValid = i; break; }
        }
        if (firstValid < 0 || firstValid + period > n) return result;

        int seedEnd = firstValid + period - 1;
        BigDecimal sum = BigDecimal.ZERO;
        int cnt = 0;
        for (int i = firstValid; i <= seedEnd && i < n; i++) {
            if (data[i] != null) { sum = sum.add(data[i]); cnt++; }
        }
        if (cnt == 0) return result;
        result[seedEnd] = sum.divide(BigDecimal.valueOf(cnt), SCALE, RoundingMode.HALF_UP);

        BigDecimal mult = BigDecimal.valueOf(2.0 / (period + 1));
        for (int i = seedEnd + 1; i < n; i++) {
            if (data[i] != null && result[i - 1] != null) {
                BigDecimal diff = data[i].subtract(result[i - 1]);
                result[i] = diff.multiply(mult).add(result[i - 1]).setScale(SCALE, RoundingMode.HALF_UP);
            }
        }
        return result;
    }

    private BigDecimal calcStdDev(BigDecimal[] data, int index, int period, BigDecimal mean) {
        BigDecimal sumSq = BigDecimal.ZERO;
        for (int i = index - period + 1; i <= index; i++) {
            BigDecimal diff = data[i].subtract(mean);
            sumSq = sumSq.add(diff.multiply(diff));
        }
        BigDecimal var = sumSq.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(Math.sqrt(Math.max(var.doubleValue(), 0.0)));
    }

    // ==================== Signal Detection ====================

    private boolean checkBuy(String type, BigDecimal[] close, BigDecimal[] high,
                             IndicatorData ind, int idx, Map<String, Object> params) {
        switch (type) {
            case "MA_CROSSOVER":
                return ind.maFast[idx] != null && ind.maSlow[idx] != null
                        && ind.maFast[idx - 1] != null && ind.maSlow[idx - 1] != null
                        && ind.maFast[idx - 1].compareTo(ind.maSlow[idx - 1]) <= 0
                        && ind.maFast[idx].compareTo(ind.maSlow[idx]) > 0;
            case "MACD":
                return ind.macdLine[idx] != null && ind.macdSignal[idx] != null
                        && ind.macdLine[idx - 1] != null && ind.macdSignal[idx - 1] != null
                        && ind.macdLine[idx - 1].compareTo(ind.macdSignal[idx - 1]) <= 0
                        && ind.macdLine[idx].compareTo(ind.macdSignal[idx]) > 0;
            case "MOMENTUM": {
                int lb = getIntParam(params, "lookback", 20);
                if (idx < lb) return false;
                BigDecimal maxH = BigDecimal.ZERO;
                for (int j = idx - lb; j < idx; j++) {
                    if (high[j].compareTo(maxH) > 0) maxH = high[j];
                }
                return close[idx].compareTo(maxH) > 0;
            }
            case "BOLLINGER":
                return ind.bollLower[idx] != null && close[idx].compareTo(ind.bollLower[idx]) <= 0;
            default: return false;
        }
    }

    private boolean checkSell(String type, BigDecimal[] close, BigDecimal[] high,
                               IndicatorData ind, int idx, String curDate, String entryDate,
                               Map<String, Object> params) {
        switch (type) {
            case "MA_CROSSOVER":
                return ind.maFast[idx] != null && ind.maSlow[idx] != null
                        && ind.maFast[idx - 1] != null && ind.maSlow[idx - 1] != null
                        && ind.maFast[idx - 1].compareTo(ind.maSlow[idx - 1]) >= 0
                        && ind.maFast[idx].compareTo(ind.maSlow[idx]) < 0;
            case "MACD":
                return ind.macdLine[idx] != null && ind.macdSignal[idx] != null
                        && ind.macdLine[idx - 1] != null && ind.macdSignal[idx - 1] != null
                        && ind.macdLine[idx - 1].compareTo(ind.macdSignal[idx - 1]) >= 0
                        && ind.macdLine[idx].compareTo(ind.macdSignal[idx]) < 0;
            case "MOMENTUM": {
                int hd = getIntParam(params, "holdDays", 10);
                try {
                    LocalDate en = LocalDate.parse(entryDate);
                    LocalDate cu = LocalDate.parse(curDate);
                    return ChronoUnit.DAYS.between(en, cu) >= hd;
                } catch (Exception e) {
                    return false;
                }
            }
            case "BOLLINGER":
                return ind.bollUpper[idx] != null && close[idx].compareTo(ind.bollUpper[idx]) >= 0;
            default: return false;
        }
    }

    // ==================== Metrics ====================

    private BigDecimal calcAnnualReturn(BigDecimal totalReturn, String startStr, String endStr) {
        try {
            LocalDate s = LocalDate.parse(startStr);
            LocalDate e = LocalDate.parse(endStr);
            long days = ChronoUnit.DAYS.between(s, e) + 1;
            double years = Math.max(days / 365.0, 0.01);
            double tr = 1.0 + totalReturn.doubleValue();
            if (tr <= 0.0) return new BigDecimal("-100.00");
            return BigDecimal.valueOf((Math.pow(tr, 1.0 / years) - 1.0) * 100.0);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calcMaxDrawdown(List<Map<String, Object>> equityPoints) {
        if (equityPoints.isEmpty()) return BigDecimal.ZERO;
        BigDecimal peak = BigDecimal.ZERO;
        BigDecimal maxDD = BigDecimal.ZERO;
        for (Map<String, Object> pt : equityPoints) {
            BigDecimal val = (BigDecimal) pt.get("value");
            if (val == null) continue;
            if (val.compareTo(peak) > 0) peak = val;
            if (peak.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal dd = BigDecimal.ONE.subtract(val.divide(peak, SCALE, RoundingMode.HALF_UP));
                if (dd.compareTo(maxDD) > 0) maxDD = dd;
            }
        }
        return maxDD;
    }

    private BigDecimal calcSharpe(List<Map<String, Object>> equityPoints) {
        if (equityPoints.size() < 2) return BigDecimal.ZERO;
        int count = equityPoints.size() - 1;
        double[] dr = new double[count];
        for (int i = 1; i < equityPoints.size(); i++) {
            BigDecimal prev = (BigDecimal) equityPoints.get(i - 1).get("value");
            BigDecimal curr = (BigDecimal) equityPoints.get(i).get("value");
            if (prev == null || curr == null || prev.compareTo(BigDecimal.ZERO) == 0) {
                dr[i - 1] = 0.0;
            } else {
                dr[i - 1] = curr.subtract(prev).divide(prev, SCALE, RoundingMode.HALF_UP).doubleValue();
            }
        }
        double sum = 0.0;
        for (double r : dr) sum += r;
        double mean = sum / count;
        double sumSq = 0.0;
        for (double r : dr) { double d = r - mean; sumSq += d * d; }
        double std = Math.sqrt(sumSq / count);
        if (std < 1e-15) return BigDecimal.ZERO;
        return BigDecimal.valueOf((mean / std) * Math.sqrt(252));
    }

    // ==================== Parameter Helpers ====================

    private int getMinLookback(String type, Map<String, Object> params) {
        switch (type) {
            case "MA_CROSSOVER": return Math.max(getIntParam(params, "fast", 5), getIntParam(params, "slow", 20)) + 1;
            case "MACD": return getIntParam(params, "slow", 26) + getIntParam(params, "signal", 9) + 1;
            case "MOMENTUM": return getIntParam(params, "lookback", 20) + 1;
            case "BOLLINGER": return getIntParam(params, "period", 20) + 1;
            default: return 20;
        }
    }

    private int getIntParam(Map<String, Object> params, String key, int def) {
        if (params == null || !params.containsKey(key)) return def;
        Object v = params.get(key);
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Long) return ((Long) v).intValue();
        if (v instanceof Double) return ((Double) v).intValue();
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return def; }
    }

    private double getDoubleParam(Map<String, Object> params, String key, double def) {
        if (params == null || !params.containsKey(key)) return def;
        Object v = params.get(key);
        if (v instanceof Double) return (Double) v;
        if (v instanceof Float) return ((Float) v).doubleValue();
        if (v instanceof BigDecimal) return ((BigDecimal) v).doubleValue();
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return def; }
    }
}
