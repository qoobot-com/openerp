package com.qoobot.qooerp.scheduler.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleJob;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleLog;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleJobMapper;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleLogMapper;
import com.qoobot.qooerp.scheduler.log.dto.JobStatisticsDTO;
import com.qoobot.qooerp.scheduler.log.dto.MonitorStatisticsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl {

    private final ScheduleJobMapper scheduleJobMapper;
    private final ScheduleLogMapper scheduleLogMapper;

    /**
     * 获取任务统计
     */
    public JobStatisticsDTO getJobStatistics(Long jobId) {
        ScheduleJob job = scheduleJobMapper.selectById(jobId);
        if (job == null) {
            return null;
        }

        JobStatisticsDTO statistics = new JobStatisticsDTO();
        statistics.setJobId(jobId);
        statistics.setJobName(job.getJobName());
        statistics.setJobType(job.getJobType());
        statistics.setStatus(job.getStatus());
        statistics.setExecuteCount(job.getExecuteCount());
        statistics.setSuccessCount(job.getSuccessCount());
        statistics.setFailCount(job.getFailCount());
        statistics.setNextExecuteTime(job.getNextExecuteTime());

        // 计算成功率
        if (job.getExecuteCount() != null && job.getExecuteCount() > 0) {
            double successRate = (double) job.getSuccessCount() / job.getExecuteCount() * 100;
            statistics.setSuccessRate(String.format("%.2f%%", successRate));
        } else {
            statistics.setSuccessRate("0.00%");
        }

        // 计算平均执行时长
        statistics.setAvgExecuteDuration(calculateAvgDuration(jobId));

        // 获取最近一次执行日志
        ScheduleLog lastLog = getLastLog(jobId);
        if (lastLog != null) {
            statistics.setLastExecuteTime(lastLog.getExecuteTime());
            statistics.setLastStatus(lastLog.getStatus());
        }

        return statistics;
    }

    /**
     * 获取监控统计
     */
    public MonitorStatisticsDTO getMonitorStatistics() {
        MonitorStatisticsDTO statistics = new MonitorStatisticsDTO();

        // 统计任务总数
        LambdaQueryWrapper<ScheduleJob> jobWrapper = new LambdaQueryWrapper<>();
        jobWrapper.eq(ScheduleJob::getDeleted, 0);
        List<ScheduleJob> jobs = scheduleJobMapper.selectList(jobWrapper);
        
        long totalJobs = jobs.size();
        long runningJobs = jobs.stream().filter(j -> "RUNNING".equals(j.getStatus())).count();
        long pausedJobs = jobs.stream().filter(j -> "PAUSED".equals(j.getStatus())).count();
        long stoppedJobs = jobs.stream().filter(j -> "STOPPED".equals(j.getStatus())).count();

        statistics.setTotalJobs(totalJobs);
        statistics.setRunningJobs(runningJobs);
        statistics.setPausedJobs(pausedJobs);
        statistics.setStoppedJobs(stoppedJobs);

        // 统计执行次数
        long totalExecutions = jobs.stream()
                .mapToLong(j -> j.getExecuteCount() != null ? j.getExecuteCount() : 0)
                .sum();
        long totalSuccess = jobs.stream()
                .mapToLong(j -> j.getSuccessCount() != null ? j.getSuccessCount() : 0)
                .sum();
        long totalFailures = jobs.stream()
                .mapToLong(j -> j.getFailCount() != null ? j.getFailCount() : 0)
                .sum();

        statistics.setTotalExecutions(totalExecutions);
        statistics.setTotalSuccess(totalSuccess);
        statistics.setTotalFailures(totalFailures);

        // 计算整体成功率
        if (totalExecutions > 0) {
            double successRate = (double) totalSuccess / totalExecutions * 100;
            statistics.setOverallSuccessRate(String.format("%.2f%%", successRate));
        } else {
            statistics.setOverallSuccessRate("0.00%");
        }

        // 统计今日执行次数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LambdaQueryWrapper<ScheduleLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.ge(ScheduleLog::getExecuteTime, todayStart);
        long todayExecutions = scheduleLogMapper.selectCount(logWrapper);
        statistics.setTodayExecutions(todayExecutions);

        return statistics;
    }

    /**
     * 计算平均执行时长
     */
    private Long calculateAvgDuration(Long jobId) {
        LambdaQueryWrapper<ScheduleLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleLog::getJobId, jobId)
               .isNotNull(ScheduleLog::getExecuteDuration);
        
        List<ScheduleLog> logs = scheduleLogMapper.selectList(wrapper);
        if (logs.isEmpty()) {
            return 0L;
        }

        long total = logs.stream()
                .mapToLong(ScheduleLog::getExecuteDuration)
                .sum();
        
        return total / logs.size();
    }

    /**
     * 获取最近一次执行日志
     */
    private ScheduleLog getLastLog(Long jobId) {
        LambdaQueryWrapper<ScheduleLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleLog::getJobId, jobId)
               .orderByDesc(ScheduleLog::getExecuteTime)
               .last("LIMIT 1");
        return scheduleLogMapper.selectOne(wrapper);
    }
}
