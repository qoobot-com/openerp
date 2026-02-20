package com.qoobot.qooerp.scheduler.job.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时任务实体
 */
@Data
@TableName("schedule_job")
public class ScheduleJob {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务编号
     */
    private String jobNo;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务类型: CRON/INTERVAL/ONCE/DEPENDENT
     */
    private String jobType;

    /**
     * 任务类全限定名
     */
    private String jobClass;

    /**
     * Cron表达式
     */
    private String cronExpression;

    /**
     * 执行间隔(秒)
     */
    private Integer interval;

    /**
     * 执行策略: NOW/SCHEDULE/RETRY/SKIP
     */
    private String executeStrategy;

    /**
     * 重试策略: NONE/FIXED/EXPONENTIAL/LINEAR
     */
    private String retryStrategy;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 超时时间(秒)
     */
    private Integer timeout;

    /**
     * 并发策略: SERIAL/PARALLEL
     */
    private String concurrency;

    /**
     * 依赖任务ID
     */
    private Long dependentJobId;

    /**
     * 状态: STOPPED/RUNNING/PAUSED/DELETED
     */
    private String status;

    /**
     * 下次执行时间
     */
    private LocalDateTime nextExecuteTime;

    /**
     * 上次执行时间
     */
    private LocalDateTime prevExecuteTime;

    /**
     * 执行次数
     */
    private Long executeCount;

    /**
     * 成功次数
     */
    private Long successCount;

    /**
     * 失败次数
     */
    private Long failCount;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记: 0-正常,1-删除
     */
    @TableLogic
    private Integer deleted;
}
