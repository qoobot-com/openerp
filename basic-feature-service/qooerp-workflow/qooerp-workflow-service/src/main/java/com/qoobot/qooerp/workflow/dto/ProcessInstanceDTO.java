package com.qoobot.qooerp.workflow.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 流程实例DTO
 */
@Data
public class ProcessInstanceDTO {

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
     * 流程版本
     */
    private Integer processDefinitionVersion;

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
     * 发起部门ID
     */
    private String startDeptId;

    /**
     * 发起部门名称
     */
    private String startDeptName;

    /**
     * 流程状态 (running-运行中, completed-已完成, suspended-已挂起, terminated-已终止)
     */
    private String status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 挂起时间
     */
    private Date suspendTime;

    /**
     * 持续时间(秒)
     */
    private Long duration;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;

    /**
     * 当前节点
     */
    private String currentTaskName;

    /**
     * 当前处理人
     */
    private String currentAssignee;

    /**
     * 表单数据ID
     */
    private String formRecordId;
}
