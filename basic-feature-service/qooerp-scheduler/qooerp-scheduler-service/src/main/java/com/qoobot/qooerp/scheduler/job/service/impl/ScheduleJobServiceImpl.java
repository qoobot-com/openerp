package com.qoobot.qooerp.scheduler.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.constant.ErrorCodeConstant;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleConfig;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleJob;
import com.qoobot.qooerp.scheduler.job.dto.*;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleConfigMapper;
import com.qoobot.qooerp.scheduler.job.mapper.ScheduleJobMapper;
import com.qoobot.qooerp.scheduler.job.service.ScheduleJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 定时任务 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleJobServiceImpl implements ScheduleJobService {

    private final ScheduleJobMapper jobMapper;
    private final ScheduleConfigMapper configMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleJobDTO createJob(ScheduleJobDTO dto) {
        log.info("创建定时任务, jobName: {}", dto.getJobName());

        ScheduleJob job = new ScheduleJob();
        BeanUtils.copyProperties(dto, job);

        // 生成任务编号
        job.setJobNo(generateJobNo());
        job.setStatus("STOPPED");
        job.setExecuteCount(0L);
        job.setSuccessCount(0L);
        job.setFailCount(0L);

        jobMapper.insert(job);

        // 保存配置
        if (dto.getConfig() != null && !dto.getConfig().isEmpty()) {
            for (Map.Entry<String, String> entry : dto.getConfig().entrySet()) {
                ScheduleConfig config = new ScheduleConfig();
                config.setJobId(job.getId());
                config.setConfigKey(entry.getKey());
                config.setConfigValue(entry.getValue());
                config.setTenantId(job.getTenantId());
                configMapper.insert(config);
            }
        }

        BeanUtils.copyProperties(job, dto);
        dto.setConfig(null); // 不返回配置
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleJobDTO updateJob(Long id, ScheduleJobDTO dto) {
        log.info("更新定时任务, id: {}", id);

        ScheduleJob job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "任务不存在");
        }

        BeanUtils.copyProperties(dto, job);
        jobMapper.updateById(job);

        BeanUtils.copyProperties(job, dto);
        dto.setConfig(null);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long id) {
        log.info("删除定时任务, id: {}", id);

        ScheduleJob job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "任务不存在");
        }

        jobMapper.deleteById(id);

        // 删除配置
        LambdaQueryWrapper<ScheduleConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleConfig::getJobId, id);
        configMapper.delete(wrapper);
    }

    @Override
    public ScheduleJobDTO getJobById(Long id) {
        ScheduleJob job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "任务不存在");
        }

        ScheduleJobDTO dto = new ScheduleJobDTO();
        BeanUtils.copyProperties(job, dto);

        // 查询配置
        LambdaQueryWrapper<ScheduleConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleConfig::getJobId, id);
        List<ScheduleConfig> configs = configMapper.selectList(wrapper);
        Map<String, String> configMap = configs.stream()
                .collect(Collectors.toMap(ScheduleConfig::getConfigKey, ScheduleConfig::getConfigValue));
        dto.setConfig(configMap);

        return dto;
    }

    @Override
    public Page<ScheduleJobDTO> queryJobs(JobQueryDTO queryDTO) {
        Page<ScheduleJob> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<ScheduleJob> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getJobNo() != null && !queryDTO.getJobNo().isEmpty()) {
            wrapper.like(ScheduleJob::getJobNo, queryDTO.getJobNo());
        }
        if (queryDTO.getJobName() != null && !queryDTO.getJobName().isEmpty()) {
            wrapper.like(ScheduleJob::getJobName, queryDTO.getJobName());
        }
        if (queryDTO.getJobType() != null && !queryDTO.getJobType().isEmpty()) {
            wrapper.eq(ScheduleJob::getJobType, queryDTO.getJobType());
        }
        if (queryDTO.getStatus() != null && !queryDTO.getStatus().isEmpty()) {
            wrapper.eq(ScheduleJob::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByDesc(ScheduleJob::getCreateTime);
        Page<ScheduleJob> resultPage = jobMapper.selectPage(page, wrapper);

        Page<ScheduleJobDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<ScheduleJobDTO> dtoList = resultPage.getRecords().stream()
                .map(job -> {
                    ScheduleJobDTO dto = new ScheduleJobDTO();
                    BeanUtils.copyProperties(job, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startJob(Long id) {
        log.info("启动定时任务, id: {}", id);

        ScheduleJob job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "任务不存在");
        }

        job.setStatus("RUNNING");
        jobMapper.updateById(job);

        // TODO: 启动任务调度
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stopJob(Long id) {
        log.info("停止定时任务, id: {}", id);

        ScheduleJob job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "任务不存在");
        }

        job.setStatus("STOPPED");
        jobMapper.updateById(job);

        // TODO: 停止任务调度
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pauseJob(Long id) {
        log.info("暂停定时任务, id: {}", id);

        ScheduleJob job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "任务不存在");
        }

        if (!"RUNNING".equals(job.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "只有运行中的任务才能暂停");
        }

        job.setStatus("PAUSED");
        jobMapper.updateById(job);

        // TODO: 暂停任务调度
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resumeJob(Long id) {
        log.info("恢复定时任务, id: {}", id);

        ScheduleJob job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "任务不存在");
        }

        if (!"PAUSED".equals(job.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "只有暂停的任务才能恢复");
        }

        job.setStatus("RUNNING");
        jobMapper.updateById(job);

        // TODO: 恢复任务调度
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeJob(JobExecuteDTO dto) {
        log.info("手动执行定时任务, jobId: {}", dto.getJobId());

        ScheduleJob job = jobMapper.selectById(dto.getJobId());
        if (job == null) {
            throw new BusinessException(ErrorCodeConstant.NOT_FOUND, "任务不存在");
        }

        if ("DELETED".equals(job.getStatus())) {
            throw new BusinessException(ErrorCodeConstant.BUSINESS_ERROR, "已删除的任务不能执行");
        }

        // TODO: 执行任务逻辑
        // TODO: 记录执行日志
        // TODO: 更新任务统计
    }

    @Override
    public Page<ScheduleLogDTO> getJobLogs(Long jobId, Integer current, Integer size) {
        // TODO: 实现任务日志查询
        return new Page<>(current, size, 0);
    }

    private String generateJobNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000;
        return "JOB" + dateStr + randomNum;
    }
}
