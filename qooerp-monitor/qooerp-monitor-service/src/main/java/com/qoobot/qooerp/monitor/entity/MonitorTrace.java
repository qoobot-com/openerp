package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_trace")
public class MonitorTrace {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("trace_id")
    private String traceId;

    @TableField("span_id")
    private String spanId;

    @TableField("parent_span_id")
    private String parentSpanId;

    @TableField("service_name")
    private String serviceName;

    @TableField("operation_name")
    private String operationName;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("duration")
    private Long duration;

    @TableField("status_code")
    private Integer statusCode;

    @TableField("tags")
    private String tags;

    @TableField("logs")
    private String logs;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
