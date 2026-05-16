package com.stock.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.common.entity.PriceAlertNotification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PriceAlertNotificationMapper extends BaseMapper<PriceAlertNotification> {
}
