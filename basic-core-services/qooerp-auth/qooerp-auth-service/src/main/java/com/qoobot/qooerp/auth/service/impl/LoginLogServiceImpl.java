package com.qoobot.qooerp.auth.service.impl;

import com.qoobot.qooerp.auth.entity.AuthLoginLog;
import com.qoobot.qooerp.auth.mapper.AuthLoginLogMapper;
import com.qoobot.qooerp.auth.service.LoginLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 登录日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final AuthLoginLogMapper authLoginLogMapper;

    @Async
    @Override
    public void recordLoginLog(String username, Long userId, String ip, String location,
                               String browser, String os, String device, Integer status, String message) {
        try {
            AuthLoginLog loginLog = new AuthLoginLog();
            loginLog.setUsername(username);
            loginLog.setUserId(userId);
            loginLog.setLoginIp(ip);
            loginLog.setLoginLocation(location);
            loginLog.setBrowser(browser);
            loginLog.setOs(os);
            loginLog.setDevice(device);
            loginLog.setStatus(status);
            loginLog.setMessage(message);
            loginLog.setLoginTime(LocalDateTime.now());
            authLoginLogMapper.insert(loginLog);
            log.debug("登录日志记录成功，用户：{}，状态：{}", username, status);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }

    @Override
    public void recordSuccessLogin(String username, Long userId, String ip, String location) {
        recordLoginLog(username, userId, ip, location, null, null, null, 1, "登录成功");
    }

    @Override
    public void recordFailedLogin(String username, String ip, String message) {
        recordLoginLog(username, null, ip, null, null, null, null, 0, message);
    }

    @Override
    public void cleanExpiredLogs(int days) {
        try {
            LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
            LambdaQueryWrapper<AuthLoginLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.lt(AuthLoginLog::getLoginTime, expireTime);
            int count = authLoginLogMapper.delete(queryWrapper);
            log.info("清理过期登录日志完成，清理数量：{}", count);
        } catch (Exception e) {
            log.error("清理过期登录日志失败", e);
        }
    }
}
