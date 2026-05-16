package com.stock.trading.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.common.constant.ConditionType;
import com.stock.common.constant.ConditionalOrderStatus;
import com.stock.common.constant.OrderDirection;
import com.stock.common.entity.ConditionalOrder;
import com.stock.common.entity.Order;
import com.stock.common.entity.Position;
import com.stock.common.exception.BusinessException;
import com.stock.trading.dto.ConditionalOrderRequest;
import com.stock.trading.dto.ConditionalOrderVO;
import com.stock.trading.mapper.ConditionalOrderMapper;
import com.stock.trading.mapper.FundAccountMapper;
import com.stock.trading.mapper.OrderMapper;
import com.stock.trading.mapper.PositionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConditionalOrderService {

    @Autowired
    private ConditionalOrderMapper conditionalOrderMapper;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private FundAccountMapper fundAccountMapper;

    @Transactional
    public ConditionalOrder createConditionalOrder(ConditionalOrderRequest request) {
        // Validate condition type
        if (!ConditionType.STOP_LOSS.equals(request.getConditionType())
                && !ConditionType.TAKE_PROFIT.equals(request.getConditionType())) {
            throw new BusinessException(400, "不支持的条件类型");
        }

        // Validate trigger price is positive
        if (request.getTriggerPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "触发价格必须大于0");
        }

        // Validate order price is positive
        if (request.getOrderPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "订单价格必须大于0");
        }

        // Validate quantity is positive
        if (request.getQuantity() <= 0) {
            throw new BusinessException(400, "数量必须大于0");
        }

        // For sell conditional orders (stop-loss / take-profit), validate user has position
        if (request.getDirection() == OrderDirection.SELL) {
            Position position = findPosition(request.getUserId(), request.getStockCode());
            if (position == null || position.getAvailableQuantity() < request.getQuantity()) {
                throw new BusinessException(400, "持仓不足");
            }
        }

        // Validate fund account exists and is active
        if (fundAccountMapper.selectById(request.getFundAccountId()) == null) {
            throw new BusinessException(400, "资金账户不存在");
        }

        ConditionalOrder order = new ConditionalOrder();
        order.setOrderNo("COT" + System.currentTimeMillis() + RandomUtil.randomInt(1000, 9999));
        order.setUserId(request.getUserId());
        order.setFundAccountId(request.getFundAccountId());
        order.setStockCode(request.getStockCode());
        order.setStockName(request.getStockName());
        order.setConditionType(request.getConditionType());
        order.setTriggerPrice(request.getTriggerPrice());
        order.setOrderPrice(request.getOrderPrice());
        order.setQuantity(request.getQuantity());
        order.setDirection(request.getDirection());
        order.setStatus(ConditionalOrderStatus.ACTIVE);
        conditionalOrderMapper.insert(order);

        return order;
    }

    @Transactional
    public void cancelConditionalOrder(Long id, Long userId) {
        ConditionalOrder order = conditionalOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(400, "条件单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权取消此条件单");
        }
        if (!ConditionalOrderStatus.ACTIVE.equals(order.getStatus())) {
            throw new BusinessException(400, "当前状态不允许取消");
        }

        order.setStatus(ConditionalOrderStatus.CANCELLED);
        conditionalOrderMapper.updateById(order);
    }

    public List<ConditionalOrderVO> getConditionalOrders(Long userId) {
        QueryWrapper<ConditionalOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        List<ConditionalOrder> orders = conditionalOrderMapper.selectList(wrapper);

        List<ConditionalOrderVO> voList = new ArrayList<>();
        for (ConditionalOrder order : orders) {
            voList.add(toVO(order));
        }
        return voList;
    }

    public ConditionalOrderVO getConditionalOrderDetail(Long id) {
        ConditionalOrder order = conditionalOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(400, "条件单不存在");
        }
        return toVO(order);
    }

    private ConditionalOrderVO toVO(ConditionalOrder order) {
        ConditionalOrderVO vo = new ConditionalOrderVO();
        BeanUtils.copyProperties(order, vo);

        // Populate triggered order number if triggered
        if (order.getTriggeredOrderId() != null) {
            Order triggeredOrder = orderMapper.selectById(order.getTriggeredOrderId());
            if (triggeredOrder != null) {
                vo.setTriggeredOrderNo(triggeredOrder.getOrderNo());
            }
        }
        return vo;
    }

    private Position findPosition(Long userId, String stockCode) {
        QueryWrapper<Position> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("stock_code", stockCode);
        return positionMapper.selectList(wrapper).stream().findFirst().orElse(null);
    }
}
