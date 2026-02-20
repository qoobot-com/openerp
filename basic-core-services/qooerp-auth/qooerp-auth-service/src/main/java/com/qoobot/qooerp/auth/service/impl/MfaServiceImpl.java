package com.qoobot.qooerp.auth.service.impl;

import com.qoobot.qooerp.auth.constants.AuthConstant;
import com.qoobot.qooerp.auth.dto.MfaSetupResponse;
import com.qoobot.qooerp.auth.entity.AuthUser;
import com.qoobot.qooerp.auth.exception.LoginFailedException;
import com.qoobot.qooerp.auth.mapper.AuthUserMapper;
import com.qoobot.qooerp.auth.service.CaptchaService;
import com.qoobot.qooerp.auth.service.MfaService;
import com.qoobot.qooerp.common.security.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 多因素认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {

    private final AuthUserMapper authUserMapper;
    private final CaptchaService captchaService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${mfa.issuer:QooERP}")
    private String issuer;

    private static final int SECRET_SIZE = 20;
    private static final int CODE_DIGITS = 6;
    private static final int TIME_STEP_SECONDS = 30;
    private static final int WINDOW_SIZE = 3;

    @Override
    public MfaSetupResponse generateTotpSecret(Long userId, String username) {
        // 生成随机密钥
        String secret = generateSecret();

        // 生成备用码
        List<String> backupCodes = generateBackupCodes(10);

        // 生成Google Authenticator二维码URL
        String qrCodeUrl = String.format(
                "otpauth://totp/%s:%s@%s?secret=%s&issuer=%s&digits=%d",
                issuer, username, issuer, secret, issuer, CODE_DIGITS
        );

        // 临时存储（用于确认启用）
        redisTemplate.opsForValue().set(
                AuthConstant.MFA_TEMP_KEY + userId,
                secret + ":" + String.join(",", backupCodes),
                10,
                TimeUnit.MINUTES
        );

        return MfaSetupResponse.builder()
                .secret(secret)
                .qrCodeUrl(qrCodeUrl)
                .backupCodes(backupCodes)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableTotpMfa(Long userId, String secret, String code) {
        // 验证验证码
        if (!verifyTotpSecret(secret, code)) {
            return false;
        }

        // 获取临时存储的密钥
        String tempKey = (String) redisTemplate.opsForValue().get(AuthConstant.MFA_TEMP_KEY + userId);
        if (tempKey == null || !tempKey.startsWith(secret)) {
            throw new LoginFailedException("MFA密钥已过期或无效");
        }

        // 更新用户信息
        AuthUser user = authUserMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        user.setMfaEnabled(AuthConstant.MFA_ENABLED);
        user.setMfaSecretKey(secret);
        authUserMapper.updateById(user);

        // 删除临时密钥
        redisTemplate.delete(AuthConstant.MFA_TEMP_KEY + userId);

        log.info("用户启用MFA成功，userId: {}", userId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableMfa(Long userId, String password) {
        // 验证密码
        AuthUser user = authUserMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new LoginFailedException("密码错误");
        }

        // 禁用MFA
        user.setMfaEnabled(AuthConstant.MFA_DISABLED);
        user.setMfaSecretKey(null);
        authUserMapper.updateById(user);

        log.info("用户禁用MFA成功，userId: {}", userId);
        return true;
    }

    @Override
    public boolean verifyTotpCode(Long userId, String code) {
        AuthUser user = authUserMapper.selectById(userId);
        if (user == null || user.getMfaSecretKey() == null) {
            return false;
        }

        return verifyTotpSecret(user.getMfaSecretKey(), code);
    }

    @Override
    public boolean verifySmsMfaCode(Long userId, String code) {
        String key = AuthConstant.MFA_SMS_CODE_KEY + userId;
        String storedCode = (String) redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return false;
        }

        if (storedCode.equals(code)) {
            redisTemplate.delete(key);
            return true;
        }

        return false;
    }

    @Override
    public boolean verifyEmailMfaCode(Long userId, String code) {
        String key = AuthConstant.MFA_EMAIL_CODE_KEY + userId;
        String storedCode = (String) redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return false;
        }

        if (storedCode.equals(code)) {
            redisTemplate.delete(key);
            return true;
        }

        return false;
    }

    @Override
    public boolean isMfaEnabled(Long userId) {
        AuthUser user = authUserMapper.selectById(userId);
        return user != null && user.getMfaEnabled() == AuthConstant.MFA_ENABLED;
    }

    /**
     * 生成TOTP密钥
     */
    private String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SECRET_SIZE];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    /**
     * 生成备用码
     */
    private List<String> generateBackupCodes(int count) {
        List<String> codes = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        Base32 base32 = new Base32();

        for (int i = 0; i < count; i++) {
            byte[] bytes = new byte[SECRET_SIZE];
            random.nextBytes(bytes);
            codes.add(base32.encodeToString(bytes).substring(0, 8).toUpperCase());
        }

        return codes;
    }

    /**
     * 验证TOTP验证码
     */
    private boolean verifyTotpSecret(String secret, String code) {
        try {
            Base32 base32 = new Base32();
            byte[] keyBytes = base32.decode(secret);

            long currentTime = System.currentTimeMillis() / 1000;
            long timeStep = currentTime / TIME_STEP_SECONDS;

            // 检查当前时间窗口及前后WINDOW_SIZE个窗口
            for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; i++) {
                String generatedCode = generateTotp(keyBytes, timeStep + i);
                if (generatedCode.equals(code)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            log.error("TOTP验证失败", e);
            return false;
        }
    }

    /**
     * 生成TOTP验证码
     */
    private String generateTotp(byte[] key, long timeStep) {
        try {
            byte[] data = new byte[8];
            long value = timeStep;
            for (int i = 7; i >= 0; i--) {
                data[i] = (byte) (value & 0xff);
                value >>= 8;
            }

            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(key, "HmacSHA1");
            mac.init(spec);

            byte[] hash = mac.doFinal(data);
            int offset = hash[hash.length - 1] & 0xf;
            int binary = ((hash[offset] & 0x7f) << 24)
                    | ((hash[offset + 1] & 0xff) << 16)
                    | ((hash[offset + 2] & 0xff) << 8)
                    | (hash[offset + 3] & 0xff);

            int otp = binary % 1000000;

            return String.format("%0" + CODE_DIGITS + "d", otp);
        } catch (Exception e) {
            log.error("生成TOTP验证码失败", e);
            return "";
        }
    }
}
