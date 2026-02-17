package com.qoobot.qooerp.monitor.scheduler;

import com.qoobot.qooerp.monitor.service.AlertEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 告警引擎定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEngineScheduler {

    private final AlertEngineService alertEngineService;

    /**
     * 定时检查告警规则
     * 默认每 60 秒检查一次
     */
    @Scheduled(fixedRateString = "${alert.engine.interval:60000}")
    public void scheduledCheckAllRules() {
        try {
            log.debug("开始定时检查告警规则");
            alertEngineService.checkAllRules();
        } catch (Exception e) {
            log.error("定时检查告警规则失败", e);
        }
    }
}
