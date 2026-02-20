package com.qoobot.qooerp.scheduler.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleJob;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务调度引擎
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleEngine {

    private final ScheduleJobMapper scheduleJobMapper;
    private final TaskExecutor taskExecutor;

    private final ConcurrentHashMap<Long, CronExpression> cronCache = new ConcurrentHashMap<>();

    /**
     * 定时扫描待执行任务
     */
    @Scheduled(fixedRate = 5000)
    public void scheduleTasks() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 查询需要执行的任务
            LambdaQueryWrapper<ScheduleJob> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ScheduleJob::getStatus, "RUNNING")
                       .le(ScheduleJob::getNextExecuteTime, now)
                       .eq(ScheduleJob::getDeleted, 0);
            
            List<ScheduleJob> jobs = scheduleJobMapper.selectList(queryWrapper);
            
            for (ScheduleJob job : jobs) {
                try {
                    scheduleJob(job);
                } catch (Exception e) {
                    log.error("调度任务失败: jobId={}, jobName={}", job.getId(), job.getJobName(), e);
                }
            }
        } catch (Exception e) {
            log.error("任务调度异常", e);
        }
    }

    /**
     * 调度单个任务
     */
    private void scheduleJob(ScheduleJob job) {
        // 检查并发策略
        if ("SINGLE".equals(job.getConcurrency())) {
            // 单例模式,检查是否已有执行中的任务
            // 简化实现:暂时允许执行
        }

        // 提交任务到执行器
        taskExecutor.executeTask(job);

        // 计算下次执行时间
        LocalDateTime nextTime = calculateNextExecuteTime(job);
        
        // 更新任务的下次执行时间
        LambdaUpdateWrapper<ScheduleJob> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScheduleJob::getId, job.getId())
                    .set(ScheduleJob::getNextExecuteTime, nextTime)
                    .set(ScheduleJob::getPrevExecuteTime, LocalDateTime.now());
        scheduleJobMapper.update(null, updateWrapper);
    }

    /**
     * 计算下次执行时间
     */
    public LocalDateTime calculateNextExecuteTime(ScheduleJob job) {
        String jobType = job.getJobType();
        LocalDateTime now = LocalDateTime.now();

        try {
            switch (jobType) {
                case "CRON":
                    CronExpression cronExpression = getCronExpression(job.getCronExpression());
                    if (cronExpression != null) {
                        return cronExpression.next(now);
                    }
                    break;
                case "INTERVAL":
                    if (job.getInterval() != null && job.getInterval() > 0) {
                        return now.plusSeconds(job.getInterval());
                    }
                    break;
                case "ONCE":
                    return null; // 一次性任务执行后不再执行
                case "DEPENDENT":
                    return null; // 依赖任务由触发机制决定
            }
        } catch (Exception e) {
            log.error("计算下次执行时间失败: jobId={}", job.getId(), e);
        }

        return null;
    }

    /**
     * 获取Cron表达式(带缓存)
     */
    private CronExpression getCronExpression(String cronExpression) {
        return cronCache.computeIfAbsent((long) cronExpression.hashCode(), k -> {
            try {
                return CronExpression.parse(cronExpression);
            } catch (Exception e) {
                log.error("Cron表达式解析失败: {}", cronExpression, e);
                return null;
            }
        });
    }

    /**
     * 清除Cron缓存
     */
    public void clearCronCache() {
        cronCache.clear();
        log.info("Cron缓存已清除");
    }
}
