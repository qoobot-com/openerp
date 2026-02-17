package com.qoobot.qooerp.auth.strategy.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.auth.constants.AuthConstant;
import com.qoobot.qooerp.auth.dto.LoginRequest;
import com.qoobot.qooerp.auth.dto.LoginResponse;
import com.qoobot.qooerp.auth.entity.AuthUser;
import com.qoobot.qooerp.auth.exception.LoginFailedException;
import com.qoobot.qooerp.auth.exception.UserLockedException;
import com.qoobot.qooerp.auth.mapper.AuthUserMapper;
import com.qoobot.qooerp.auth.strategy.LoginStrategy;
import com.qoobot.qooerp.common.security.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 用户名密码登录策略
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UsernamePasswordLoginStrategy implements LoginStrategy {

    private final AuthUserMapper authUserMapper;

    @Override
    public boolean supports(String loginType) {
        return AuthConstant.LOGIN_TYPE_USERNAME.equals(loginType) || loginType == null;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        validate(request);

        String username = request.getUsername();
        String password = request.getPassword();

        // 查询用户
        LambdaQueryWrapper<AuthUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthUser::getUsername, username);
        queryWrapper.eq(AuthUser::getDeleted, 0);
        AuthUser user = authUserMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new LoginFailedException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != AuthConstant.USER_STATUS_ENABLED) {
            throw new UserLockedException("账户已被禁用");
        }

        // 检查账户锁定
        if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
            long minutes = (user.getLockTime().getSecond() - LocalDateTime.now().getSecond()) / 60;
            throw new UserLockedException("账户已被锁定，请" + minutes + "分钟后重试");
        }

        // 验证密码
        if (!PasswordUtil.matches(password, user.getPassword())) {
            // 更新失败次数
            int failCount = user.getLoginFailCount() + 1;
            user.setLoginFailCount(failCount);
            if (failCount >= 5) {
                user.setLockTime(LocalDateTime.now().plusMinutes(30));
            }
            authUserMapper.updateById(user);
            throw new LoginFailedException("用户名或密码错误");
        }

        // 重置失败次数
        if (user.getLoginFailCount() > 0) {
            user.setLoginFailCount(0);
            user.setLockTime(null);
            authUserMapper.updateById(user);
        }

        // 构建响应（令牌生成在AuthService中完成）
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
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new LoginFailedException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new LoginFailedException("密码不能为空");
        }
    }
}
