package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 报表数据源创建DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class ReportDatasourceCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报表ID
     */
    private Long reportId;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源类型(mysql,postgresql,api,elastic)
     */
    private String datasourceType;

    /**
     * 连接配置JSON
     */
    private String connectionConfig;

    /**
     * SQL查询
     */
    private String sqlQuery;

    /**
     * 缓存配置JSON
     */
    private String cacheConfig;
}
