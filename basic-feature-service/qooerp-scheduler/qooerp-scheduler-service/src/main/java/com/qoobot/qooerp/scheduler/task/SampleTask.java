package com.qoobot.qooerp.scheduler.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 示例任务类
 */
@Slf4j
@Component
public class SampleTask {

    /**
     * 示例任务方法
     */
    public void execute() {
        log.info("执行示例任务: 时间={}", LocalDateTime.now());
        
        // 模拟任务执行
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("任务执行被中断");
        }
        
        log.info("示例任务执行完成: 时间={}", LocalDateTime.now());
    }
}
