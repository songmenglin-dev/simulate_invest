package com.stock.trading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.common.entity.Position;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PositionMapper extends BaseMapper<Position> {
}