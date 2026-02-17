package com.qoobot.qooerp.notify.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 黑名单实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notify_blacklist")
public class NotifyBlacklist {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("receiver")
    private String receiver;

    @TableField("channel_type")
    private String channelType;

    @TableField("reason")
    private String reason;

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
