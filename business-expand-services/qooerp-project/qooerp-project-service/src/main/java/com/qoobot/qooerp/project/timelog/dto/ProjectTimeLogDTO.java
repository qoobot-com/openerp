package com.qoobot.qooerp.project.timelog.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工时记录DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
public class ProjectTimeLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工时ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 工作时长(小时)
     */
    private BigDecimal hours;

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
}
