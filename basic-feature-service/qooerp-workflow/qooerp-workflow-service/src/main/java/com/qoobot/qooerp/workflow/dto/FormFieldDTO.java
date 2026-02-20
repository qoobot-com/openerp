package com.qoobot.qooerp.workflow.dto;

import lombok.Data;

/**
 * 表单字段DTO
 */
@Data
public class FormFieldDTO {

    /**
     * 字段ID
     */
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
}
