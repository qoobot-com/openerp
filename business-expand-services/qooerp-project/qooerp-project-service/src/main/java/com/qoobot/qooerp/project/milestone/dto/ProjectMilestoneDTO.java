package com.qoobot.qooerp.project.milestone.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目里程碑DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
public class ProjectMilestoneDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 里程碑ID
     */
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
     * 项目名称
     */
    private String projectName;

    /**
     * 里程碑名称
     */
    private String milestoneName;

    /**
     * 状态(0-未达成,1-已达成)
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

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
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;
}
