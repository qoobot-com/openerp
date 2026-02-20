package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_alert_handle")
public class MonitorAlertHandle {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("alert_id")
    private Long alertId;

    @TableField("handler_id")
    private Long handlerId;

    @TableField("handle_action")
    private String handleAction;

    @TableField("handle_remark")
    private String handleRemark;

    @TableField("handle_time")
    private LocalDateTime handleTime;

    @TableField("tenant_id")
    private Long tenantId;
}
