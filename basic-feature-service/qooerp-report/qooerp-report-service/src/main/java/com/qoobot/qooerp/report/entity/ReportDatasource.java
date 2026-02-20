package com.qoobot.qooerp.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报表数据源实体
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
@TableName("report_datasource")
public class ReportDatasource implements Serializable {

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
