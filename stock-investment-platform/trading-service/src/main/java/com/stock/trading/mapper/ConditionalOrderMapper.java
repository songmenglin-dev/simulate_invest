package com.stock.trading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.common.entity.ConditionalOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConditionalOrderMapper extends BaseMapper<ConditionalOrder> {
}
