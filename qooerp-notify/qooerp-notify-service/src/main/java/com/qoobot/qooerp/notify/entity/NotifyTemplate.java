package com.qoobot.qooerp.notify.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.notify.enums.TemplateTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知模板实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notify_template")
public class NotifyTemplate {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("template_code")
    private String templateCode;

    @TableField("template_name")
    private String templateName;

    @TableField("category")
    private String category;

    @TableField("type")
    private TemplateTypeEnum type;

    @TableField("subject")
    private String subject;

    @TableField("content")
    private String content;

    @TableField("variables")
    private String variables;

    @TableField("status")
    private Integer status;

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
