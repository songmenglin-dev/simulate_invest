package com.stock.portfolio.service;

import com.stock.common.entity.FundAccount;
import com.stock.common.entity.Position;
import com.stock.portfolio.dto.PositionDetail;
import com.stock.portfolio.dto.PortfolioOverview;
import com.stock.portfolio.mapper.FundAccountMapper;
import com.stock.portfolio.mapper.PositionMapper;
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

    // 模拟股价（实际应从market-service获取）
    private static final Map<String, BigDecimal> MOCK_PRICES = new HashMap<>();

    static {
        MOCK_PRICES.put("600036", new BigDecimal("35.50"));
        MOCK_PRICES.put("600519", new BigDecimal("1680.00"));
        MOCK_PRICES.put("000858", new BigDecimal("145.30"));
        MOCK_PRICES.put("601318", new BigDecimal("48.50"));
        MOCK_PRICES.put("000001", new BigDecimal("12.30"));
        MOCK_PRICES.put("600887", new BigDecimal("28.90"));
        MOCK_PRICES.put("000333", new BigDecimal("58.20"));
        MOCK_PRICES.put("002594", new BigDecimal("268.00"));
    }

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
            BigDecimal currentPrice = MOCK_PRICES.getOrDefault(position.getStockCode(), position.getAvgCost());
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

    private PositionDetail convertToDetail(Position position) {
        PositionDetail detail = new PositionDetail();
        detail.setPositionId(position.getId());
        detail.setStockCode(position.getStockCode());
        detail.setStockName(position.getStockName());
        detail.setTotalQuantity(position.getTotalQuantity());
        detail.setAvailableQuantity(position.getAvailableQuantity());
        detail.setFrozenQuantity(position.getFrozenQuantity());
        detail.setAvgCost(position.getAvgCost());

        BigDecimal currentPrice = MOCK_PRICES.getOrDefault(position.getStockCode(), position.getAvgCost());
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