package com.stock.common.util;

import cn.hutool.crypto.SecureUtil;
import org.springframework.util.DigestUtils;

public class PasswordUtil {

    public static String encode(String password) {
        // 使用MD5 + 盐值进行加密（实际项目应使用BCrypt）
        String salt = "StockPlatformSalt2024";
        return SecureUtil.md5(password + salt);
    }

    public static boolean match(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}