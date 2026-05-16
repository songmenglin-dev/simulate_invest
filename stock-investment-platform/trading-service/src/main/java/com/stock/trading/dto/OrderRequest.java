package com.stock.trading.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OrderRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "资金账户ID不能为空")
    private Long fundAccountId;

    @NotBlank(message = "股票代码不能为空")
    private String stockCode;

    @NotBlank(message = "股票名称不能为空")
    private String stockName;

    @NotNull(message = "方向不能为空")
    private Integer direction; // 1-买入，2-卖出

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    private Integer orderType = 1; // 1-市价，2-限价
}