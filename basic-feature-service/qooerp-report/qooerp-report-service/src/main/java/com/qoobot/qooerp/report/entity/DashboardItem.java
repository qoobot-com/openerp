package com.qoobot.qooerp.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 仪表盘项实体
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
@TableName("dashboard_item")
public class DashboardItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 仪表盘ID
     */
    private Long dashboardId;

    /**
     * 报表ID
     */
    private Long reportId;

    /**
     * 项名称
     */
    private String itemName;

    /**
     * 位置配置JSON
     */
    private String positionConfig;

    /**
     * 尺寸配置JSON
     */
    private String sizeConfig;

    /**
     * 显示配置JSON
     */
    private String displayConfig;

    /**
     * 刷新间隔(秒)
     */
    private Integer refreshInterval;

    /**
     * 排序
     */
    private Integer sortOrder;

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
