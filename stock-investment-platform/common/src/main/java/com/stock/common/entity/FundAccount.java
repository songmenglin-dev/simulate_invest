package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fund_accounts")
public class FundAccount extends BaseEntity {
    private Long userId;
    private String accountNo;
    private BigDecimal balance;
    private BigDecimal frozenBalance;
    private Integer status; // 1-正常，0-禁用
}