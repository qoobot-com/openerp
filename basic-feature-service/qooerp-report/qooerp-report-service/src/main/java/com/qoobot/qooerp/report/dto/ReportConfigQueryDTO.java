package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 报表配置查询DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class ReportConfigQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报表ID
     */
    private Long reportId;

    /**
     * 配置名称(模糊查询)
     */
    private String configName;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
