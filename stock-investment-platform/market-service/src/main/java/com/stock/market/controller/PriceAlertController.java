package com.stock.market.controller;

import com.stock.common.entity.PriceAlert;
import com.stock.market.dto.NotificationVO;
import com.stock.market.dto.PriceAlertRequest;
import com.stock.market.dto.PriceAlertVO;
import com.stock.market.service.PriceAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alert")
public class PriceAlertController {

    @Autowired
    private PriceAlertService priceAlertService;

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody PriceAlertRequest req) {
        PriceAlert alert = priceAlertService.createAlert(req);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", alert);
        return result;
    }

    @GetMapping("/list/{userId}")
    public Map<String, Object> list(@PathVariable Long userId,
                                    @RequestParam(required = false) String status) {
        List<PriceAlertVO> list = priceAlertService.getAlerts(userId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", list);
        return result;
    }

    @PostMapping("/cancel/{id}")
    public Map<String, Object> cancel(@PathVariable Long id, @RequestParam Long userId) {
        priceAlertService.cancelAlert(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        return result;
    }

    @GetMapping("/notifications/{userId}")
    public Map<String, Object> notifications(@PathVariable Long userId) {
        List<NotificationVO> list = priceAlertService.getNotifications(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", list);
        return result;
    }

    @PostMapping("/notifications/read/{id}")
    public Map<String, Object> read(@PathVariable Long id) {
        priceAlertService.markNotificationRead(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        return result;
    }

    @PostMapping("/notifications/read-all/{userId}")
    public Map<String, Object> readAll(@PathVariable Long userId) {
        priceAlertService.markAllNotificationsRead(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        return result;
    }

    @GetMapping("/notifications/unread-count/{userId}")
    public Map<String, Object> unreadCount(@PathVariable Long userId) {
        int count = priceAlertService.getUnreadCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", count);
        return result;
    }
}
