package com.stock.trading;

import com.stock.common.constant.OrderDirection;
import com.stock.common.constant.OrderStatus;
import com.stock.common.entity.FundAccount;
import com.stock.common.entity.Order;
import com.stock.common.entity.Position;
import com.stock.common.exception.BusinessException;
import com.stock.trading.dto.OrderRequest;
import com.stock.trading.mapper.FundAccountMapper;
import com.stock.trading.mapper.OrderMapper;
import com.stock.trading.mapper.PositionMapper;
import com.stock.trading.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("订单服务测试")
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;
    @Mock
    private PositionMapper positionMapper;
    @Mock
    private FundAccountMapper fundAccountMapper;

    @InjectMocks
    private OrderService orderService;

    private FundAccount fundAccount;
    private OrderRequest buyRequest;

    @BeforeEach
    void setUp() {
        fundAccount = new FundAccount();
        fundAccount.setId(1L);
        fundAccount.setUserId(1L);
        fundAccount.setBalance(new BigDecimal("200000"));
        fundAccount.setFrozenBalance(BigDecimal.ZERO);
        fundAccount.setStatus(1);

        buyRequest = new OrderRequest();
        buyRequest.setUserId(1L);
        buyRequest.setFundAccountId(1L);
        buyRequest.setStockCode("600519");
        buyRequest.setStockName("贵州茅台");
        buyRequest.setDirection(OrderDirection.BUY);
        buyRequest.setPrice(new BigDecimal("1680"));
        buyRequest.setQuantity(100);
    }

    @Test
    @DisplayName("8.5 下单成功：余额充足时创建订单")
    void placeBuyOrder_sufficientBalance_createsOrder() {
        when(fundAccountMapper.selectById(1L)).thenReturn(fundAccount);

        Order order = orderService.placeOrder(buyRequest);

        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(0, new BigDecimal("168000").compareTo(order.getAmount()));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderMapper).insert(orderCaptor.capture());
        assertEquals("600519", orderCaptor.getValue().getStockCode());
    }

    @Test
    @DisplayName("8.5 下单失败：余额不足时抛出异常")
    void placeBuyOrder_insufficientBalance_throwsException() {
        fundAccount.setBalance(new BigDecimal("1000"));
        when(fundAccountMapper.selectById(1L)).thenReturn(fundAccount);

        assertThrows(BusinessException.class, () -> orderService.placeOrder(buyRequest));
        verify(orderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("8.5 确认订单成功：状态变为已成交")
    void confirmOrder_validOrder_executesTrade() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setFundAccountId(1L);
        order.setStockCode("600519");
        order.setStockName("贵州茅台");
        order.setDirection(OrderDirection.BUY);
        order.setPrice(new BigDecimal("1680"));
        order.setQuantity(100);
        order.setAmount(new BigDecimal("168000"));
        order.setStatus(OrderStatus.PENDING);

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(fundAccountMapper.selectById(1L)).thenReturn(fundAccount);

        Order confirmed = orderService.confirmOrder(1L, 1L);

        assertEquals(OrderStatus.FILLED, order.getStatus());
        verify(orderMapper, atLeastOnce()).updateById(order);
    }
}