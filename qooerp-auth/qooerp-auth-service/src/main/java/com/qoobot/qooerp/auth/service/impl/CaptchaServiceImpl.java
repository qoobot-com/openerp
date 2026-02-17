package com.qoobot.qooerp.auth.service.impl;

import com.qoobot.qooerp.auth.constants.AuthConstant;
import com.qoobot.qooerp.auth.dto.CaptchaResponse;
import com.qoobot.qooerp.auth.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 * 验证码服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${auth.captcha.expire:300}")
    private Long captchaExpire;

    // 字符验证码配置
    private static final int CAPTCHA_WIDTH = 120;
    private static final int CAPTCHA_HEIGHT = 40;
    private static final int CAPTCHA_CODE_LENGTH = 4;

    // 滑块验证码配置
    private static final int SLIDER_WIDTH = 350;
    private static final int SLIDER_HEIGHT = 200;
    private static final int SLIDER_SIZE = 50;
    private static final int SLIDER_TOLERANCE = 5; // 允许误差

    private static final long SMS_CODE_EXPIRE_TIME = 300; // 5分钟
    private static final long EMAIL_CODE_EXPIRE_TIME = 600; // 10分钟

    @Override
    public void generateCaptcha(HttpServletResponse response) throws IOException {
        CaptchaResponse captchaResponse = generateCaptcha(0);

        // 输出到响应
        response.setContentType("image/jpeg");
        response.setHeader("captcha-key", captchaResponse.getKey());
        byte[] imageBytes = Base64.getDecoder().decode(captchaResponse.getImage().split(",")[1]);
        response.getOutputStream().write(imageBytes);
    }

    @Override
    public CaptchaResponse generateCaptcha(Integer type) {
        String key = UUID.randomUUID().toString();

        if (type == null || type == 0) {
            // 字符验证码
            return generateTextCaptcha(key);
        } else {
            // 滑块验证码
            return generateSliderCaptcha(key);
        }
    }

    @Override
    public boolean verifyCaptcha(String key, String code) {
        if (key == null || code == null) {
            return false;
        }

        String cacheKey = AuthConstant.CAPTCHA_KEY + key;
        String cachedCode = (String) redisTemplate.opsForValue().get(cacheKey);

        if (cachedCode == null) {
            return false;
        }

        // 验证后删除验证码
        redisTemplate.delete(cacheKey);

        return cachedCode.equalsIgnoreCase(code);
    }

    @Override
    public boolean verifySliderCaptcha(String key, Integer x, Integer y) {
        if (key == null || x == null || y == null) {
            return false;
        }

        String cacheKey = AuthConstant.CAPTCHA_KEY + key;
        String cachedValue = (String) redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue == null) {
            return false;
        }

        // 验证后删除验证码
        redisTemplate.delete(cacheKey);

        try {
            // 格式：x,y
            String[] parts = cachedValue.split(",");
            int cachedX = Integer.parseInt(parts[0]);
            int cachedY = Integer.parseInt(parts[1]);

            // 验证滑块位置
            return Math.abs(x - cachedX) <= SLIDER_TOLERANCE &&
                   Math.abs(y - cachedY) <= SLIDER_TOLERANCE;
        } catch (Exception e) {
            log.error("验证滑块验证码失败", e);
            return false;
        }
    }

    @Override
    public boolean sendSmsCode(String phone) {
        // TODO: 实现短信发送逻辑
        String code = generateRandomCode(6);
        String cacheKey = AuthConstant.SMS_CODE_KEY + phone;

        redisTemplate.opsForValue().set(
                cacheKey,
                code,
                SMS_CODE_EXPIRE_TIME,
                TimeUnit.SECONDS
        );

        log.info("发送短信验证码到手机号: {}, 验证码: {}", phone, code);
        return true;
    }

    @Override
    public boolean verifySmsCode(String phone, String code) {
        if (phone == null || code == null) {
            return false;
        }

        String cacheKey = AuthConstant.SMS_CODE_KEY + phone;
        String cachedCode = (String) redisTemplate.opsForValue().get(cacheKey);

        if (cachedCode == null) {
            return false;
        }

        // 验证后删除验证码
        redisTemplate.delete(cacheKey);

        return cachedCode.equals(code);
    }

    @Override
    public boolean sendEmailCode(String email) {
        // TODO: 实现邮件发送逻辑
        String code = generateRandomCode(6);
        String cacheKey = AuthConstant.EMAIL_CODE_KEY + email;

        redisTemplate.opsForValue().set(
                cacheKey,
                code,
                EMAIL_CODE_EXPIRE_TIME,
                TimeUnit.SECONDS
        );

        log.info("发送邮件验证码到邮箱: {}, 验证码: {}", email, code);
        return true;
    }

    @Override
    public boolean verifyEmailCode(String email, String code) {
        if (email == null || code == null) {
            return false;
        }

        String cacheKey = AuthConstant.EMAIL_CODE_KEY + email;
        String cachedCode = (String) redisTemplate.opsForValue().get(cacheKey);

        if (cachedCode == null) {
            return false;
        }

        // 验证后删除验证码
        redisTemplate.delete(cacheKey);

        return cachedCode.equals(code);
    }

    /**
     * 生成字符验证码
     */
    private CaptchaResponse generateTextCaptcha(String key) {
        String code = generateRandomCode(CAPTCHA_CODE_LENGTH);

        // 存储到Redis
        redisTemplate.opsForValue().set(
                AuthConstant.CAPTCHA_KEY + key,
                code,
                captchaExpire,
                TimeUnit.SECONDS
        );

        // 生成验证码图片
        BufferedImage image = createCaptchaImage(code);
        String imageBase64 = imageToBase64(image, "JPEG");

        return CaptchaResponse.builder()
                .key(key)
                .type(0)
                .image(imageBase64)
                .build();
    }

    /**
     * 生成滑块验证码
     */
    private CaptchaResponse generateSliderCaptcha(String key) {
        Random random = new Random();

        // 随机滑块位置
        int sliderX = random.nextInt(SLIDER_WIDTH - SLIDER_SIZE - 50) + 25;
        int sliderY = random.nextInt(SLIDER_HEIGHT - SLIDER_SIZE - 50) + 25;

        // 存储到Redis
        redisTemplate.opsForValue().set(
                AuthConstant.CAPTCHA_KEY + key,
                sliderX + "," + sliderY,
                captchaExpire,
                TimeUnit.SECONDS
        );

        // 生成背景图
        BufferedImage backgroundImage = createBackgroundImage(sliderX, sliderY);
        String backgroundBase64 = imageToBase64(backgroundImage, "PNG");

        // 生成滑块图
        BufferedImage sliderImage = createSliderImage(backgroundImage, sliderX, sliderY);
        String sliderBase64 = imageToBase64(sliderImage, "PNG");

        return CaptchaResponse.builder()
                .key(key)
                .type(1)
                .backgroundImage(backgroundBase64)
                .sliderImage(sliderBase64)
                .y(sliderY)
                .build();
    }

    /**
     * 生成随机验证码
     */
    private String generateRandomCode(int length) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    /**
     * 创建字符验证码图片
     */
    private BufferedImage createCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 填充背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 20));

        // 绘制验证码
        Random random = new Random();
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            g.drawString(String.valueOf(code.charAt(i)), 20 * i + 10, 30);
        }

        // 添加干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawLine(random.nextInt(CAPTCHA_WIDTH), random.nextInt(CAPTCHA_HEIGHT),
                    random.nextInt(CAPTCHA_WIDTH), random.nextInt(CAPTCHA_HEIGHT));
        }

        g.dispose();
        return image;
    }

    /**
     * 创建滑块背景图
     */
    private BufferedImage createBackgroundImage(int sliderX, int sliderY) {
        BufferedImage image = new BufferedImage(SLIDER_WIDTH, SLIDER_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 填充渐变背景
        GradientPaint gradient = new GradientPaint(0, 0, new Color(200, 220, 240),
                SLIDER_WIDTH, SLIDER_HEIGHT, new Color(180, 200, 220));
        g.setPaint(gradient);
        g.fillRect(0, 0, SLIDER_WIDTH, SLIDER_HEIGHT);

        // 添加一些干扰元素
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(SLIDER_WIDTH);
            int y = random.nextInt(SLIDER_HEIGHT);
            int size = random.nextInt(30) + 10;
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 100));
            g.fillOval(x, y, size, size);
        }

        // 绘制滑块缺口（镂空）
        g.setColor(new Color(180, 200, 220));
        g.fillRoundRect(sliderX, sliderY, SLIDER_SIZE, SLIDER_SIZE, 10, 10);
        g.setColor(Color.WHITE);
        g.fillOval(sliderX + 10, sliderY - 10, 20, 20);

        g.dispose();
        return image;
    }

    /**
     * 创建滑块图
     */
    private BufferedImage createSliderImage(BufferedImage background, int x, int y) {
        BufferedImage image = new BufferedImage(SLIDER_SIZE + 20, SLIDER_SIZE + 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // 启用抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制滑块形状
        g.setColor(new Color(240, 120, 80));
        g.fillRoundRect(0, 0, SLIDER_SIZE, SLIDER_SIZE, 10, 10);

        // 绘制拼图凹槽
        g.setColor(new Color(255, 255, 255));
        g.fillOval(10, -10, 20, 20);

        // 绘制滑块边框
        g.setColor(new Color(200, 100, 60));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(0, 0, SLIDER_SIZE, SLIDER_SIZE, 10, 10);

        // 添加高光效果
        g.setColor(new Color(255, 255, 255, 100));
        g.fillOval(10, 10, 15, 15);

        g.dispose();
        return image;
    }

    /**
     * 图片转Base64
     */
    private String imageToBase64(BufferedImage image, String format) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/" + format.toLowerCase() + ";base64," +
                    Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("图片转Base64失败", e);
            return null;
        }
    }
}
