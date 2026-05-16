package com.stock.user.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.stock.common.constant.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Map<String, String> generateCaptcha() {
        String captchaId = IdUtil.fastSimpleUUID();
        String code = RandomUtil.randomString(4).toUpperCase();

        redisTemplate.opsForValue().set(
                RedisConstant.CAPTCHA_PREFIX + captchaId,
                code,
                RedisConstant.CAPTCHA_EXPIRE,
                TimeUnit.SECONDS);

        String imageBase64 = generateImage(code);

        Map<String, String> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("image", imageBase64);
        return result;
    }

    public boolean verifyCaptcha(String captchaId, String code) {
        if (captchaId == null || code == null) {
            return false;
        }
        String cachedCode = redisTemplate.opsForValue().get(RedisConstant.CAPTCHA_PREFIX + captchaId);
        if (cachedCode == null) {
            return false;
        }
        boolean matches = cachedCode.equalsIgnoreCase(code);
        if (matches) {
            redisTemplate.delete(RedisConstant.CAPTCHA_PREFIX + captchaId);
        }
        return matches;
    }

    private String generateImage(String code) {
        int width = 120;
        int height = 40;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 5; i++) {
            int x1 = RandomUtil.randomInt(width);
            int y1 = RandomUtil.randomInt(height);
            int x2 = RandomUtil.randomInt(width);
            int y2 = RandomUtil.randomInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.BLUE);
        int x = 20;
        for (char c : code.toCharArray()) {
            g.drawString(String.valueOf(c), x, 28);
            x += 25;
        }

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return null;
        }
    }
}