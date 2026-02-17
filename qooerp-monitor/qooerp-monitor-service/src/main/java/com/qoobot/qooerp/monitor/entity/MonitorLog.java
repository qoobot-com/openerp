package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_log")
public class MonitorLog {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("service_name")
    private String serviceName;

    @TableField("log_type")
    private String logType;

    @TableField("log_level")
    private String logLevel;

    @TableField("log_content")
    private String logContent;

    @TableField("log_time")
    private LocalDateTime logTime;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("user_id")
    private Long userId;

    @TableField("tenant_id")
    private Long tenantId;
}
