package com.qoobot.qooerp.workflow.dto;

import lombok.Data;

/**
 * 表单数据DTO
 */
@Data
public class FormDataDTO {

    /**
     * 记录ID
     */
    private String id;

    /**
     * 表单ID
     */
    private String formId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 表单数据 (JSON)
     */
    private String formData;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 创建人ID
     */
    private String creatorId;
}
