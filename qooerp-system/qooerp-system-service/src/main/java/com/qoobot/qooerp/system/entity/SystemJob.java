package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 定时任务表实体
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_job")
public class SystemJob {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    @TableField("job_name")
    private String jobName;

    /**
     * 任务组
     */
    @TableField("job_group")
    private String jobGroup;

    /**
     * 任务类名
     */
    @TableField("job_class")
    private String jobClass;

    /**
     * Cron表达式
     */
    @TableField("cron_expression")
    private String cronExpression;

    /**
     * 任务描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态：0-暂停 1-运行中
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否并发执行：0-否 1-是
     */
    @TableField("concurrent")
    private Integer concurrent;

    /**
     * 错过策略：1-立即执行 2-执行一次 3-放弃
     */
    @TableField("misfire_policy")
    private Integer misfirePolicy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}
