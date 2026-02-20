package com.qoobot.qooerp.scheduler.job.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务报警实体
 */
@Data
@TableName("schedule_notify")
public class ScheduleNotify {

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
     * 报警类型: FAILURE/TIMEOUT/RETRY
     */
    private String notifyType;

    /**
     * 报警级别: LOW/MEDIUM/HIGH/CRITICAL
     */
    private String notifyLevel;

    /**
     * 报警内容
     */
    private String notifyContent;

    /**
     * 报警时间
     */
    private LocalDateTime notifyTime;

    /**
     * 状态: PENDING/HANDLED/IGNORED
     */
    private String status;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 处理备注
     */
    private String handleRemark;

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
