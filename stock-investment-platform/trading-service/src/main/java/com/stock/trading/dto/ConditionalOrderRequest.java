package com.stock.trading.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ConditionalOrderRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "资金账户ID不能为空")
    private Long fundAccountId;

    @NotBlank(message = "股票代码不能为空")
    private String stockCode;

    @NotBlank(message = "股票名称不能为空")
    private String stockName;

    @NotBlank(message = "条件类型不能为空")
    private String conditionType;

    @NotNull(message = "触发价格不能为空")
    private BigDecimal triggerPrice;

    @NotNull(message = "订单价格不能为空")
    private BigDecimal orderPrice;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    @NotNull(message = "方向不能为空")
    private Integer direction;
}
