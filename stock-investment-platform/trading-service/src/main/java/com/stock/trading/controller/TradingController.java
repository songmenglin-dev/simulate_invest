package com.stock.trading.controller;

import com.stock.common.entity.Order;
import com.stock.trading.dto.OrderRequest;
import com.stock.trading.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        return Map.of("code", 200, "message", "下单成功", "data", order);
    }

    @PostMapping("/cancel/{orderId}")
    public Map<String, Object> cancelOrder(@PathVariable Long orderId, @RequestParam Long userId) {
        orderService.cancelOrder(orderId, userId);
        return Map.of("code", 200, "message", "撤单成功");
    }

    @GetMapping("/history/{userId}")
    public Map<String, Object> getOrderHistory(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrderHistory(userId);
        return Map.of("code", 200, "message", "success", "data", orders);
    }
}