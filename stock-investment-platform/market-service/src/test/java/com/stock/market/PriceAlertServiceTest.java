package com.stock.market;

import com.stock.common.constant.AlertType;
import com.stock.common.entity.PriceAlert;
import com.stock.common.entity.PriceAlertNotification;
import com.stock.market.dto.NotificationVO;
import com.stock.market.dto.PriceAlertRequest;
import com.stock.market.dto.PriceAlertVO;
import com.stock.market.mapper.PriceAlertMapper;
import com.stock.market.mapper.PriceAlertNotificationMapper;
import com.stock.market.mapper.StockQuoteMapper;
import com.stock.market.service.EmailService;
import com.stock.market.service.PriceAlertService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("价格预警服务测试")
class PriceAlertServiceTest {

    @Mock
    private PriceAlertMapper priceAlertMapper;
    @Mock
    private PriceAlertNotificationMapper notificationMapper;
    @Mock
    private StockQuoteMapper stockQuoteMapper;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private PriceAlertService service;

    @BeforeEach
    void setUp() {
        lenient().doNothing().when(emailService)
                .sendAlertEmail(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    @DisplayName("创建价格预警成功")
    void create_setsActiveStatus() {
        PriceAlertRequest req = new PriceAlertRequest();
        req.setUserId(1L);
        req.setStockCode("600519");
        req.setStockName("贵州茅台");
        req.setAlertType(AlertType.PRICE_ABOVE);
        req.setTargetPrice(new BigDecimal("2000"));

        PriceAlert result = service.createAlert(req);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getAlertNo());
        verify(priceAlertMapper).insert(any(PriceAlert.class));
    }

    @Test
    @DisplayName("取消预警更新状态")
    void cancel_updatesStatus() {
        PriceAlert alert = new PriceAlert();
        alert.setId(1L);
        alert.setUserId(1L);
        when(priceAlertMapper.selectById(1L)).thenReturn(alert);

        service.cancelAlert(1L, 1L);

        verify(priceAlertMapper).update(any(), any());
    }

    @Test
    @DisplayName("取消不存在的预警抛出异常")
    void cancel_nonexistent_throwsException() {
        when(priceAlertMapper.selectById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> service.cancelAlert(1L, 1L));
    }

    @Test
    @DisplayName("获取预警列表返回VO")
    void getAlerts_returnsVOList() {
        PriceAlert alert = new PriceAlert();
        alert.setId(1L);
        alert.setUserId(1L);
        alert.setAlertNo("ALT001");
        alert.setStockCode("600519");
        alert.setStockName("贵州茅台");
        alert.setAlertType(AlertType.PRICE_ABOVE);
        alert.setTargetPrice(new BigDecimal("2000"));
        alert.setStatus("ACTIVE");
        when(priceAlertMapper.selectList(any())).thenReturn(Arrays.asList(alert));

        List<PriceAlertVO> list = service.getAlerts(1L, null);

        assertEquals(1, list.size());
        assertEquals("600519", list.get(0).getStockCode());
        assertEquals("ACTIVE", list.get(0).getStatus());
    }

    @Test
    @DisplayName("获取通知列表返回VO")
    void getNotifications_returnsVOList() {
        PriceAlertNotification n = new PriceAlertNotification();
        n.setId(1L);
        n.setUserId(1L);
        n.setAlertId(1L);
        n.setStockCode("600519");
        n.setStockName("贵州茅台");
        n.setAlertType(AlertType.PRICE_ABOVE);
        n.setTargetPrice(new BigDecimal("2000"));
        n.setTriggeredPrice(new BigDecimal("2005"));
        n.setIsRead(0);
        when(notificationMapper.selectList(any())).thenReturn(Arrays.asList(n));

        List<NotificationVO> list = service.getNotifications(1L);

        assertEquals(1, list.size());
        assertEquals("600519", list.get(0).getStockCode());
        assertEquals(0, list.get(0).getIsRead());
    }

    @Test
    @DisplayName("标记单条通知已读")
    void markNotificationRead_updatesFlag() {
        service.markNotificationRead(1L);

        verify(notificationMapper).update(any(), any());
    }

    @Test
    @DisplayName("标记所有通知已读")
    void markAllNotificationsRead_updatesAll() {
        service.markAllNotificationsRead(1L);

        verify(notificationMapper).update(any(), any());
    }

    @Test
    @DisplayName("获取未读通知数量")
    void getUnreadCount_returnsCount() {
        when(notificationMapper.selectCount(any())).thenReturn(5L);

        int count = service.getUnreadCount(1L);

        assertEquals(5, count);
    }
}
