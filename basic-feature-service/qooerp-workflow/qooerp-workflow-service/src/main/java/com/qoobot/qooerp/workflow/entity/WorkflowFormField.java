package com.qoobot.qooerp.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表单字段表
 */
@Data
public class WorkflowFormField implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 表单ID
     */
    private String formId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段标题
     */
    private String fieldLabel;

    /**
     * 字段类型 (text, number, date, select, checkbox, radio, textarea, upload等)
     */
    private String fieldType;

    /**
     * 字段配置 (JSON)
     */
    private String fieldConfig;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否必填 (0-否, 1-是)
     */
    private Integer required;

    /**
     * 是否只读 (0-否, 1-是)
     */
    private Integer readonly;

    /**
     * 是否隐藏 (0-否, 1-是)
     */
    private Integer hidden;

    /**
     * 验证规则 (JSON)
     */
    private String validationRules;

    /**
     * 排序
     */
    private Integer sort;

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
     * 删除标志 (0-未删除, 1-已删除)
     */
    @TableLogic
    private Integer deleted;
}
