package com.qoobot.qooerp.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_receiver")
public class MessageReceiver {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("message_id")
    private Long messageId;

    @TableField("receiver_id")
    private Long receiverId;

    @TableField("receiver_name")
    private String receiverName;

    @TableField("read_status")
    private Integer readStatus;

    @TableField("read_time")
    private LocalDateTime readTime;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
