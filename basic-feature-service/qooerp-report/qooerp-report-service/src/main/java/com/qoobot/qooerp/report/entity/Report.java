package com.qoobot.qooerp.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报表实体
 *
 * @author Auto
 * @since 2026-02-18
 */
@Data
@TableName("report")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 报表编号
     */
    private String reportNo;

    /**
     * 报表名称
     */
    private String reportName;

    /**
     * 报表类型(1-列表,2-图表,3-混合,4-交叉,5-透视)
     */
    private Integer reportType;

    /**
     * 报表分类
     */
    private String category;

    /**
     * 报表描述
     */
    private String description;

    /**
     * 状态(0-草稿,1-已发布,2-已归档)
     */
    private Integer status;

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
