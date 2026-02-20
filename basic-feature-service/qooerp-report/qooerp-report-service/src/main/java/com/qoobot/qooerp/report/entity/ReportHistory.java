package com.qoobot.qooerp.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报表历史实体
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
@TableName("report_history")
public class ReportHistory implements Serializable {

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
     * 快照数据JSON
     */
    private String snapshotData;

    /**
     * 快照配置JSON
     */
    private String snapshotConfig;

    /**
     * 快照时间
     */
    private LocalDateTime snapshotTime;

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
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    private Integer deleted;
}
