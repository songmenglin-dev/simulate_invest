package com.stock.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.common.entity.Stock;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {
}