package com.qoobot.qooerp.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报表订阅实体
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
@TableName("report_subscription")
public class ReportSubscription implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 报表ID
     */
    private Long reportId;

    /**
     * 订阅用户ID
     */
    private Long userId;

    /**
     * 订阅名称
     */
    private String subscriptionName;

    /**
     * 调度配置JSON
     */
    private String scheduleConfig;

    /**
     * 投递配置JSON
     */
    private String deliveryConfig;

    /**
     * 过滤条件JSON
     */
    private String filterConfig;

    /**
     * 状态(0-暂停,1-启用)
     */
    private Integer status;

    /**
     * 最后运行时间
     */
    private LocalDateTime lastRunTime;

    /**
     * 下次运行时间
     */
    private LocalDateTime nextRunTime;

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
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    private Integer deleted;
}
