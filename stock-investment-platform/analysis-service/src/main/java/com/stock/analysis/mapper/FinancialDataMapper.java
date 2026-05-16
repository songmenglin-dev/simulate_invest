package com.stock.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.common.entity.FinancialData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FinancialDataMapper extends BaseMapper<FinancialData> {
}