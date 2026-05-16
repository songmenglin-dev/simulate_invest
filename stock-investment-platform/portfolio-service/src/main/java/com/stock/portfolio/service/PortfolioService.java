package com.stock.portfolio.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.common.entity.FundAccount;
import com.stock.common.entity.Position;
import com.stock.common.entity.StockQuoteEntity;
import com.stock.common.exception.BusinessException;
import com.stock.portfolio.dto.PositionDetail;
import com.stock.portfolio.dto.PortfolioOverview;
import com.stock.portfolio.mapper.FundAccountMapper;
import com.stock.portfolio.mapper.PositionMapper;
import com.stock.portfolio.mapper.StockQuoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private FundAccountMapper fundAccountMapper;

    @Autowired
    private StockQuoteMapper stockQuoteMapper;

    public PortfolioOverview getOverview(Long userId) {
        PortfolioOverview overview = new PortfolioOverview();

        // 查询资金账户
        List<FundAccount> accounts = fundAccountMapper.selectList(null).stream()
                .filter(a -> a.getUserId().equals(userId))
                .collect(Collectors.toList());

        FundAccount account = accounts.isEmpty() ? null : accounts.get(0);

        // 初始化默认值
        overview.setAvailableCash(account != null ? account.getBalance() : BigDecimal.ZERO);
        overview.setFrozenCash(account != null ? account.getFrozenBalance() : BigDecimal.ZERO);

        // 查询持仓
        List<Position> positions = positionMapper.selectList(null).stream()
                .filter(p -> p.getUserId().equals(userId))
                .collect(Collectors.toList());

        // 计算总市值和盈亏
        BigDecimal totalMarketValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Position position : positions) {
            BigDecimal currentPrice = getCurrentPrice(position.getStockCode(), position.getAvgCost());
            BigDecimal marketValue = currentPrice.multiply(new BigDecimal(position.getTotalQuantity()));
            BigDecimal cost = position.getAvgCost().multiply(new BigDecimal(position.getTotalQuantity()));

            totalMarketValue = totalMarketValue.add(marketValue);
            totalCost = totalCost.add(cost);
        }

        overview.setTotalMarketValue(totalMarketValue);

        BigDecimal totalProfitLoss = totalMarketValue.subtract(totalCost);
        overview.setTotalProfitLoss(totalProfitLoss);

        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profitLossPercent = totalProfitLoss.divide(totalCost, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            overview.setProfitLossPercent(profitLossPercent);
        } else {
            overview.setProfitLossPercent(BigDecimal.ZERO);
        }

        overview.setTotalAssets(overview.getAvailableCash()
                .add(overview.getFrozenCash())
                .add(totalMarketValue));

        return overview;
    }

    public List<PositionDetail> getPositions(Long userId) {
        List<Position> positions = positionMapper.selectList(null).stream()
                .filter(p -> p.getUserId().equals(userId))
                .collect(Collectors.toList());

        return positions.stream().map(this::convertToDetail).collect(Collectors.toList());
    }

    public PositionDetail getPositionDetail(Long userId, String stockCode) {
        Position position = positionMapper.selectList(null).stream()
                .filter(p -> p.getUserId().equals(userId) && p.getStockCode().equals(stockCode))
                .findFirst()
                .orElse(null);

        return position != null ? convertToDetail(position) : null;
    }

    public Map<String, Object> getCashBalance(Long userId) {
        List<FundAccount> accounts = fundAccountMapper.selectList(null).stream()
                .filter(a -> a.getUserId().equals(userId))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        if (accounts.isEmpty()) {
            result.put("availableCash", BigDecimal.ZERO);
            result.put("frozenCash", BigDecimal.ZERO);
            result.put("totalCash", BigDecimal.ZERO);
        } else {
            FundAccount account = accounts.get(0);
            result.put("availableCash", account.getBalance());
            result.put("frozenCash", account.getFrozenBalance());
            result.put("totalCash", account.getBalance().add(account.getFrozenBalance()));
            result.put("accountNo", account.getAccountNo());
            result.put("status", account.getStatus());
        }
        return result;
    }

    public Map<String, Object> deposit(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "充值金额必须大于0");
        }
        FundAccount account = fundAccountMapper.selectList(null).stream()
                .filter(a -> a.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
        if (account == null) {
            throw new BusinessException(400, "资金账户不存在");
        }
        account.setBalance(account.getBalance().add(amount));
        fundAccountMapper.updateById(account);
        Map<String, Object> result = new HashMap<>();
        result.put("balance", account.getBalance());
        result.put("amount", amount);
        return result;
    }

    public Map<String, Object> withdraw(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "提现金额必须大于0");
        }
        FundAccount account = fundAccountMapper.selectList(null).stream()
                .filter(a -> a.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
        if (account == null) {
            throw new BusinessException(400, "资金账户不存在");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException(400, "余额不足");
        }
        account.setBalance(account.getBalance().subtract(amount));
        fundAccountMapper.updateById(account);
        Map<String, Object> result = new HashMap<>();
        result.put("balance", account.getBalance());
        result.put("amount", amount);
        return result;
    }

    private BigDecimal getCurrentPrice(String stockCode, BigDecimal fallback) {
        QueryWrapper<StockQuoteEntity> qw = new QueryWrapper<>();
        qw.eq("stock_code", stockCode);
        StockQuoteEntity quote = stockQuoteMapper.selectOne(qw);
        if (quote != null && quote.getCurrentPrice() != null) {
            return quote.getCurrentPrice();
        }
        BigDecimal fluctuation = BigDecimal.valueOf(0.95 + Math.random() * 0.10);
        return fallback.multiply(fluctuation).setScale(2, RoundingMode.HALF_UP);
    }

    private PositionDetail convertToDetail(Position position) {
        PositionDetail detail = new PositionDetail();
        detail.setPositionId(position.getId());
        detail.setStockCode(position.getStockCode());
        detail.setStockName(position.getStockName());
        detail.setTotalQuantity(position.getTotalQuantity());
        detail.setAvailableQuantity(position.getAvailableQuantity());
        detail.setFrozenQuantity(position.getFrozenQuantity());
        detail.setAvgCost(position.getAvgCost());

        BigDecimal currentPrice = getCurrentPrice(position.getStockCode(), position.getAvgCost());
        detail.setCurrentPrice(currentPrice);

        BigDecimal marketValue = currentPrice.multiply(new BigDecimal(position.getTotalQuantity()));
        detail.setMarketValue(marketValue);

        BigDecimal cost = position.getAvgCost().multiply(new BigDecimal(position.getTotalQuantity()));
        BigDecimal profitLoss = marketValue.subtract(cost);
        detail.setProfitLoss(profitLoss);

        if (cost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profitLossPercent = profitLoss.divide(cost, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            detail.setProfitLossPercent(profitLossPercent);
        } else {
            detail.setProfitLossPercent(BigDecimal.ZERO);
        }

        return detail;
    }
}