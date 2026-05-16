package com.stock.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.common.entity.PriceAlert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PriceAlertMapper extends BaseMapper<PriceAlert> {
}
