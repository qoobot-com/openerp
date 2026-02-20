package com.qoobot.qooerp.project.project.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 项目查询DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
public class ProjectQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目编号
     */
    private String projectNo;

    /**
     * 项目名称（模糊查询）
     */
    private String projectName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 项目经理ID
     */
    private Long managerId;

    /**
     * 开始日期（起）
     */
    private LocalDate startDateBegin;

    /**
     * 开始日期（止）
     */
    private LocalDate startDateEnd;

    /**
     * 结束日期（起）
     */
    private LocalDate endDateBegin;

    /**
     * 结束日期（止）
     */
    private LocalDate endDateEnd;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;
}
