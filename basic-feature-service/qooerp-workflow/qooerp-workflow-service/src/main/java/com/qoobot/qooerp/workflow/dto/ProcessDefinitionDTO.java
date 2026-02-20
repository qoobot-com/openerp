package com.qoobot.qooerp.workflow.dto;

import lombok.Data;

import java.util.Map;

/**
 * 流程定义DTO
 */
@Data
public class ProcessDefinitionDTO {

    /**
     * 流程定义ID
     */
    private String id;

    /**
     * 流程定义Key
     */
    private String key;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 流程描述
     */
    private String description;

    /**
     * 部署ID
     */
    private String deploymentId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 流程分类ID
     */
    private String categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 表单ID
     */
    private String formId;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 流程状态 (1-激活, 0-挂起)
     */
    private Integer suspended;

    /**
     * 创建时间
     */
    private String deploymentTime;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;
}
