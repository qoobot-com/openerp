package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 告警实例查询 DTO
 */
@Data
public class AlertInstanceQuery {

    /**
     * 告警规则 ID
     */
    private Long ruleId;

    /**
     * 告警级别（INFO, WARNING, CRITICAL）
     */
    private String severity;

    /**
     * 告警状态（PENDING, FIRING, RESOLVED）
     */
    private String status;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 开始时间
     */
    private Timestamp startTime;

    /**
     * 结束时间
     */
    private Timestamp endTime;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 20;
}
