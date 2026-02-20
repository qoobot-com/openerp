package com.qoobot.qooerp.notify.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户订阅实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notify_subscription")
public class NotifySubscription {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("channel_type")
    private String channelType;

    @TableField("receiver")
    private String receiver;

    @TableField("subscribed")
    private Integer subscribed;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}
