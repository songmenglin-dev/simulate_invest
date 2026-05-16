package com.stock.user.controller;

import com.stock.common.entity.User;
import com.stock.common.util.JwtUtil;
import com.stock.user.dto.LoginRequest;
import com.stock.user.dto.LoginResponse;
import com.stock.user.dto.RegisterRequest;
import com.stock.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/captcha")
    public Map<String, String> getCaptcha() {
        return userService.generateCaptcha();
    }

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return Map.of("code", 200, "message", "注册成功", "data", user.getId());
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Map.of("code", 200, "message", "登录成功", "data", response);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long userId = JwtUtil.getUserId(token);
            if (userId != null) {
                userService.logout(userId);
            }
        }
        return Map.of("code", 200, "message", "退出成功");
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Map.of("code", 401, "message", "未登录");
        }
        token = token.substring(7);
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Map.of("code", 401, "message", "Token无效");
        }
        User user = userService.getUserById(userId);
        return Map.of("code", 200, "message", "success", "data", user);
    }

    @PostMapping("/avatar")
    public Map<String, Object> uploadAvatar(HttpServletRequest request, @RequestParam("file") byte[] bytes, @RequestParam("fileName") String fileName) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Map.of("code", 401, "message", "未登录");
        }
        token = token.substring(7);
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Map.of("code", 401, "message", "Token无效");
        }
        String avatarUrl = userService.uploadAvatar(userId, bytes, fileName);
        return Map.of("code", 200, "message", "上传成功", "data", Map.of("avatarUrl", avatarUrl));
    }
}