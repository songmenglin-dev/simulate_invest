package com.stock.portfolio;

import com.stock.common.entity.FundAccount;
import com.stock.common.entity.Position;
import com.stock.portfolio.dto.PortfolioOverview;
import com.stock.portfolio.mapper.FundAccountMapper;
import com.stock.portfolio.mapper.PositionMapper;
import com.stock.portfolio.service.PortfolioService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("投资组合服务测试")
class PortfolioServiceTest {

    @Mock
    private PositionMapper positionMapper;
    @Mock
    private FundAccountMapper fundAccountMapper;

    @InjectMocks
    private PortfolioService portfolioService;

    private FundAccount fundAccount;

    @BeforeEach
    void setUp() {
        fundAccount = new FundAccount();
        fundAccount.setId(1L);
        fundAccount.setUserId(1L);
        fundAccount.setBalance(new BigDecimal("83200"));
        fundAccount.setFrozenBalance(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("8.6 盈亏计算：持仓盈利正确计算")
    void getOverview_withPositions_calculatesProfitLoss() {
        Position pos = new Position();
        pos.setUserId(1L);
        pos.setStockCode("600519");
        pos.setStockName("贵州茅台");
        pos.setTotalQuantity(100);
        pos.setAvailableQuantity(100);
        pos.setFrozenQuantity(0);
        pos.setAvgCost(new BigDecimal("1600"));

        when(fundAccountMapper.selectList(null)).thenReturn(Collections.singletonList(fundAccount));
        when(positionMapper.selectList(null)).thenReturn(Collections.singletonList(pos));

        PortfolioOverview overview = portfolioService.getOverview(1L);

        assertNotNull(overview);
        // 当前市价 1680 * 100 = 168000
        assertEquals(0, new BigDecimal("168000").compareTo(overview.getTotalMarketValue()));
        // 盈亏 = 168000 - 160000 = 8000
        assertTrue(overview.getTotalProfitLoss().compareTo(BigDecimal.ZERO) > 0,
                "应显示盈利");
        // 总资产 = 现金 + 市值
        assertEquals(0, new BigDecimal("251200").compareTo(overview.getTotalAssets()));
    }

    @Test
    @DisplayName("8.6 空持仓：总资产仅包含现金")
    void getOverview_emptyPositions_onlyCash() {
        when(fundAccountMapper.selectList(null)).thenReturn(Collections.singletonList(fundAccount));
        when(positionMapper.selectList(null)).thenReturn(Collections.emptyList());

        PortfolioOverview overview = portfolioService.getOverview(1L);

        assertEquals(0, new BigDecimal("83200").compareTo(overview.getTotalAssets()));
        assertEquals(0, BigDecimal.ZERO.compareTo(overview.getTotalMarketValue()));
        assertEquals(BigDecimal.ZERO, overview.getTotalProfitLoss());
    }
}