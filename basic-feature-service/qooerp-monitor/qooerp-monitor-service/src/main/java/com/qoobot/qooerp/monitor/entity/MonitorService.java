package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_service")
public class MonitorService {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("service_name")
    private String serviceName;

    @TableField("service_address")
    private String serviceAddress;

    @TableField("service_port")
    private Integer servicePort;

    @TableField("status")
    private Integer status;

    @TableField("health_status")
    private Integer healthStatus;

    @TableField("last_check_time")
    private LocalDateTime lastCheckTime;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
}
