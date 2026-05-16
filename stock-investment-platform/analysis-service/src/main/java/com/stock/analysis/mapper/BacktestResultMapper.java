package com.stock.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.common.entity.BacktestResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BacktestResultMapper extends BaseMapper<BacktestResult> {
}
