package com.stock.market.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.stock.common.constant.AlertType;
import com.stock.common.entity.PriceAlert;
import com.stock.common.entity.PriceAlertNotification;
import com.stock.common.entity.StockQuoteEntity;
import com.stock.market.dto.NotificationVO;
import com.stock.market.dto.PriceAlertRequest;
import com.stock.market.dto.PriceAlertVO;
import com.stock.market.mapper.PriceAlertMapper;
import com.stock.market.mapper.PriceAlertNotificationMapper;
import com.stock.market.mapper.StockQuoteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceAlertService {

    private static final Logger log = LoggerFactory.getLogger(PriceAlertService.class);

    @Autowired
    private PriceAlertMapper priceAlertMapper;

    @Autowired
    private PriceAlertNotificationMapper notificationMapper;

    @Autowired
    private StockQuoteMapper stockQuoteMapper;

    @Autowired
    private EmailService emailService;

    public PriceAlert createAlert(PriceAlertRequest req) {
        String alertNo = "ALT" + System.currentTimeMillis()
                + String.format("%04d", (int) (Math.random() * 10000));
        PriceAlert alert = new PriceAlert();
        alert.setAlertNo(alertNo);
        alert.setUserId(req.getUserId());
        alert.setStockCode(req.getStockCode());
        alert.setStockName(req.getStockName());
        alert.setAlertType(req.getAlertType());
        alert.setTargetPrice(req.getTargetPrice());
        alert.setStatus("ACTIVE");
        priceAlertMapper.insert(alert);
        log.info("Price alert created: {} for stock {}", alertNo, req.getStockCode());
        return alert;
    }

    public void cancelAlert(Long id, Long userId) {
        PriceAlert alert = priceAlertMapper.selectById(id);
        if (alert == null || !alert.getUserId().equals(userId)) {
            throw new RuntimeException("Alert not found or not owned by user");
        }
        UpdateWrapper<PriceAlert> uw = new UpdateWrapper<>();
        uw.eq("id", id);
        uw.set("status", "CANCELLED");
        priceAlertMapper.update(null, uw);
        log.info("Price alert cancelled: id={}", id);
    }

    public List<PriceAlertVO> getAlerts(Long userId, String statusFilter) {
        QueryWrapper<PriceAlert> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        if (statusFilter != null && !statusFilter.isEmpty()) {
            qw.eq("status", statusFilter);
        }
        qw.orderByDesc("create_time");
        List<PriceAlert> list = priceAlertMapper.selectList(qw);
        return list.stream().map(this::toAlertVO).collect(Collectors.toList());
    }

    public List<NotificationVO> getNotifications(Long userId) {
        QueryWrapper<PriceAlertNotification> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.orderByDesc("create_time");
        List<PriceAlertNotification> list = notificationMapper.selectList(qw);
        return list.stream().map(this::toNotificationVO).collect(Collectors.toList());
    }

    public void markNotificationRead(Long id) {
        UpdateWrapper<PriceAlertNotification> uw = new UpdateWrapper<>();
        uw.eq("id", id);
        uw.set("is_read", 1);
        notificationMapper.update(null, uw);
    }

    public void markAllNotificationsRead(Long userId) {
        UpdateWrapper<PriceAlertNotification> uw = new UpdateWrapper<>();
        uw.eq("user_id", userId);
        uw.eq("is_read", 0);
        uw.set("is_read", 1);
        notificationMapper.update(null, uw);
    }

    public int getUnreadCount(Long userId) {
        QueryWrapper<PriceAlertNotification> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.eq("is_read", 0);
        return Math.toIntExact(notificationMapper.selectCount(qw));
    }

    /**
     * Check all active alerts and trigger notifications if conditions are met.
     * Called by PriceAlertScheduler.
     */
    public void checkAndTriggerAlerts() {
        QueryWrapper<PriceAlert> qw = new QueryWrapper<>();
        qw.eq("status", "ACTIVE");
        List<PriceAlert> activeAlerts = priceAlertMapper.selectList(qw);

        for (PriceAlert alert : activeAlerts) {
            try {
                QueryWrapper<StockQuoteEntity> sqw = new QueryWrapper<>();
                sqw.eq("stock_code", alert.getStockCode());
                StockQuoteEntity quote = stockQuoteMapper.selectOne(sqw);
                if (quote == null || quote.getCurrentPrice() == null) {
                    continue;
                }

                BigDecimal currentPrice = quote.getCurrentPrice();
                BigDecimal targetPrice = alert.getTargetPrice();
                boolean triggered = false;

                if (AlertType.PRICE_ABOVE.equals(alert.getAlertType())
                        && currentPrice.compareTo(targetPrice) >= 0) {
                    triggered = true;
                } else if (AlertType.PRICE_BELOW.equals(alert.getAlertType())
                        && currentPrice.compareTo(targetPrice) <= 0) {
                    triggered = true;
                }

                if (triggered) {
                    // Update alert status
                    UpdateWrapper<PriceAlert> uw = new UpdateWrapper<>();
                    uw.eq("id", alert.getId());
                    uw.set("status", "TRIGGERED");
                    priceAlertMapper.update(null, uw);

                    // Create notification
                    PriceAlertNotification notification = new PriceAlertNotification();
                    notification.setUserId(alert.getUserId());
                    notification.setAlertId(alert.getId());
                    notification.setStockCode(alert.getStockCode());
                    notification.setStockName(alert.getStockName());
                    notification.setAlertType(alert.getAlertType());
                    notification.setTargetPrice(targetPrice);
                    notification.setTriggeredPrice(currentPrice);
                    notification.setIsRead(0);
                    notificationMapper.insert(notification);

                    log.info("Price alert triggered: {} stock={}, target={}, current={}",
                            alert.getAlertNo(), alert.getStockCode(), targetPrice, currentPrice);

                    // Send email notification - to a configured user email
                    emailService.sendAlertEmail(
                            alert.getUserId() + "@qq.com",
                            alert.getStockName(),
                            alert.getAlertType(),
                            targetPrice,
                            currentPrice);
                }
            } catch (Exception e) {
                log.error("Error checking alert {}: {}", alert.getAlertNo(), e.getMessage(), e);
            }
        }
    }

    private PriceAlertVO toAlertVO(PriceAlert alert) {
        PriceAlertVO vo = new PriceAlertVO();
        vo.setId(alert.getId());
        vo.setAlertNo(alert.getAlertNo());
        vo.setUserId(alert.getUserId());
        vo.setStockCode(alert.getStockCode());
        vo.setStockName(alert.getStockName());
        vo.setAlertType(alert.getAlertType());
        vo.setTargetPrice(alert.getTargetPrice());
        vo.setStatus(alert.getStatus());
        vo.setCreateTime(alert.getCreateTime());
        vo.setUpdateTime(alert.getUpdateTime());
        return vo;
    }

    private NotificationVO toNotificationVO(PriceAlertNotification n) {
        NotificationVO vo = new NotificationVO();
        vo.setId(n.getId());
        vo.setAlertId(n.getAlertId());
        vo.setUserId(n.getUserId());
        vo.setStockCode(n.getStockCode());
        vo.setStockName(n.getStockName());
        vo.setAlertType(n.getAlertType());
        vo.setTargetPrice(n.getTargetPrice());
        vo.setTriggeredPrice(n.getTriggeredPrice());
        vo.setIsRead(n.getIsRead());
        vo.setCreateTime(n.getCreateTime());
        return vo;
    }
}
