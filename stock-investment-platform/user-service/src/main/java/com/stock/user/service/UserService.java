package com.stock.user.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
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
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
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
    private MinioClient minioClient;

    @Value("${minio.bucket:avatars}")
    private String bucketName;

    private static final String TOKEN_PREFIX = "user:token:";
    private static final long TOKEN_EXPIRE_SECONDS = 86400 * 7; // 7 days

    @Transactional
    public User register(RegisterRequest request) {
        if (userMapper.selectCount(null) > 0 &&
            userMapper.selectList(null).stream().anyMatch(u -> u.getUsername().equals(request.getUsername()))) {
            throw new BusinessException(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        userMapper.insert(user);

        FundAccount fundAccount = new FundAccount();
        fundAccount.setUserId(user.getId());
        fundAccount.setAccountNo(generateAccountNo());
        fundAccount.setBalance(new BigDecimal("100000.00"));
        fundAccount.setFrozenBalance(BigDecimal.ZERO);
        fundAccount.setStatus(1);
        fundAccountMapper.insert(fundAccount);

        return user;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectList(null).stream()
                .filter(u -> u.getUsername().equals(request.getUsername()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        if (!PasswordUtil.match(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException(403, "账户已被禁用");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        redisTemplate.opsForValue().set(TOKEN_PREFIX + user.getId(), token, TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }

    public void logout(Long userId) {
        redisTemplate.delete(TOKEN_PREFIX + userId);
    }

    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    public String uploadAvatar(Long userId, byte[] bytes, String fileName) {
        String objectName = "avatars/" + userId + "/" + fileName;
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                            .contentType("image/" + getExtension(fileName))
                            .build());
        } catch (Exception e) {
            throw new BusinessException(500, "头像上传失败: " + e.getMessage());
        }

        String avatarUrl = "/" + bucketName + "/" + objectName;

        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setAvatarUrl(avatarUrl);
            userMapper.updateById(user);
        }
        return avatarUrl;
    }

    private String getExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        return idx > 0 ? fileName.substring(idx + 1) : "png";
    }

    private String generateAccountNo() {
        return "FA" + System.currentTimeMillis() + RandomUtil.randomInt(1000, 9999);
    }
}