package com.qoobot.qooerp.scheduler.engine;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleJob;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleLog;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleNotify;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleJobMapper;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleLogMapper;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleNotifyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 任务执行器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskExecutor {

    private final ScheduleJobMapper scheduleJobMapper;
    private final ScheduleLogMapper scheduleLogMapper;
    private final ScheduleNotifyMapper scheduleNotifyMapper;
    private final RetryMechanism retryMechanism;
    private final ThreadPoolTaskExecutor taskExecutor;

    /**
     * 执行任务
     */
    public void executeTask(ScheduleJob job) {
        CompletableFuture.runAsync(() -> {
            LocalDateTime startTime = LocalDateTime.now();
            String status = "SUCCESS";
            String result = "执行成功";
            String exception = null;
            Long duration = 0L;

            try {
                // 更新任务统计
                updateJobStats(job.getId(), 1, 0, 0);

                // 执行任务逻辑
                if (job.getTimeout() != null && job.getTimeout() > 0) {
                    // 带超时控制的执行
                    executeWithTimeout(job, job.getTimeout());
                } else {
                    // 无超时控制
                    executeInternal(job);
                }

                duration = Duration.between(startTime, LocalDateTime.now()).toMillis();
                log.info("任务执行成功: jobId={}, jobName={}, duration={}ms", 
                        job.getId(), job.getJobName(), duration);

            } catch (TimeoutException e) {
                status = "TIMEOUT";
                result = "执行超时";
                exception = e.getMessage();
                duration = Duration.between(startTime, LocalDateTime.now()).toMillis();
                
                // 更新任务统计
                updateJobStats(job.getId(), 0, 0, 1);
                
                // 发送超时报警
                sendNotify(job, "TIMEOUT", "HIGH", "任务执行超时");
                
                log.warn("任务执行超时: jobId={}, jobName={}", job.getId(), job.getJobName());

            } catch (Exception e) {
                status = "FAILED";
                result = "执行失败";
                exception = e.getMessage();
                duration = Duration.between(startTime, LocalDateTime.now()).toMillis();
                
                // 更新任务统计
                updateJobStats(job.getId(), 0, 1, 0);
                
                // 发送失败报警
                sendNotify(job, "FAILURE", "HIGH", "任务执行失败: " + e.getMessage());
                
                // 执行重试
                retryMechanism.retry(job);
                
                log.error("任务执行失败: jobId={}, jobName={}", job.getId(), job.getJobName(), e);
            }

            // 记录执行日志
            ScheduleLog logEntity = new ScheduleLog();
            logEntity.setJobId(job.getId());
            logEntity.setExecuteTime(startTime);
            logEntity.setExecuteResult(result);
            logEntity.setExecuteDuration(duration);
            logEntity.setStatus(status);
            logEntity.setExceptionInfo(exception);
            logEntity.setCreateTime(LocalDateTime.now());
            scheduleLogMapper.insert(logEntity);

        }, taskExecutor);
    }

    /**
     * 带超时控制的任务执行
     */
    private void executeWithTimeout(ScheduleJob job, long timeout) throws Exception {
        Future<?> future = CompletableFuture.runAsync(() -> {
            try {
                executeInternal(job);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        try {
            future.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new TimeoutException("任务执行超时: " + timeout + "秒");
        }
    }

    /**
     * 内部任务执行逻辑
     */
    private void executeInternal(ScheduleJob job) throws Exception {
        try {
            // 加载任务类
            Class<?> clazz = Class.forName(job.getJobClass());
            Object instance = clazz.getDeclaredConstructor().newInstance();
            
            // 调用execute方法
            Method method = clazz.getMethod("execute");
            method.invoke(instance);
            
        } catch (ClassNotFoundException e) {
            throw new Exception("任务类不存在: " + job.getJobClass(), e);
        } catch (NoSuchMethodException e) {
            throw new Exception("任务类缺少execute方法: " + job.getJobClass(), e);
        } catch (Exception e) {
            throw new Exception("任务执行异常", e);
        }
    }

    /**
     * 更新任务统计
     */
    private void updateJobStats(Long jobId, long executeDelta, long successDelta, long failDelta) {
        LambdaUpdateWrapper<ScheduleJob> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScheduleJob::getId, jobId);
        
        if (executeDelta > 0) {
            updateWrapper.setSql("execute_count = execute_count + " + executeDelta);
        }
        if (successDelta > 0) {
            updateWrapper.setSql("success_count = success_count + " + successDelta);
        }
        if (failDelta > 0) {
            updateWrapper.setSql("fail_count = fail_count + " + failDelta);
        }
        
        scheduleJobMapper.update(null, updateWrapper);
    }

    /**
     * 发送报警通知
     */
    private void sendNotify(ScheduleJob job, String notifyType, String notifyLevel, String notifyContent) {
        try {
            ScheduleNotify notify = new ScheduleNotify();
            notify.setJobId(job.getId());
            notify.setNotifyType(notifyType);
            notify.setNotifyLevel(notifyLevel);
            notify.setNotifyContent(notifyContent);
            notify.setNotifyTime(LocalDateTime.now());
            notify.setStatus("PENDING");
            notify.setCreateTime(LocalDateTime.now());
            scheduleNotifyMapper.insert(notify);
        } catch (Exception e) {
            log.error("发送报警通知失败: jobId={}", job.getId(), e);
        }
    }
}
