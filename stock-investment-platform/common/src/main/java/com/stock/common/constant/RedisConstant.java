package com.stock.common.constant;

public class RedisConstant {
    // Token前缀
    public static final String TOKEN_PREFIX = "stock:token:";
    // 验证码前缀
    public static final String CAPTCHA_PREFIX = "stock:captcha:";
    // Token过期时间（秒）
    public static final long TOKEN_EXPIRE = 86400; // 24小时
    // 验证码过期时间（秒）
    public static final long CAPTCHA_EXPIRE = 300; // 5分钟
}