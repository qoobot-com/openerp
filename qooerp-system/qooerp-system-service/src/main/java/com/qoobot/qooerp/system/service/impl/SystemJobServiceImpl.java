package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.system.dto.SystemJobDTO;
import com.qoobot.qooerp.system.entity.SystemJob;
import com.qoobot.qooerp.system.job.InvokeMethodJob;
import com.qoobot.qooerp.system.mapper.SystemJobMapper;
import com.qoobot.qooerp.system.service.SystemJobService;
import com.qoobot.qooerp.system.vo.SystemJobVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定时任务服务实现
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemJobServiceImpl extends ServiceImpl<SystemJobMapper, SystemJob> implements SystemJobService {

    private final SchedulerFactoryBean schedulerFactoryBean;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createJob(SystemJobDTO dto) {
        // 验证Cron表达式
        if (!CronExpression.isValidExpression(dto.getCronExpression())) {
            throw new BusinessException("Cron表达式无效");
        }

        SystemJob job = new SystemJob();
        BeanUtils.copyProperties(dto, job);
        job.setStatus(dto.getStatus() == null ? 0 : dto.getStatus());
        job.setConcurrent(dto.getConcurrent() == null ? 1 : dto.getConcurrent());
        job.setMisfirePolicy(dto.getMisfirePolicy() == null ? 1 : dto.getMisfirePolicy());
        
        baseMapper.insert(job);

        // 如果状态为启用，立即调度
        if (job.getStatus() == 1) {
            scheduleJob(job);
        }

        return job.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJob(Long id, SystemJobDTO dto) {
        SystemJob job = baseMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }

        // 验证Cron表达式
        if (dto.getCronExpression() != null && !CronExpression.isValidExpression(dto.getCronExpression())) {
            throw new BusinessException("Cron表达式无效");
        }

        BeanUtils.copyProperties(dto, job);
        boolean result = baseMapper.updateById(job) > 0;

        // 重新调度任务
        if (job.getStatus() == 1) {
            rescheduleJob(job);
        } else {
            pauseJob(job);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJob(Long id) {
        SystemJob job = baseMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }

        // 删除调度
        deleteScheduleJob(job);

        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public SystemJobVO getJob(Long id) {
        SystemJob job = baseMapper.selectById(id);
        if (job == null) {
            return null;
        }
        SystemJobVO vo = new SystemJobVO();
        BeanUtils.copyProperties(job, vo);
        return vo;
    }

    @Override
    public Page<SystemJobVO> pageJob(Long current, Long size, String jobName, String jobGroup, Integer status) {
        Page<SystemJob> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemJob> wrapper = new LambdaQueryWrapper<>();

        if (jobName != null && !jobName.isEmpty()) {
            wrapper.like(SystemJob::getJobName, jobName);
        }
        if (jobGroup != null && !jobGroup.isEmpty()) {
            wrapper.eq(SystemJob::getJobGroup, jobGroup);
        }
        if (status != null) {
            wrapper.eq(SystemJob::getStatus, status);
        }

        wrapper.orderByDesc(SystemJob::getCreateTime);

        Page<SystemJob> resultPage = baseMapper.selectPage(page, wrapper);

        Page<SystemJobVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<SystemJobVO> voList = resultPage.getRecords().stream().map(job -> {
            SystemJobVO vo = new SystemJobVO();
            BeanUtils.copyProperties(job, vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startJob(Long id) {
        SystemJob job = baseMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }

        job.setStatus(1);
        baseMapper.updateById(job);

        scheduleJob(job);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stopJob(Long id) {
        SystemJob job = baseMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }

        job.setStatus(0);
        baseMapper.updateById(job);

        pauseJob(job);
        return true;
    }

    @Override
    public Object executeJob(Long id) {
        SystemJob job = baseMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("jobId", id);
        result.put("jobName", job.getJobName());

        try {
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.triggerJob(jobKey);

            result.put("status", "success");
            result.put("message", "任务已触发");
        } catch (SchedulerException e) {
            log.error("执行任务失败", e);
            result.put("status", "fail");
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    public void initJobs() {
        LambdaQueryWrapper<SystemJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemJob::getStatus, 1);
        List<SystemJob> jobs = baseMapper.selectList(wrapper);

        for (SystemJob job : jobs) {
            try {
                scheduleJob(job);
            } catch (Exception e) {
                log.error("初始化任务失败: {}", job.getJobName(), e);
            }
        }

        log.info("定时任务初始化完成，共初始化 {} 个任务", jobs.size());
    }

    /**
     * 调度任务
     */
    private void scheduleJob(SystemJob job) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob(InvokeMethodJob.class)
                    .withIdentity(job.getJobName(), job.getJobGroup())
                    .setJobData(createJobDataMap(job))
                    .storeDurably()
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(job.getJobName(), job.getJobGroup())
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                    .build();

            if (scheduler.checkExists(JobKey.jobKey(job.getJobName(), job.getJobGroup()))) {
                scheduler.deleteJob(JobKey.jobKey(job.getJobName(), job.getJobGroup()));
            }

            scheduler.scheduleJob(jobDetail, trigger);

            if (!scheduler.isStarted()) {
                scheduler.start();
            }

            log.info("任务调度成功: {}", job.getJobName());
        } catch (SchedulerException e) {
            throw new BusinessException("任务调度失败: " + e.getMessage());
        }
    }

    /**
     * 重新调度任务
     */
    private void rescheduleJob(SystemJob job) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                    .build();

            scheduler.rescheduleJob(triggerKey, trigger);

            log.info("任务重新调度成功: {}", job.getJobName());
        } catch (SchedulerException e) {
            throw new BusinessException("任务重新调度失败: " + e.getMessage());
        }
    }

    /**
     * 暂停任务
     */
    private void pauseJob(SystemJob job) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            if (scheduler.checkExists(jobKey)) {
                scheduler.pauseJob(jobKey);
                log.info("任务暂停成功: {}", job.getJobName());
            }
        } catch (SchedulerException e) {
            log.error("暂停任务失败: {}", job.getJobName(), e);
        }
    }

    /**
     * 删除任务调度
     */
    private void deleteScheduleJob(SystemJob job) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                log.info("任务删除成功: {}", job.getJobName());
            }
        } catch (SchedulerException e) {
            log.error("删除任务失败: {}", job.getJobName(), e);
        }
    }

    /**
     * 创建任务数据映射
     */
    private JobDataMap createJobDataMap(SystemJob job) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", job.getJobName());
        jobDataMap.put("jobClass", job.getJobClass());
        return jobDataMap;
    }
}
