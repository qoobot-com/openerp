package com.qoobot.qooerp.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.auth.constants.AuthConstant;
import com.qoobot.qooerp.auth.dto.LoginRequest;
import com.qoobot.qooerp.auth.dto.LoginResponse;
import com.qoobot.qooerp.auth.dto.RefreshTokenRequest;
import com.qoobot.qooerp.auth.dto.TokenResponse;
import com.qoobot.qooerp.auth.entity.AuthUser;
import com.qoobot.qooerp.auth.exception.LoginFailedException;
import com.qoobot.qooerp.auth.exception.UserLockedException;
import com.qoobot.qooerp.auth.mapper.AuthUserMapper;
import com.qoobot.qooerp.auth.service.AuthService;
import com.qoobot.qooerp.auth.service.CaptchaService;
import com.qoobot.qooerp.auth.service.LoginLogService;
import com.qoobot.qooerp.auth.service.TokenService;
import com.qoobot.qooerp.common.security.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserMapper authUserMapper;
    private final TokenService tokenService;
    private final LoginLogService loginLogService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CaptchaService captchaService;

    @Value("${auth.login.max-fail-count:5}")
    private Integer maxFailCount;

    @Value("${auth.login.ip-max-failed:10}")
    private Integer ipMaxFailed;

    @Value("${auth.captcha.enabled:true}")
    private Boolean captchaEnabled;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String ip = request.getIp() != null ? request.getIp() : "127.0.0.1";
        String location = request.getLocation() != null ? request.getLocation() : "未知";
        String browser = request.getBrowser() != null ? request.getBrowser() : "未知";
        String os = request.getOs() != null ? request.getOs() : "未知";

        // 1. 验证图形验证码
        if (!verifyCaptcha(request)) {
            loginLogService.recordFailedLogin(username, ip, "验证码错误");
            throw new LoginFailedException("验证码错误");
        }

        // 2. 查询用户
        LambdaQueryWrapper<AuthUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthUser::getUsername, username);
        queryWrapper.eq(AuthUser::getDeleted, 0);
        AuthUser user = authUserMapper.selectOne(queryWrapper);

        if (user == null) {
            loginLogService.recordFailedLogin(username, ip, "用户不存在");
            throw new LoginFailedException("用户不存在");
        }

        // 3. 检查用户状态
        if (user.getStatus() != AuthConstant.USER_STATUS_ENABLED) {
            loginLogService.recordFailedLogin(username, ip, "账户已被禁用");
            throw new UserLockedException("账户已被禁用");
        }

        // 4. 检查登录失败次数和锁定状态
        checkLoginLock(user, ip);

        // 5. 验证密码
        if (!PasswordUtil.matches(password, user.getPassword())) {
            handleLoginFailure(user, ip, location, browser, os);
            throw new LoginFailedException("密码错误");
        }

        // 6. 检查MFA（待实现）
        // if (user.getMfaEnabled() == 1) {
        //     // 需要MFA验证
        // }

        // 7. 登录成功，重置失败次数
        resetLoginFailure(user);

        // 8. 更新最后登录信息
        user.setLastLoginIp(ip);
        user.setLastLoginTime(LocalDateTime.now());
        authUserMapper.updateById(user);

        // 9. 生成令牌
        TokenResponse tokenResponse = tokenService.generateTokenPair(user);

        // 10. 记录成功日志
        loginLogService.recordSuccessLogin(username, user.getId(), ip, location);

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus())
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .tokenType(tokenResponse.getTokenType())
                .expiresIn(tokenResponse.getExpiresIn())
                .build();
    }

    /**
     * 检查登录锁定状态
     */
    private void checkLoginLock(AuthUser user, String ip) {
        // 检查账户锁定
        if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
            log.warn("账户已被锁定，用户：{}，锁定时间：{}", user.getUsername(), user.getLockTime());
            long minutes = java.time.Duration.between(LocalDateTime.now(), user.getLockTime()).toMinutes();
            throw new UserLockedException("账户已被锁定，请" + minutes + "分钟后重试");
        }

        // 检查IP失败次数
        String ipFailKey = AuthConstant.IP_FAIL_COUNT_KEY + ip;
        Integer ipFailCount = (Integer) redisTemplate.opsForValue().get(ipFailKey);
        if (ipFailCount != null && ipFailCount >= ipMaxFailed) {
            log.warn("IP已被锁定：{}", ip);
            throw new UserLockedException("IP已被临时锁定，请稍后重试");
        }
    }

    /**
     * 处理登录失败
     */
    private void handleLoginFailure(AuthUser user, String ip, String location,
                                    String browser, String os) {
        // 增加用户失败次数
        int newFailCount = user.getLoginFailCount() + 1;
        user.setLoginFailCount(newFailCount);

        // 检查是否需要锁定账户
        if (newFailCount >= maxFailCount) {
            user.setLockTime(LocalDateTime.now().plusMinutes(30));
            log.warn("账户已锁定，用户：{}，失败次数：{}", user.getUsername(), newFailCount);
        }

        authUserMapper.updateById(user);

        // 增加IP失败次数
        String ipFailKey = AuthConstant.IP_FAIL_COUNT_KEY + ip;
        redisTemplate.opsForValue().increment(ipFailKey);
        redisTemplate.expire(ipFailKey, 1, TimeUnit.HOURS);

        // 记录失败日志
        loginLogService.recordFailedLogin(user.getUsername(), ip, "密码错误，失败次数：" + newFailCount);
    }

    /**
     * 重置登录失败次数
     */
    private void resetLoginFailure(AuthUser user) {
        if (user.getLoginFailCount() > 0 || user.getLockTime() != null) {
            user.setLoginFailCount(0);
            user.setLockTime(null);
            authUserMapper.updateById(user);
        }
    }

    /**
     * 验证验证码
     */
    private boolean verifyCaptcha(LoginRequest request) {
        // 如果禁用验证码，直接通过
        if (!captchaEnabled) {
            return true;
        }

        // 如果没有提供验证码Key，直接通过（用于测试或内部调用）
        if (request.getCaptchaKey() == null || request.getCaptchaKey().isEmpty()) {
            return true;
        }

        // 根据验证码类型进行验证
        Integer captchaType = request.getCaptchaType() != null ? request.getCaptchaType() : 0;

        if (captchaType == 1) {
            // 滑块验证码
            if (request.getCaptchaX() == null || request.getCaptchaY() == null) {
                return false;
            }
            return captchaService.verifySliderCaptcha(request.getCaptchaKey(), request.getCaptchaX(), request.getCaptchaY());
        } else {
            // 字符验证码
            if (request.getCaptchaCode() == null || request.getCaptchaCode().isEmpty()) {
                return false;
            }
            return captchaService.verifyCaptcha(request.getCaptchaKey(), request.getCaptchaCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(String token) {
        try {
            Long userId = tokenService.getUserIdFromToken(token);
            if (userId != null) {
                // 将令牌加入黑名单
                tokenService.addToBlacklist(token);

                // 删除刷新令牌
                redisTemplate.delete(AuthConstant.REFRESH_TOKEN_KEY + userId);

                log.info("用户登出成功，userId: {}", userId);
            }
        } catch (Exception e) {
            log.error("登出失败", e);
            throw new LoginFailedException("登出失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // 验证刷新令牌
            if (!tokenService.validateToken(refreshToken)) {
                throw new LoginFailedException("刷新令牌无效或已过期");
            }

            Long userId = tokenService.getUserIdFromToken(refreshToken);
            String username = tokenService.getUsernameFromToken(refreshToken);

            // 检查刷新令牌是否存在
            String storedToken = (String) redisTemplate.opsForValue().get(AuthConstant.REFRESH_TOKEN_KEY + userId);
            if (!refreshToken.equals(storedToken)) {
                throw new LoginFailedException("刷新令牌已失效");
            }

            // 检查用户状态
            AuthUser user = authUserMapper.selectById(userId);
            if (user == null || user.getDeleted() == 1 || user.getStatus() != AuthConstant.USER_STATUS_ENABLED) {
                throw new LoginFailedException("用户不存在或已被禁用");
            }

            // 生成新令牌
            TokenResponse tokenResponse = tokenService.generateTokenPair(user);

            return tokenResponse;
        } catch (LoginFailedException e) {
            throw e;
        } catch (Exception e) {
            log.error("刷新令牌失败", e);
            throw new LoginFailedException("刷新令牌失败");
        }
    }

    @Override
    public boolean validateToken(String token) {
        return tokenService.validateToken(token);
    }

    @Override
    public LoginResponse getCurrentUser(String token) {
        try {
            Long userId = tokenService.getUserIdFromToken(token);

            AuthUser user = authUserMapper.selectById(userId);
            if (user == null || user.getDeleted() == 1) {
                throw new LoginFailedException("用户不存在");
            }

            return LoginResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .realName(user.getRealName())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .phone(user.getPhone())
                    .email(user.getEmail())
                    .status(user.getStatus())
                    .accessToken(token)
                    .build();
        } catch (LoginFailedException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            throw new LoginFailedException("获取用户信息失败");
        }
    }
}
