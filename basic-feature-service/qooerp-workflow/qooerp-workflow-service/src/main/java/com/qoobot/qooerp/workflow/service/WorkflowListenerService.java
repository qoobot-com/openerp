package com.qoobot.qooerp.workflow.service;

import java.util.List;
import java.util.Map;

/**
 * 流程监听器服务
 * 提供流程监听器的注册、管理、执行等功能
 */
public interface WorkflowListenerService {

    /**
     * 注册流程监听器
     * @param processDefinitionId 流程定义ID
     * @param eventType 事件类型（start, end, take, create等）
     * @param listenerClass 监听器类名
     * @param listenerType 监听器类型（task, execution, event等）
     * @param description 描述
     */
    void registerListener(String processDefinitionId, String eventType, String listenerClass,
                          String listenerType, String description);

    /**
     * 注销流程监听器
     * @param listenerId 监听器ID
     */
    void unregisterListener(String listenerId);

    /**
     * 获取流程的所有监听器
     * @param processDefinitionId 流程定义ID
     * @return 监听器列表
     */
    List<Map<String, Object>> getProcessListeners(String processDefinitionId);

    /**
     * 触发监听器执行
     * @param processInstanceId 流程实例ID
     * @param eventType 事件类型
     * @param variables 流程变量
     */
    void triggerListener(String processInstanceId, String eventType, Map<String, Object> variables);

    /**
     * 获取监听器执行历史
     * @param processInstanceId 流程实例ID
     * @return 执行历史列表
     */
    List<Map<String, Object>> getListenerExecutionHistory(String processInstanceId);
}
