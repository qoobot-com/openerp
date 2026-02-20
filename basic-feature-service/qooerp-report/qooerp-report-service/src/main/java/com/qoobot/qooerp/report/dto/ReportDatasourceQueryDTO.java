package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 报表数据源查询DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class ReportDatasourceQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报表ID
     */
    private Long reportId;

    /**
     * 数据源名称(模糊查询)
     */
    private String datasourceName;

    /**
     * 数据源类型
     */
    private String datasourceType;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
