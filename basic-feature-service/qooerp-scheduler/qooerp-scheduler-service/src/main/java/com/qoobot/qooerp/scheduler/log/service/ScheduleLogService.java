package com.qoobot.qooerp.scheduler.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.scheduler.job.dto.ScheduleLogDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务日志 Service 接口
 */
public interface ScheduleLogService {

    /**
     * 记录执行日志
     */
    void logExecution(Long jobId, String status, String result, Long duration, String exception);

    /**
     * 查询执行日志
     */
    Page<ScheduleLogDTO> queryLogs(Long jobId, LocalDateTime startTime, LocalDateTime endTime, Integer current, Integer size);

    /**
     * 获取最近日志
     */
    List<ScheduleLogDTO> getRecentLogs(Long jobId, Integer limit);
}
