package com.qoobot.qooerp.scheduler.engine;

import com.qoobot.qooerp.scheduler.job.domain.ScheduleJob;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 重试机制
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RetryMechanism {

    private final ScheduleJobMapper scheduleJobMapper;
    private final TaskExecutor taskExecutor;
    private final ThreadPoolTaskExecutor retryExecutor;

    /**
     * 重试任务
     */
    public void retry(ScheduleJob job) {
        String retryStrategy = job.getRetryStrategy();
        Integer retryCount = job.getRetryCount();

        if (retryCount == null || retryCount <= 0) {
            log.info("任务不启用重试: jobId={}", job.getId());
            return;
        }

        // 获取当前重试次数
        int currentRetry = getCurrentRetryCount(job.getId());
        
        if (currentRetry >= retryCount) {
            log.warn("任务已达到最大重试次数: jobId={}, retryCount={}", job.getId(), retryCount);
            return;
        }

        // 计算重试延迟
        long delay = calculateRetryDelay(retryStrategy, currentRetry);

        log.info("任务将在{}ms后重试: jobId={}, currentRetry={}", delay, job.getId(), currentRetry);

        // 异步重试
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(delay);
                taskExecutor.executeTask(job);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("任务重试被中断: jobId={}", job.getId());
            }
        }, retryExecutor);
    }

    /**
     * 计算重试延迟
     */
    private long calculateRetryDelay(String retryStrategy, int currentRetry) {
        long baseDelay = 5000; // 基础延迟5秒

        switch (retryStrategy) {
            case "FIXED":
                // 固定延迟
                return baseDelay;
            case "EXPONENTIAL":
                // 指数退避: baseDelay * 2^currentRetry
                return baseDelay * (long) Math.pow(2, currentRetry);
            case "LINEAR":
                // 线性增加: baseDelay * (currentRetry + 1)
                return baseDelay * (currentRetry + 1);
            case "NONE":
            default:
                return 0;
        }
    }

    /**
     * 获取当前重试次数(简化实现,实际应该从日志表统计)
     */
    private int getCurrentRetryCount(Long jobId) {
        // 简化实现:使用任务表的失败次数
        ScheduleJob job = scheduleJobMapper.selectById(jobId);
        if (job != null && job.getFailCount() != null) {
            return job.getFailCount().intValue();
        }
        return 0;
    }
}
