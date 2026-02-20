package com.qoobot.qooerp.project.milestone.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目里程碑实体类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
@TableName("project_milestone")
public class ProjectMilestone implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 里程碑编号
     */
    private String milestoneNo;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 里程碑名称
     */
    private String milestoneName;

    /**
     * 状态(0-未达成,1-已达成)
     */
    private Integer status;

    /**
     * 目标日期
     */
    private LocalDate targetDate;

    /**
     * 实际日期
     */
    private LocalDate actualDate;

    /**
     * 描述
     */
    private String description;

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
    private String createBy;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 是否删除(0-否,1-是)
     */
    @TableLogic
    private Integer deleted;
}
