package com.qoobot.qooerp.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 仪表盘实体
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
@TableName("dashboard")
public class Dashboard implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 仪表盘编号
     */
    private String dashboardNo;

    /**
     * 仪表盘名称
     */
    private String dashboardName;

    /**
     * 类型(1-个人,2-公共,3-系统)
     */
    private Integer dashboardType;

    /**
     * 布局配置JSON
     */
    private String layoutConfig;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态(0-禁用,1-启用)
     */
    private Integer status;

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
