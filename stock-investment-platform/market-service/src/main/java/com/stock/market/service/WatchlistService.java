package com.stock.market.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.common.entity.StockQuoteEntity;
import com.stock.common.entity.Watchlist;
import com.stock.market.dto.WatchlistQuoteVO;
import com.stock.market.dto.WatchlistRequest;
import com.stock.market.dto.WatchlistVO;
import com.stock.market.mapper.StockQuoteMapper;
import com.stock.market.mapper.WatchlistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WatchlistService {

    @Autowired
    private WatchlistMapper watchlistMapper;

    @Autowired
    private StockQuoteMapper stockQuoteMapper;

    public Watchlist addWatchlist(WatchlistRequest req) {
        QueryWrapper<Watchlist> qw = new QueryWrapper<>();
        qw.eq("user_id", req.getUserId());
        qw.eq("stock_code", req.getStockCode());
        Watchlist existing = watchlistMapper.selectOne(qw);
        if (existing != null) {
            return existing;
        }
        Watchlist watchlist = new Watchlist();
        watchlist.setUserId(req.getUserId());
        watchlist.setStockCode(req.getStockCode());
        watchlist.setStockName(req.getStockName());
        watchlistMapper.insert(watchlist);
        return watchlist;
    }

    public void removeWatchlist(Long userId, String stockCode) {
        QueryWrapper<Watchlist> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.eq("stock_code", stockCode);
        watchlistMapper.delete(qw);
    }

    public List<WatchlistVO> getWatchlist(Long userId) {
        QueryWrapper<Watchlist> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.orderByDesc("create_time");
        List<Watchlist> list = watchlistMapper.selectList(qw);
        return list.stream().map(w -> {
            WatchlistVO vo = new WatchlistVO();
            vo.setId(w.getId());
            vo.setStockCode(w.getStockCode());
            vo.setStockName(w.getStockName());
            vo.setCreateTime(w.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    public List<WatchlistQuoteVO> getWatchlistQuotes(Long userId) {
        List<WatchlistVO> watchlist = getWatchlist(userId);
        List<WatchlistQuoteVO> result = new ArrayList<>();
        for (WatchlistVO item : watchlist) {
            WatchlistQuoteVO vo = new WatchlistQuoteVO();
            vo.setId(item.getId());
            vo.setStockCode(item.getStockCode());
            vo.setStockName(item.getStockName());
            vo.setCreateTime(item.getCreateTime());

            QueryWrapper<StockQuoteEntity> sqw = new QueryWrapper<>();
            sqw.eq("stock_code", item.getStockCode());
            StockQuoteEntity quote = stockQuoteMapper.selectOne(sqw);
            if (quote != null) {
                vo.setCurrentPrice(quote.getCurrentPrice());
                vo.setChange(quote.getPriceChange());
                vo.setChangePercent(quote.getChangePercent());
            }
            result.add(vo);
        }
        return result;
    }
}
