package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_metrics")
public class MonitorMetrics {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("service_name")
    private String serviceName;

    @TableField("metric_type")
    private String metricType;

    @TableField("metric_name")
    private String metricName;

    @TableField("metric_value")
    private BigDecimal metricValue;

    @TableField("metric_unit")
    private String metricUnit;

    @TableField("collect_time")
    private LocalDateTime collectTime;

    @TableField("tenant_id")
    private Long tenantId;
}
