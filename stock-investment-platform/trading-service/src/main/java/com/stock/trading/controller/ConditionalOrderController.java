package com.stock.trading.controller;

import com.stock.common.entity.ConditionalOrder;
import com.stock.trading.dto.ConditionalOrderRequest;
import com.stock.trading.dto.ConditionalOrderVO;
import com.stock.trading.service.ConditionalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conditional-order")
public class ConditionalOrderController {

    @Autowired
    private ConditionalOrderService conditionalOrderService;

    @PostMapping("/create")
    public Map<String, Object> create(@Valid @RequestBody ConditionalOrderRequest request) {
        ConditionalOrder order = conditionalOrderService.createConditionalOrder(request);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "条件单创建成功");
        result.put("data", order);
        return result;
    }

    @GetMapping("/list/{userId}")
    public Map<String, Object> list(@PathVariable Long userId) {
        List<ConditionalOrderVO> orders = conditionalOrderService.getConditionalOrders(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", orders);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        ConditionalOrderVO order = conditionalOrderService.getConditionalOrderDetail(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", order);
        return result;
    }

    @PostMapping("/cancel/{id}")
    public Map<String, Object> cancel(@PathVariable Long id, @RequestParam Long userId) {
        conditionalOrderService.cancelConditionalOrder(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "条件单取消成功");
        return result;
    }
}
