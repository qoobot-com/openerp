package com.qoobot.qooerp.scheduler.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleLog;
import com.qoobot.qooerp.scheduler.job.dto.ScheduleLogDTO;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleLogMapper;
import com.qoobot.qooerp.scheduler.log.service.ScheduleLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务日志 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleLogServiceImpl implements ScheduleLogService {

    private final ScheduleLogMapper logMapper;

    @Override
    public void logExecution(Long jobId, String status, String result, Long duration, String exception) {
        log.info("记录执行日志, jobId: {}, status: {}", jobId, status);

        ScheduleLog scheduleLog = new ScheduleLog();
        scheduleLog.setJobId(jobId);
        scheduleLog.setExecuteTime(LocalDateTime.now());
        scheduleLog.setExecuteResult(result);
        scheduleLog.setExecuteDuration(duration);
        scheduleLog.setStatus(status);
        scheduleLog.setExceptionInfo(exception);
        scheduleLog.setTenantId(1L); // TODO: 从上下文获取租户ID

        logMapper.insert(scheduleLog);
    }

    @Override
    public Page<ScheduleLogDTO> queryLogs(Long jobId, LocalDateTime startTime, LocalDateTime endTime, Integer current, Integer size) {
        log.info("查询执行日志, jobId: {}, startTime: {}, endTime: {}", jobId, startTime, endTime);

        Page<ScheduleLog> page = new Page<>(current, size);
        LambdaQueryWrapper<ScheduleLog> wrapper = new LambdaQueryWrapper<>();

        if (jobId != null) {
            wrapper.eq(ScheduleLog::getJobId, jobId);
        }
        if (startTime != null) {
            wrapper.ge(ScheduleLog::getExecuteTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(ScheduleLog::getExecuteTime, endTime);
        }

        wrapper.orderByDesc(ScheduleLog::getExecuteTime);
        Page<ScheduleLog> resultPage = logMapper.selectPage(page, wrapper);

        Page<ScheduleLogDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<ScheduleLogDTO> dtoList = resultPage.getRecords().stream()
                .map(scheduleLog -> {
                    ScheduleLogDTO dto = new ScheduleLogDTO();
                    BeanUtils.copyProperties(scheduleLog, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public List<ScheduleLogDTO> getRecentLogs(Long jobId, Integer limit) {
        log.info("获取最近日志, jobId: {}, limit: {}", jobId, limit);

        LambdaQueryWrapper<ScheduleLog> wrapper = new LambdaQueryWrapper<>();
        if (jobId != null) {
            wrapper.eq(ScheduleLog::getJobId, jobId);
        }
        wrapper.orderByDesc(ScheduleLog::getExecuteTime);
        wrapper.last("LIMIT " + (limit != null ? limit : 10));

        List<ScheduleLog> logs = logMapper.selectList(wrapper);
        return logs.stream()
                .map(scheduleLog -> {
                    ScheduleLogDTO dto = new ScheduleLogDTO();
                    BeanUtils.copyProperties(scheduleLog, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
