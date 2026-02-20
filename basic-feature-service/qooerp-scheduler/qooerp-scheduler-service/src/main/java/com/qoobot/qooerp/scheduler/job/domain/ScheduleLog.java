package com.qoobot.qooerp.scheduler.job.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务日志实体
 */
@Data
@TableName("schedule_log")
public class ScheduleLog {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long jobId;

    /**
     * 执行时间
     */
    private LocalDateTime executeTime;

    /**
     * 执行结果
     */
    private String executeResult;

    /**
     * 执行时长(毫秒)
     */
    private Long executeDuration;

    /**
     * 状态: SUCCESS/FAILURE/RUNNING/TIMEOUT
     */
    private String status;

    /**
     * 异常信息
     */
    private String exceptionInfo;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
