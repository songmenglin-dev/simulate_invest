package com.stock.market;

import com.stock.common.entity.Watchlist;
import com.stock.market.dto.WatchlistQuoteVO;
import com.stock.market.dto.WatchlistRequest;
import com.stock.market.dto.WatchlistVO;
import com.stock.market.mapper.StockQuoteMapper;
import com.stock.market.mapper.WatchlistMapper;
import com.stock.market.service.WatchlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("自选股服务测试")
class WatchlistServiceTest {

    @Mock
    private WatchlistMapper watchlistMapper;

    @Mock
    private StockQuoteMapper stockQuoteMapper;

    @InjectMocks
    private WatchlistService service;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("添加自选股成功")
    void add_newStock_success() {
        when(watchlistMapper.selectOne(any())).thenReturn(null);

        WatchlistRequest req = new WatchlistRequest();
        req.setUserId(1L);
        req.setStockCode("600519");
        req.setStockName("贵州茅台");

        Watchlist result = service.addWatchlist(req);

        assertNotNull(result);
        assertEquals("600519", result.getStockCode());
        assertEquals("贵州茅台", result.getStockName());
        verify(watchlistMapper).insert(any(Watchlist.class));
    }

    @Test
    @DisplayName("添加已存在的自选股返回现有记录")
    void add_duplicateStock_returnsExisting() {
        Watchlist existing = new Watchlist();
        existing.setId(1L);
        existing.setUserId(1L);
        existing.setStockCode("600519");
        existing.setStockName("贵州茅台");
        when(watchlistMapper.selectOne(any())).thenReturn(existing);

        WatchlistRequest req = new WatchlistRequest();
        req.setUserId(1L);
        req.setStockCode("600519");
        req.setStockName("贵州茅台");

        Watchlist result = service.addWatchlist(req);

        assertEquals(existing.getId(), result.getId());
        verify(watchlistMapper, never()).insert(any());
    }

    @Test
    @DisplayName("删除自选股调用mapper")
    void remove_deletesViaMapper() {
        service.removeWatchlist(1L, "600519");

        verify(watchlistMapper).delete(any());
    }

    @Test
    @DisplayName("获取自选股列表返回VO")
    void getWatchlist_returnsVOList() {
        Watchlist w = new Watchlist();
        w.setId(1L);
        w.setUserId(1L);
        w.setStockCode("600519");
        w.setStockName("贵州茅台");
        when(watchlistMapper.selectList(any())).thenReturn(Arrays.asList(w));

        List<WatchlistVO> result = service.getWatchlist(1L);

        assertEquals(1, result.size());
        assertEquals("600519", result.get(0).getStockCode());
        assertEquals("贵州茅台", result.get(0).getStockName());
    }

    @Test
    @DisplayName("空自选股列表返回空列表而非null")
    void getWatchlist_empty_returnsEmptyList() {
        when(watchlistMapper.selectList(any())).thenReturn(Collections.emptyList());

        List<WatchlistVO> result = service.getWatchlist(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取自选股行情返回带价格的VO")
    void getWatchlistQuotes_returnsQuotes() {
        Watchlist w = new Watchlist();
        w.setId(1L);
        w.setUserId(1L);
        w.setStockCode("600519");
        w.setStockName("贵州茅台");
        when(watchlistMapper.selectList(any())).thenReturn(Arrays.asList(w));

        List<WatchlistQuoteVO> result = service.getWatchlistQuotes(1L);

        assertEquals(1, result.size());
        assertEquals("600519", result.get(0).getStockCode());
    }
}
