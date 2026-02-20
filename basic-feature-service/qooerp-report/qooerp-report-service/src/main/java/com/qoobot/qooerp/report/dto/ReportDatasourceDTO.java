package com.qoobot.qooerp.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报表数据源DTO
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
public class ReportDatasourceDTO implements Serializable {

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
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源类型
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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long createBy;
}
