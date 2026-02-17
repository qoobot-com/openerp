package com.qoobot.qooerp.system.listener;

import com.qoobot.qooerp.system.service.SystemJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 应用启动监听器
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupListener {

    private final SystemJobService systemJobService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("初始化定时任务...");
        try {
            systemJobService.initJobs();
        } catch (Exception e) {
            log.error("初始化定时任务失败", e);
        }
    }
}
