package com.stock.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.common.entity.BacktestStrategy;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BacktestStrategyMapper extends BaseMapper<BacktestStrategy> {
}
