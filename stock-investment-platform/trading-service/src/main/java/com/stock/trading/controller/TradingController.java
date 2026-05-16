package com.stock.trading.controller;

import com.stock.common.entity.Order;
import com.stock.trading.dto.OrderRequest;
import com.stock.trading.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class TradingController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public Map<String, Object> placeOrder(@Valid @RequestBody OrderRequest request) {
        Order order = orderService.placeOrder(request);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "下单成功");
        result.put("data", order);
        return result;
    }

    @PostMapping("/confirm/{orderId}")
    public Map<String, Object> confirmOrder(@PathVariable Long orderId, @RequestParam Long userId) {
        Order order = orderService.confirmOrder(orderId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "确认成功");
        result.put("data", order);
        return result;
    }

    @PostMapping("/cancel/{orderId}")
    public Map<String, Object> cancelOrder(@PathVariable Long orderId, @RequestParam Long userId) {
        orderService.cancelOrder(orderId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "撤单成功");
        return result;
    }

    @GetMapping("/history/{userId}")
    public Map<String, Object> getOrderHistory(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrderHistory(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", orders);
        return result;
    }
}