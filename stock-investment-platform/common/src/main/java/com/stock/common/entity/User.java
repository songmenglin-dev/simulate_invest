package com.stock.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatarUrl;
    private Integer status; // 1-正常，0-禁用
}