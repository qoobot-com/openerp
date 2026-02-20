package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_alert")
public class MonitorAlert {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("service_name")
    private String serviceName;

    @TableField("alert_type")
    private String alertType;

    @TableField("alert_level")
    private Integer alertLevel;

    @TableField("alert_content")
    private String alertContent;

    @TableField("alert_time")
    private LocalDateTime alertTime;

    @TableField("status")
    private Integer status;

    @TableField("handler_id")
    private Long handlerId;

    @TableField("handle_time")
    private LocalDateTime handleTime;

    @TableField("handle_remark")
    private String handleRemark;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
