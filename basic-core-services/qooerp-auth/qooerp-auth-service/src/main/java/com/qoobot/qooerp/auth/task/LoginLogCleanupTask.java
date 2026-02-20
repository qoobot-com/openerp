package com.qoobot.qooerp.auth.task;

import com.qoobot.qooerp.auth.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 登录日志清理定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginLogCleanupTask {

    private final LoginLogService loginLogService;

    /**
     * 每天凌晨2点清理过期日志（保留90天）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredLogs() {
        log.info("开始清理过期登录日志...");
        try {
            loginLogService.cleanExpiredLogs(90);
            log.info("清理过期登录日志完成");
        } catch (Exception e) {
            log.error("清理过期登录日志失败", e);
        }
    }
}
