package com.qoobot.qooerp.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_channel")
public class MessageChannel {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("channel_code")
    private String channelCode;

    @TableField("channel_name")
    private String channelName;

    @TableField("channel_type")
    private String channelType;

    @TableField("config")
    private String config;

    @TableField("status")
    private Integer status;

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
