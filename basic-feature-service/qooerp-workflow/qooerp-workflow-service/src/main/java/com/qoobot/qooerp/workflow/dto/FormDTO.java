package com.qoobot.qooerp.workflow.dto;

import lombok.Data;

import java.util.List;

/**
 * 表单DTO
 */
@Data
public class FormDTO {

    /**
     * 表单ID
     */
    private String id;

    /**
     * 表单名称
     */
    private String name;

    /**
     * 表单编码
     */
    private String code;

    /**
     * 表单版本
     */
    private Integer version;

    /**
     * 表单类型 (single-单表, master-detail-主子表)
     */
    private String formType;

    /**
     * 表单配置 (JSON)
     */
    private String formConfig;

    /**
     * 表单字段列表
     */
    private List<FormFieldDTO> fields;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态 (0-草稿, 1-已发布, 2-已停用)
     */
    private Integer status;
}
