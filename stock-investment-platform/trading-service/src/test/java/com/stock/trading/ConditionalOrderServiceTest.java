package com.stock.trading;

import com.stock.common.constant.ConditionType;
import com.stock.common.constant.ConditionalOrderStatus;
import com.stock.common.constant.OrderDirection;
import com.stock.common.entity.ConditionalOrder;
import com.stock.common.entity.FundAccount;
import com.stock.common.entity.Position;
import com.stock.common.exception.BusinessException;
import com.stock.trading.dto.ConditionalOrderRequest;
import com.stock.trading.dto.ConditionalOrderVO;
import com.stock.trading.mapper.ConditionalOrderMapper;
import com.stock.trading.mapper.FundAccountMapper;
import com.stock.trading.mapper.OrderMapper;
import com.stock.trading.mapper.PositionMapper;
import com.stock.trading.service.ConditionalOrderService;
import com.stock.trading.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("条件单服务测试")
class ConditionalOrderServiceTest {

    @Mock
    private ConditionalOrderMapper conditionalOrderMapper;
    @Mock
    private PositionMapper positionMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderService orderService;
    @Mock
    private FundAccountMapper fundAccountMapper;

    @InjectMocks
    private ConditionalOrderService service;

    private ConditionalOrderRequest request;
    private FundAccount fundAccount;

    @BeforeEach
    void setUp() {
        fundAccount = new FundAccount();
        fundAccount.setId(1L);
        fundAccount.setUserId(1L);
        fundAccount.setBalance(new BigDecimal("200000"));
        fundAccount.setFrozenBalance(BigDecimal.ZERO);
        fundAccount.setStatus(1);

        request = new ConditionalOrderRequest();
        request.setUserId(1L);
        request.setFundAccountId(1L);
        request.setStockCode("600519");
        request.setStockName("贵州茅台");
        request.setDirection(OrderDirection.SELL);
        request.setConditionType(ConditionType.STOP_LOSS);
        request.setTriggerPrice(new BigDecimal("1600"));
        request.setOrderPrice(new BigDecimal("1590"));
        request.setQuantity(100);
    }

    @Test
    @DisplayName("创建止损条件单成功")
    void create_stopLoss_success() {
        when(fundAccountMapper.selectById(1L)).thenReturn(fundAccount);
        Position position = new Position();
        position.setAvailableQuantity(200);
        when(positionMapper.selectList(any())).thenReturn(Arrays.asList(position));

        ConditionalOrder result = service.createConditionalOrder(request);

        assertNotNull(result);
        assertEquals(ConditionalOrderStatus.ACTIVE, result.getStatus());
        assertNotNull(result.getOrderNo());
        verify(conditionalOrderMapper).insert(any(ConditionalOrder.class));
    }

    @Test
    @DisplayName("不支持的触发条件类型抛出异常")
    void create_unsupportedConditionType_throwsException() {
        request.setConditionType("INVALID_TYPE");

        assertThrows(BusinessException.class, () -> service.createConditionalOrder(request));
        verify(conditionalOrderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("触发价格<=0抛出异常")
    void create_negativeTriggerPrice_throwsException() {
        request.setTriggerPrice(BigDecimal.ZERO);

        assertThrows(BusinessException.class, () -> service.createConditionalOrder(request));
    }

    @Test
    @DisplayName("数量<=0抛出异常")
    void create_zeroQuantity_throwsException() {
        request.setQuantity(0);

        assertThrows(BusinessException.class, () -> service.createConditionalOrder(request));
    }

    @Test
    @DisplayName("卖出条件单持仓不足抛出异常")
    void create_sellWithoutPosition_throwsException() {
        when(positionMapper.selectList(any())).thenReturn(Collections.emptyList());

        assertThrows(BusinessException.class, () -> service.createConditionalOrder(request));
    }

    @Test
    @DisplayName("资金账户不存在抛出异常")
    void create_fundAccountNotFound_throwsException() {
        request.setDirection(OrderDirection.BUY);
        when(fundAccountMapper.selectById(1L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> service.createConditionalOrder(request));
    }

    @Test
    @DisplayName("取消条件单成功")
    void cancel_activeOrder_updatesStatus() {
        ConditionalOrder order = new ConditionalOrder();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(ConditionalOrderStatus.ACTIVE);
        when(conditionalOrderMapper.selectById(1L)).thenReturn(order);

        service.cancelConditionalOrder(1L, 1L);

        assertEquals(ConditionalOrderStatus.CANCELLED, order.getStatus());
        verify(conditionalOrderMapper).updateById(order);
    }

    @Test
    @DisplayName("取消不存在的条件单抛出异常")
    void cancel_nonexistentOrder_throwsException() {
        when(conditionalOrderMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> service.cancelConditionalOrder(999L, 1L));
    }

    @Test
    @DisplayName("取消他人条件单抛出异常")
    void cancel_otherUserOrder_throwsException() {
        ConditionalOrder order = new ConditionalOrder();
        order.setId(1L);
        order.setUserId(2L);
        when(conditionalOrderMapper.selectById(1L)).thenReturn(order);

        assertThrows(BusinessException.class, () -> service.cancelConditionalOrder(1L, 1L));
    }

    @Test
    @DisplayName("获取条件单列表返回正确的VO")
    void getConditionalOrders_returnsVOList() {
        ConditionalOrder order = new ConditionalOrder();
        order.setId(1L);
        order.setUserId(1L);
        order.setStockCode("600519");
        order.setStockName("贵州茅台");
        order.setConditionType(ConditionType.STOP_LOSS);
        order.setTriggerPrice(new BigDecimal("1600"));
        order.setOrderPrice(new BigDecimal("1590"));
        order.setQuantity(100);
        order.setDirection(OrderDirection.SELL);
        order.setStatus(ConditionalOrderStatus.ACTIVE);

        when(conditionalOrderMapper.selectList(any())).thenReturn(Arrays.asList(order));

        List<ConditionalOrderVO> list = service.getConditionalOrders(1L);

        assertEquals(1, list.size());
        assertEquals("600519", list.get(0).getStockCode());
        assertEquals(ConditionType.STOP_LOSS, list.get(0).getConditionType());
    }

    @Test
    @DisplayName("获取条件单详情返回VO")
    void getDetail_returnsVO() {
        ConditionalOrder order = new ConditionalOrder();
        order.setId(1L);
        order.setUserId(1L);
        order.setStockCode("600519");
        order.setStockName("贵州茅台");
        order.setConditionType(ConditionType.STOP_LOSS);
        order.setTriggerPrice(new BigDecimal("1600"));
        order.setOrderPrice(new BigDecimal("1590"));
        order.setQuantity(100);
        order.setDirection(OrderDirection.SELL);
        order.setStatus(ConditionalOrderStatus.ACTIVE);

        when(conditionalOrderMapper.selectById(1L)).thenReturn(order);

        ConditionalOrderVO vo = service.getConditionalOrderDetail(1L);

        assertNotNull(vo);
        assertEquals("600519", vo.getStockCode());
    }
}
