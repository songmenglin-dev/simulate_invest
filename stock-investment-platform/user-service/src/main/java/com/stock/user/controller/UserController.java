package com.stock.user.controller;

import com.stock.common.entity.User;
import com.stock.common.util.JwtUtil;
import com.stock.user.dto.LoginRequest;
import com.stock.user.dto.LoginResponse;
import com.stock.user.dto.RegisterRequest;
import com.stock.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "注册成功");
        result.put("data", user.getId());
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "登录成功");
        result.put("data", response);
        return result;
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
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "退出成功");
        return result;
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "未登录");
            return result;
        }
        token = token.substring(7);
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "Token无效");
            return result;
        }
        User user = userService.getUserById(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", user);
        return result;
    }

    @PostMapping("/avatar")
    public Map<String, Object> uploadAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "未登录");
            return result;
        }
        token = token.substring(7);
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "Token无效");
            return result;
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "读取文件失败");
            return result;
        }
        String avatarUrl = userService.uploadAvatar(userId, bytes, fileName);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "上传成功");
        Map<String, Object> data = new HashMap<>();
        data.put("avatarUrl", avatarUrl);
        result.put("data", data);
        return result;
    }
}