package com.qoobot.qooerp.scheduler.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleJob;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleLog;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleJobMapper;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 依赖管理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DependencyManager {

    private final ScheduleJobMapper scheduleJobMapper;
    private final ScheduleLogMapper scheduleLogMapper;
    private final TaskExecutor taskExecutor;

    /**
     * 检查依赖任务是否完成
     */
    public boolean checkDependencyCompleted(Long dependentJobId) {
        if (dependentJobId == null) {
            return true;
        }

        ScheduleJob dependentJob = scheduleJobMapper.selectById(dependentJobId);
        if (dependentJob == null) {
            log.error("依赖任务不存在: dependentJobId={}", dependentJobId);
            return false;
        }

        // 检查依赖任务状态
        if (!"RUNNING".equals(dependentJob.getStatus())) {
            log.warn("依赖任务未运行: dependentJobId={}, status={}", 
                    dependentJobId, dependentJob.getStatus());
            return false;
        }

        // 检查依赖任务最近一次执行是否成功
        LambdaQueryWrapper<ScheduleLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(ScheduleLog::getJobId, dependentJobId)
                  .orderByDesc(ScheduleLog::getExecuteTime)
                  .last("LIMIT 1");
        
        ScheduleLog lastLog = scheduleLogMapper.selectOne(logWrapper);
        
        if (lastLog == null) {
            log.info("依赖任务尚未执行: dependentJobId={}", dependentJobId);
            return false;
        }

        boolean completed = "SUCCESS".equals(lastLog.getStatus());
        log.info("依赖任务完成状态检查: dependentJobId={}, completed={}", 
                dependentJobId, completed);

        return completed;
    }

    /**
     * 依赖任务完成后的回调
     */
    public void onDependencyCompleted(Long dependentJobId) {
        // 查找所有依赖此任务的任务
        List<ScheduleJob> dependentTasks = findDependentTasks(dependentJobId);
        
        log.info("找到依赖任务: dependentJobId={}, count={}", 
                dependentJobId, dependentTasks.size());

        for (ScheduleJob task : dependentTasks) {
            try {
                // 检查是否所有依赖都已完成
                if (checkAllDependenciesCompleted(task)) {
                    log.info("执行依赖任务: taskId={}, taskName={}", 
                            task.getId(), task.getJobName());
                    taskExecutor.executeTask(task);
                }
            } catch (Exception e) {
                log.error("执行依赖任务失败: taskId={}", task.getId(), e);
            }
        }
    }

    /**
     * 查找依赖指定任务的所有任务
     */
    private List<ScheduleJob> findDependentTasks(Long dependentJobId) {
        LambdaQueryWrapper<ScheduleJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScheduleJob::getDependentJobId, dependentJobId)
                   .eq(ScheduleJob::getStatus, "RUNNING")
                   .eq(ScheduleJob::getJobType, "DEPENDENT")
                   .eq(ScheduleJob::getDeleted, 0);
        
        return scheduleJobMapper.selectList(queryWrapper);
    }

    /**
     * 检查任务的所有依赖是否都已完成
     */
    private boolean checkAllDependenciesCompleted(ScheduleJob task) {
        // 当前实现只支持单个依赖任务
        // 如果需要支持多个依赖,需要修改数据结构
        return checkDependencyCompleted(task.getDependentJobId());
    }
}
