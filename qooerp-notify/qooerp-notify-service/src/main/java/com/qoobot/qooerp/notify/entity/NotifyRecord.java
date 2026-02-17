package com.qoobot.qooerp.notify.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.notify.enums.NotifyStatusEnum;
import com.qoobot.qooerp.notify.enums.NotifyTypeEnum;
import com.qoobot.qooerp.notify.enums.TemplateTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notify_record")
public class NotifyRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("notify_no")
    private String notifyNo;

    @TableField("type")
    private NotifyTypeEnum type;

    @TableField("receiver")
    private String receiver;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("template_id")
    private Long templateId;

    @TableField("status")
    private NotifyStatusEnum status;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("retry_count")
    private Integer retryCount;

    @TableField("retry_time")
    private LocalDateTime retryTime;

    @TableField("sent_time")
    private LocalDateTime sentTime;

    @TableField("extra_params")
    private String extraParams;

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
