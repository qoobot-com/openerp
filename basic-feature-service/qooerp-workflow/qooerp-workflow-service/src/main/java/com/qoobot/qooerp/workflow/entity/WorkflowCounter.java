package com.qoobot.qooerp.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程统计表
 */
@Data
public class WorkflowCounter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 统计类型 (process_definition-流程定义, process_instance-流程实例, task-任务)
     */
    private String counterType;

    /**
     * 统计Key (流程定义Key或流程实例ID或任务ID)
     */
    private String counterKey;

    /**
     * 统计名称
     */
    private String name;

    /**
     * 统计日期
     */
    private String counterDate;

    /**
     * 启动次数
     */
    private Integer startCount;

    /**
     * 完成次数
     */
    private Integer completeCount;

    /**
     * 取消次数
     */
    private Integer cancelCount;

    /**
     * 挂起次数
     */
    private Integer suspendCount;

    /**
     * 平均耗时(秒)
     */
    private Long avgDuration;

    /**
     * 最大耗时(秒)
     */
    private Long maxDuration;

    /**
     * 最小耗时(秒)
     */
    private Long minDuration;

    /**
     * 超时次数
     */
    private Integer timeoutCount;

    /**
     * 统计时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime counterTime;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 删除标志 (0-未删除, 1-已删除)
     */
    @TableLogic
    private Integer deleted;
}
