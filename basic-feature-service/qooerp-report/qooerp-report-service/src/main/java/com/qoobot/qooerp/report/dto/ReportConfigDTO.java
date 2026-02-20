package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报表配置DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class ReportConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
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
     * 图表类型
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long createBy;
}
