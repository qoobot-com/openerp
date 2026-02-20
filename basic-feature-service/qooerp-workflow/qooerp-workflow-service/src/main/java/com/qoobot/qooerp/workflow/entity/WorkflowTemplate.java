package com.qoobot.qooerp.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程模板表
 */
@Data
public class WorkflowTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板编码
     */
    private String code;

    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * 流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 模板版本
     */
    private Integer version;

    /**
     * 模板分类ID
     */
    private String categoryId;

    /**
     * 表单ID
     */
    private String formId;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 模板图标
     */
    private String icon;

    /**
     * 是否公开 (0-私有, 1-公开)
     */
    private Integer isPublic;

    /**
     * 使用次数
     */
    private Integer useCount;

    /**
     * 状态 (0-草稿, 1-已发布, 2-已停用)
     */
    private Integer status;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 删除标志 (0-未删除, 1-已删除)
     */
    @TableLogic
    private Integer deleted;
}
