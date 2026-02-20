package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 报表配置创建DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class ReportConfigCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
}
