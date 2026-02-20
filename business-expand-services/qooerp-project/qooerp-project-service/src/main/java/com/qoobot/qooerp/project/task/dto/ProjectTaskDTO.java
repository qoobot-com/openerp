package com.qoobot.qooerp.project.task.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目任务DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
public class ProjectTaskDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Long id;

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 状态(0-待分配,1-进行中,2-已完成,3-已取消,4-已阻塞)
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 负责人ID
     */
    private Long assigneeId;

    /**
     * 负责人姓名
     */
    private String assigneeName;

    /**
     * 优先级(1-低,2-中,3-高,4-紧急)
     */
    private Integer priority;

    /**
     * 优先级名称
     */
    private String priorityName;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 任务描述
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
