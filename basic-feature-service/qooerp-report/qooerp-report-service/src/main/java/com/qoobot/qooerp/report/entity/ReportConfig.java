package com.qoobot.qooerp.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报表配置实体
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
@TableName("report_config")
public class ReportConfig implements Serializable {

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
     * 配置名称
     */
    private String configName;

    /**
     * 图表类型(bar,line,pie,table,scatter,gauge,funnel,radar)
     */
    private String chartType;

    /**
     * 图表配置JSON
     */
    private String chartConfig;

    /**
     * 列配置JSON
     */
    private String columnConfig;

    /**
     * 过滤条件配置JSON
     */
    private String filterConfig;

    /**
     * 排序配置JSON
     */
    private String sortConfig;

    /**
     * 下钻配置JSON
     */
    private String drillConfig;

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
