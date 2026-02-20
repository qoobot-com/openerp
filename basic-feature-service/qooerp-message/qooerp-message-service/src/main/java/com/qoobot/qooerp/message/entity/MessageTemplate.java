package com.qoobot.qooerp.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_template")
public class MessageTemplate {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("template_no")
    private String templateNo;

    @TableField("template_name")
    private String templateName;

    @TableField("template_type")
    private Integer templateType;

    @TableField("title_template")
    private String titleTemplate;

    @TableField("content_template")
    private String contentTemplate;

    @TableField("variables")
    private String variables;

    @TableField("status")
    private Integer status;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("create_by")
    private Long createBy;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_by")
    private Long updateBy;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
}
