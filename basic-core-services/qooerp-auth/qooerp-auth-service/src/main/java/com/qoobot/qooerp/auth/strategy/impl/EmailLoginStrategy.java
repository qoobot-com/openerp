package com.qoobot.qooerp.auth.strategy.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.auth.constants.AuthConstant;
import com.qoobot.qooerp.auth.dto.LoginRequest;
import com.qoobot.qooerp.auth.dto.LoginResponse;
import com.qoobot.qooerp.auth.entity.AuthUser;
import com.qoobot.qooerp.auth.exception.LoginFailedException;
import com.qoobot.qooerp.auth.mapper.AuthUserMapper;
import com.qoobot.qooerp.auth.strategy.LoginStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 邮箱验证码登录策略
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailLoginStrategy implements LoginStrategy {

    private final AuthUserMapper authUserMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean supports(String loginType) {
        return AuthConstant.LOGIN_TYPE_EMAIL.equals(loginType);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        validate(request);

        String email = request.getEmail();
        String code = request.getEmailCode();

        // 验证验证码
        String cacheKey = AuthConstant.EMAIL_CODE_KEY + email;
        String cachedCode = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cachedCode == null || !cachedCode.equals(code)) {
            throw new LoginFailedException("验证码错误或已过期");
        }

        // 查询用户
        LambdaQueryWrapper<AuthUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthUser::getEmail, email);
        queryWrapper.eq(AuthUser::getDeleted, 0);
        AuthUser user = authUserMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new LoginFailedException("邮箱未注册");
        }

        // 检查用户状态
        if (user.getStatus() != AuthConstant.USER_STATUS_ENABLED) {
            throw new LoginFailedException("账户已被禁用");
        }

        // 删除验证码
        redisTemplate.delete(cacheKey);

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }

    @Override
    public void validate(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new LoginFailedException("邮箱不能为空");
        }
        if (request.getEmailCode() == null || request.getEmailCode().trim().isEmpty()) {
            throw new LoginFailedException("验证码不能为空");
        }
    }
}
