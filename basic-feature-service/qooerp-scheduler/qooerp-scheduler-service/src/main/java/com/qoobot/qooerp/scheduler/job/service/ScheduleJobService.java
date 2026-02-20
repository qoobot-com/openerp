package com.qoobot.qooerp.scheduler.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.scheduler.job.dto.JobExecuteDTO;
import com.qoobot.qooerp.scheduler.job.dto.JobQueryDTO;
import com.qoobot.qooerp.scheduler.job.dto.ScheduleJobDTO;

/**
 * 定时任务 Service 接口
 */
public interface ScheduleJobService {

    /**
     * 创建任务
     */
    ScheduleJobDTO createJob(ScheduleJobDTO dto);

    /**
     * 更新任务
     */
    ScheduleJobDTO updateJob(Long id, ScheduleJobDTO dto);

    /**
     * 删除任务
     */
    void deleteJob(Long id);

    /**
     * 根据ID查询任务
     */
    ScheduleJobDTO getJobById(Long id);

    /**
     * 分页查询任务
     */
    Page<ScheduleJobDTO> queryJobs(JobQueryDTO queryDTO);

    /**
     * 启动任务
     */
    void startJob(Long id);

    /**
     * 停止任务
     */
    void stopJob(Long id);

    /**
     * 暂停任务
     */
    void pauseJob(Long id);

    /**
     * 恢复任务
     */
    void resumeJob(Long id);

    /**
     * 手动执行任务
     */
    void executeJob(JobExecuteDTO dto);

    /**
     * 获取任务日志
     */
    Page<com.qoobot.qooerp.scheduler.job.dto.ScheduleLogDTO> getJobLogs(Long jobId, Integer current, Integer size);
}
