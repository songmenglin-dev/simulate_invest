package com.stock.trading.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.common.constant.ConditionType;
import com.stock.common.constant.ConditionalOrderStatus;
import com.stock.common.entity.ConditionalOrder;
import com.stock.common.entity.Order;
import com.stock.common.entity.StockQuoteEntity;
import com.stock.trading.dto.OrderRequest;
import com.stock.trading.mapper.ConditionalOrderMapper;
import com.stock.trading.mapper.FundAccountMapper;
import com.stock.trading.mapper.StockQuoteMapper;
import com.stock.trading.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class ConditionalOrderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ConditionalOrderScheduler.class);

    private static final BigDecimal PRICE_DEVIATION_THRESHOLD = new BigDecimal("0.03");

    @Autowired
    private ConditionalOrderMapper conditionalOrderMapper;

    @Autowired
    private StockQuoteMapper stockQuoteMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private FundAccountMapper fundAccountMapper;

    @Scheduled(fixedRate = 10000)
    public void checkConditionalOrders() {
        log.debug("Starting conditional order check...");

        // Query all ACTIVE conditional orders
        QueryWrapper<ConditionalOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("status", ConditionalOrderStatus.ACTIVE);
        List<ConditionalOrder> activeOrders = conditionalOrderMapper.selectList(wrapper);

        if (activeOrders.isEmpty()) {
            log.debug("No active conditional orders found");
            return;
        }

        log.info("Found {} active conditional orders to check", activeOrders.size());

        for (ConditionalOrder co : activeOrders) {
            try {
                processConditionalOrder(co);
            } catch (Exception e) {
                log.error("Failed to process conditional order: id={}, orderNo={}", co.getId(), co.getOrderNo(), e);
                // Mark as expired on unexpected error
                co.setStatus(ConditionalOrderStatus.EXPIRED);
                co.setFailReason("系统错误: " + truncate(e.getMessage(), 200));
                conditionalOrderMapper.updateById(co);
            }
        }
    }

    private void processConditionalOrder(ConditionalOrder co) {
        // Get current stock price
        BigDecimal currentPrice = getCurrentPrice(co.getStockCode());
        if (currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Could not get current price for stock: {}, skipping conditional order: {}",
                    co.getStockCode(), co.getOrderNo());
            return;
        }

        // Check if condition is met based on condition type
        boolean conditionMet = false;
        if (ConditionType.TAKE_PROFIT.equals(co.getConditionType())) {
            conditionMet = currentPrice.compareTo(co.getTriggerPrice()) >= 0;
        } else if (ConditionType.STOP_LOSS.equals(co.getConditionType())) {
            conditionMet = currentPrice.compareTo(co.getTriggerPrice()) <= 0;
        }

        if (!conditionMet) {
            log.debug("Condition not yet met for order: {}, currentPrice={}, triggerPrice={}",
                    co.getOrderNo(), currentPrice, co.getTriggerPrice());
            return;
        }

        // Check price deviation: |currentPrice - triggerPrice| / triggerPrice > 3%
        BigDecimal deviation = currentPrice.subtract(co.getTriggerPrice()).abs()
                .divide(co.getTriggerPrice(), 4, RoundingMode.HALF_UP);
        if (deviation.compareTo(PRICE_DEVIATION_THRESHOLD) > 0) {
            log.info("Price deviation too large for order: {}, deviation={}, currentPrice={}, triggerPrice={}",
                    co.getOrderNo(), deviation, currentPrice, co.getTriggerPrice());
            co.setStatus(ConditionalOrderStatus.EXPIRED);
            co.setFailReason("价格偏差过大");
            conditionalOrderMapper.updateById(co);
            return;
        }

        // Execute the conditional order via OrderService
        try {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setUserId(co.getUserId());
            orderRequest.setFundAccountId(co.getFundAccountId());
            orderRequest.setStockCode(co.getStockCode());
            orderRequest.setStockName(co.getStockName());
            orderRequest.setDirection(co.getDirection());
            orderRequest.setPrice(co.getOrderPrice());
            orderRequest.setQuantity(co.getQuantity());
            orderRequest.setOrderType(2); // 限价单
            orderRequest.setConditionalOrderId(co.getId());

            Order placedOrder = orderService.placeOrder(orderRequest);
            Order confirmedOrder = orderService.confirmOrder(placedOrder.getId(), co.getUserId());

            // Update conditional order status
            co.setStatus(ConditionalOrderStatus.TRIGGERED);
            co.setTriggeredOrderId(confirmedOrder.getId());
            conditionalOrderMapper.updateById(co);

            log.info("Conditional order triggered: id={}, orderNo={}, triggeredOrderId={}",
                    co.getId(), co.getOrderNo(), confirmedOrder.getId());

        } catch (Exception e) {
            log.error("Failed to execute conditional order: id={}, orderNo={}", co.getId(), co.getOrderNo(), e);
            co.setStatus(ConditionalOrderStatus.EXPIRED);
            co.setFailReason("执行失败: " + truncate(e.getMessage(), 200));
            conditionalOrderMapper.updateById(co);
        }
    }

    private BigDecimal getCurrentPrice(String stockCode) {
        QueryWrapper<StockQuoteEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("stock_code", stockCode).orderByDesc("update_time").last("LIMIT 1");
        StockQuoteEntity quote = stockQuoteMapper.selectList(wrapper).stream().findFirst().orElse(null);
        if (quote != null && quote.getCurrentPrice() != null) {
            return quote.getCurrentPrice();
        }
        return BigDecimal.ZERO;
    }

    private String truncate(String msg, int maxLength) {
        if (msg == null) {
            return "未知错误";
        }
        return msg.length() > maxLength ? msg.substring(0, maxLength) + "..." : msg;
    }
}
