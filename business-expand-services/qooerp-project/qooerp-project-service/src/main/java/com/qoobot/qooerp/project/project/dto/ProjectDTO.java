package com.qoobot.qooerp.project.project.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
public class ProjectDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private Long id;

    /**
     * 项目编号
     */
    private String projectNo;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 状态(0-草稿,1-已审批,2-进行中,3-已完成,4-已关闭)
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 项目经理ID
     */
    private Long managerId;

    /**
     * 项目经理姓名
     */
    private String managerName;

    /**
     * 项目描述
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
