package com.qoobot.qooerp.workflow.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 任务DTO
 */
@Data
public class TaskDTO {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务定义Key
     */
    private String taskDefinitionKey;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * 流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 流程定义名称
     */
    private String processDefinitionName;

    /**
     * 业务Key
     */
    private String businessKey;

    /**
     * 发起人ID
     */
    private String startUserId;

    /**
     * 发起人姓名
     */
    private String startUserName;

    /**
     * 处理人ID
     */
    private String assigneeId;

    /**
     * 处理人姓名
     */
    private String assigneeName;

    /**
     * 候选人ID列表
     */
    private List<String> candidateUserIds;

    /**
     * 候选组ID列表
     */
    private List<String> candidateGroupIds;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 到期时间
     */
    private Date dueDate;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 任务状态 (pending-待处理, completed-已完成, delegated-已委派, suspended-已挂起)
     */
    private String status;

    /**
     * 是否已超时
     */
    private Boolean isTimeout;

    /**
     * 表单数据ID
     */
    private String formRecordId;

    /**
     * 表单数据
     */
    private Object formData;
}
