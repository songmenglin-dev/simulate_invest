package com.stock.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.common.entity.StockKLine;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockKLineMapper extends BaseMapper<StockKLine> {
}
