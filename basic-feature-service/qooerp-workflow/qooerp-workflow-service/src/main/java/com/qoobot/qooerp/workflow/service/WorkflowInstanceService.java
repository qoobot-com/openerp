package com.qoobot.qooerp.workflow.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.workflow.dto.ProcessInstanceDTO;

import java.util.Map;

/**
 * 流程实例服务
 */
public interface WorkflowInstanceService {

    /**
     * 启动流程实例
     * @param processDefinitionKey 流程定义Key
     * @param businessKey 业务Key
     * @param variables 流程变量
     * @param startUserId 发起人ID
     * @return 流程实例ID
     */
    String startProcess(String processDefinitionKey, String businessKey,
                        Map<String, Object> variables, String startUserId);

    /**
     * 完成流程实例
     * @param processInstanceId 流程实例ID
     */
    void completeProcess(String processInstanceId);

    /**
     * 取消流程实例
     * @param processInstanceId 流程实例ID
     * @param reason 取消原因
     */
    void cancelProcess(String processInstanceId, String reason);

    /**
     * 挂起流程实例
     * @param processInstanceId 流程实例ID
     */
    void suspendProcess(String processInstanceId);

    /**
     * 恢复流程实例
     * @param processInstanceId 流程实例ID
     */
    void activateProcess(String processInstanceId);

    /**
     * 撤回流程实例
     * @param processInstanceId 流程实例ID
     * @param taskId 任务ID
     * @param reason 撤回原因
     */
    void withdrawProcess(String processInstanceId, String taskId, String reason);

    /**
     * 转办流程实例
     * @param processInstanceId 流程实例ID
     * @param targetUserId 目标用户ID
     * @param reason 转办原因
     */
    void transferProcess(String processInstanceId, String targetUserId, String reason);

    /**
     * 查询流程实例详情
     * @param processInstanceId 流程实例ID
     * @return 流程实例DTO
     */
    ProcessInstanceDTO getProcessInstance(String processInstanceId);

    /**
     * 根据业务Key查询流程实例
     * @param businessKey 业务Key
     * @return 流程实例DTO
     */
    ProcessInstanceDTO getProcessInstanceByBusinessKey(String businessKey);

    /**
     * 分页查询流程实例列表
     * @param current 当前页
     * @param size 每页大小
     * @param processDefinitionKey 流程定义Key
     * @param status 流程状态
     * @param startUserId 发起人ID
     * @return 分页结果
     */
    PageResult<ProcessInstanceDTO> pageProcessInstances(Long current, Long size,
                                                  String processDefinitionKey, String status,
                                                  String startUserId);

    /**
     * 获取流程进度
     * @param processInstanceId 流程实例ID
     * @return 进度信息
     */
    Map<String, Object> getProcessProgress(String processInstanceId);

    /**
     * 获取流程图
     * @param processInstanceId 流程实例ID
     * @return 流程图字节数组
     */
    byte[] getProcessDiagram(String processInstanceId);

    /**
     * 获取流程日志
     * @param processInstanceId 流程实例ID
     * @return 流程日志列表
     */
    Map<String, Object> getProcessLogs(String processInstanceId);

    /**
     * 删除流程实例
     * @param processInstanceId 流程实例ID
     * @param cascade 是否级联删除
     */
    void deleteProcess(String processInstanceId, boolean cascade);
}
