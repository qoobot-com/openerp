package com.qoobot.qooerp.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message")
public class Message {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("message_no")
    private String messageNo;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("type")
    private Integer type;

    @TableField("priority")
    private Integer priority;

    @TableField("status")
    private Integer status;

    @TableField("sender_id")
    private Long senderId;

    @TableField("sender_name")
    private String senderName;

    @TableField("channels")
    private String channels;

    @TableField("template_id")
    private Long templateId;

    @TableField("send_time")
    private LocalDateTime sendTime;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
}
