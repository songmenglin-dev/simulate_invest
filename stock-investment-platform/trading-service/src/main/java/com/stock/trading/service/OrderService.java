package com.stock.trading.service;

import cn.hutool.core.util.IdUtil;
import com.stock.common.constant.OrderDirection;
import com.stock.common.constant.OrderStatus;
import com.stock.common.entity.FundAccount;
import com.stock.common.entity.Order;
import com.stock.common.entity.Position;
import com.stock.common.exception.BusinessException;
import com.stock.trading.mapper.FundAccountMapper;
import com.stock.trading.mapper.OrderMapper;
import com.stock.trading.mapper.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private FundAccountMapper fundAccountMapper;

    @Transactional
    public Order placeOrder(OrderRequest request) {
        // 计算订单金额
        BigDecimal amount = request.getPrice().multiply(new BigDecimal(request.getQuantity()));

        // 查询资金账户
        FundAccount fundAccount = fundAccountMapper.selectById(request.getFundAccountId());
        if (fundAccount == null) {
            throw new BusinessException(400, "资金账户不存在");
        }
        if (fundAccount.getStatus() != 1) {
            throw new BusinessException(400, "资金账户已被禁用");
        }

        // 买入：检查余额
        if (request.getDirection() == OrderDirection.BUY) {
            BigDecimal available = fundAccount.getBalance().subtract(fundAccount.getFrozenBalance());
            if (available.compareTo(amount) < 0) {
                throw new BusinessException(400, "余额不足");
            }
            // 冻结资金
            fundAccount.setFrozenBalance(fundAccount.getFrozenBalance().add(amount));
            fundAccountMapper.updateById(fundAccount);
        }

        // 卖出：检查持仓
        if (request.getDirection() == OrderDirection.SELL) {
            Position position = findPosition(request.getUserId(), request.getStockCode());
            if (position == null || position.getAvailableQuantity() < request.getQuantity()) {
                throw new BusinessException(400, "持仓不足");
            }
            // 冻结持仓
            position.setFrozenQuantity(position.getFrozenQuantity() + request.getQuantity());
            position.setAvailableQuantity(position.getAvailableQuantity() - request.getQuantity());
            positionMapper.updateById(position);
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(request.getUserId());
        order.setFundAccountId(request.getFundAccountId());
        order.setStockCode(request.getStockCode());
        order.setStockName(request.getStockName());
        order.setDirection(request.getDirection());
        order.setPrice(request.getPrice());
        order.setQuantity(request.getQuantity());
        order.setAmount(amount);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderType(request.getOrderType());
        orderMapper.insert(order);

        // 模拟撮合：立即成交
        executeOrder(order);

        return order;
    }

    @Transactional
    public void executeOrder(Order order) {
        // 更新订单状态为已成交
        order.setStatus(OrderStatus.FILLED);
        orderMapper.updateById(order);

        // 更新资金账户
        FundAccount fundAccount = fundAccountMapper.selectById(order.getFundAccountId());
        BigDecimal amount = order.getAmount();

        if (order.getDirection() == OrderDirection.BUY) {
            // 买入：扣除冻结资金，增加持仓
            fundAccount.setFrozenBalance(fundAccount.getFrozenBalance().subtract(amount));
            fundAccount.setBalance(fundAccount.getBalance().subtract(amount));
            fundAccountMapper.updateById(fundAccount);

            // 更新持仓
            Position position = findPosition(order.getUserId(), order.getStockCode());
            if (position == null) {
                position = new Position();
                position.setUserId(order.getUserId());
                position.setFundAccountId(order.getFundAccountId());
                position.setStockCode(order.getStockCode());
                position.setStockName(order.getStockName());
                position.setTotalQuantity(order.getQuantity());
                position.setAvailableQuantity(order.getQuantity());
                position.setFrozenQuantity(0);
                // 计算平均成本
                position.setAvgCost(order.getPrice());
                positionMapper.insert(position);
            } else {
                // 更新持仓数量和成本
                int totalQty = position.getTotalQuantity() + order.getQuantity();
                BigDecimal totalCost = position.getAvgCost().multiply(new BigDecimal(position.getTotalQuantity()))
                        .add(order.getPrice().multiply(new BigDecimal(order.getQuantity())));
                position.setTotalQuantity(totalQty);
                position.setAvailableQuantity(position.getAvailableQuantity() + order.getQuantity());
                position.setAvgCost(totalCost.divide(new BigDecimal(totalQty), 3, BigDecimal.ROUND_HALF_UP));
                positionMapper.updateById(position);
            }
        } else {
            // 卖出：增加可用资金，更新持仓
            fundAccount.setBalance(fundAccount.getBalance().add(amount));
            fundAccountMapper.updateById(fundAccount);

            // 更新持仓
            Position position = findPosition(order.getUserId(), order.getStockCode());
            if (position != null) {
                position.setTotalQuantity(position.getTotalQuantity() - order.getQuantity());
                position.setFrozenQuantity(position.getFrozenQuantity() - order.getQuantity());
                if (position.getTotalQuantity() <= 0) {
                    positionMapper.deleteById(position);
                } else {
                    positionMapper.updateById(position);
                }
            }
        }
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权取消此订单");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException(400, "订单状态不允许取消");
        }

        // 更新订单状态
        order.setStatus(OrderStatus.CANCELLED);
        orderMapper.updateById(order);

        // 解冻资金或持仓
        FundAccount fundAccount = fundAccountMapper.selectById(order.getFundAccountId());
        if (order.getDirection() == OrderDirection.BUY) {
            // 解冻资金
            fundAccount.setFrozenBalance(fundAccount.getFrozenBalance().subtract(order.getAmount()));
            fundAccountMapper.updateById(fundAccount);
        } else {
            // 解冻持仓
            Position position = findPosition(order.getUserId(), order.getStockCode());
            if (position != null) {
                position.setFrozenQuantity(position.getFrozenQuantity() - order.getQuantity());
                position.setAvailableQuantity(position.getAvailableQuantity() + order.getQuantity());
                positionMapper.updateById(position);
            }
        }
    }

    public List<Order> getOrderHistory(Long userId) {
        return orderMapper.selectList(null).stream()
                .filter(o -> o.getUserId().equals(userId))
                .toList();
    }

    private Position findPosition(Long userId, String stockCode) {
        return positionMapper.selectList(null).stream()
                .filter(p -> p.getUserId().equals(userId) && p.getStockCode().equals(stockCode))
                .findFirst()
                .orElse(null);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + IdUtil.randomInt(1000, 9999);
    }
}