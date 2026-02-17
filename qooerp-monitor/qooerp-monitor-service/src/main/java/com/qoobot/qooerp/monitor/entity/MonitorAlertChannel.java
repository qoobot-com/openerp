package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_alert_channel")
public class MonitorAlertChannel {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("channel_name")
    private String channelName;

    @TableField("channel_type")
    private String channelType;

    @TableField("channel_config")
    private String channelConfig;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("description")
    private String description;

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
