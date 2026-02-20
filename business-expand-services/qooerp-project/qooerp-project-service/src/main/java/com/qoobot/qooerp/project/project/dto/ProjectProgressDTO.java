package com.qoobot.qooerp.project.project.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 项目进度DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
@Builder
public class ProjectProgressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编号
     */
    private String projectNo;

    /**
     * 总任务数
     */
    private Long totalTasks;

    /**
     * 已完成任务数
     */
    private Long completedTasks;

    /**
     * 进行中任务数
     */
    private Long inProgressTasks;

    /**
     * 待分配任务数
     */
    private Long pendingTasks;

    /**
     * 逾期任务数
     */
    private Long overdueTasks;

    /**
     * 完成进度（百分比）
     */
    private BigDecimal progress;

    /**
     * 预计完成日期
     */
    private LocalDate estimatedEndDate;

    /**
     * 实际完成日期
     */
    private LocalDate actualEndDate;

    /**
     * 总里程碑数
     */
    private Long totalMilestones;

    /**
     * 已完成里程碑数
     */
    private Long completedMilestones;

    /**
     * 里程碑进度（百分比）
     */
    private BigDecimal milestoneProgress;
}
