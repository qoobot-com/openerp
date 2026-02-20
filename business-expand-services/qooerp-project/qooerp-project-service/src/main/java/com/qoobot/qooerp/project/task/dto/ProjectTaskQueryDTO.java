package com.qoobot.qooerp.project.task.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 项目任务查询DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
public class ProjectTaskQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 任务名称（模糊查询）
     */
    private String taskName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 负责人ID
     */
    private Long assigneeId;

    /**
     * 优先级
     */
    private Integer priority;

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
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;
}
