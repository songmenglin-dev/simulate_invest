package com.stock.user.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.stock.common.constant.RedisConstant;
import com.stock.common.entity.FundAccount;
import com.stock.common.entity.User;
import com.stock.common.exception.BusinessException;
import com.stock.common.util.JwtUtil;
import com.stock.common.util.PasswordUtil;
import com.stock.user.dto.LoginRequest;
import com.stock.user.dto.LoginResponse;
import com.stock.user.dto.RegisterRequest;
import com.stock.user.mapper.FundAccountMapper;
import com.stock.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FundAccountMapper fundAccountMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CaptchaService captchaService;

    @Transactional
    public User register(RegisterRequest request) {
        // 验证验证码
        if (!captchaService.verifyCaptcha(request.getCaptchaId(), request.getCaptcha())) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        // 检查用户名是否已存在
        if (userMapper.selectCount(null) > 0 &&
            userMapper.selectList(null).stream().anyMatch(u -> u.getUsername().equals(request.getUsername()))) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        userMapper.insert(user);

        // 创建资金账户
        FundAccount fundAccount = new FundAccount();
        fundAccount.setUserId(user.getId());
        fundAccount.setAccountNo(generateAccountNo());
        fundAccount.setBalance(new BigDecimal("100000.00")); // 初始化10万模拟资金
        fundAccount.setFrozenBalance(BigDecimal.ZERO);
        fundAccount.setStatus(1);
        fundAccountMapper.insert(fundAccount);

        return user;
    }

    public LoginResponse login(LoginRequest request) {
        // 验证验证码
        if (!captchaService.verifyCaptcha(request.getCaptchaId(), request.getCaptcha())) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        // 查找用户
        User user = userMapper.selectList(null).stream()
                .filter(u -> u.getUsername().equals(request.getUsername()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        // 验证密码
        if (!PasswordUtil.match(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查状态
        if (user.getStatus() != 1) {
            throw new BusinessException(403, "账户已被禁用");
        }

        // 生成Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 存储到Redis
        String key = RedisConstant.TOKEN_PREFIX + user.getId();
        redisTemplate.opsForValue().set(key, token, RedisConstant.TOKEN_EXPIRE, TimeUnit.SECONDS);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }

    public void logout(Long userId) {
        String key = RedisConstant.TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }

    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    public String uploadAvatar(Long userId, byte[] bytes, String fileName) {
        // 这里简化处理，实际应上传到MinIO
        // 返回一个模拟的URL
        String avatarUrl = "https://minio.example.com/avatars/" + userId + "/" + fileName;

        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setAvatarUrl(avatarUrl);
            userMapper.updateById(user);
        }
        return avatarUrl;
    }

    private String generateAccountNo() {
        return "FA" + System.currentTimeMillis() + RandomUtil.randomInt(1000, 9999);
    }
}