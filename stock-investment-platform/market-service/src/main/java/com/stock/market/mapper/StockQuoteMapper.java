package com.stock.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.common.entity.StockQuoteEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockQuoteMapper extends BaseMapper<StockQuoteEntity> {
}
