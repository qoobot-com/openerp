package com.qoobot.qooerp.workflow.service;

import java.util.List;
import java.util.Map;

/**
 * 子流程服务
 * 提供子流程的启动、管理、查询等功能
 */
public interface WorkflowSubProcessService {

    /**
     * 启动子流程
     * @param parentProcessInstanceId 父流程实例ID
     * @param subProcessDefinitionKey 子流程定义Key
     * @param variables 流程变量
     * @param callActivityId 调用活动ID
     * @param userId 用户ID
     * @return 子流程实例ID
     */
    String startSubProcess(String parentProcessInstanceId, String subProcessDefinitionKey,
                          Map<String, Object> variables, String callActivityId, String userId);

    /**
     * 获取父流程的所有子流程
     * @param parentProcessInstanceId 父流程实例ID
     * @return 子流程列表
     */
    List<Map<String, Object>> getSubProcesses(String parentProcessInstanceId);

    /**
     * 获取子流程的父流程信息
     * @param subProcessInstanceId 子流程实例ID
     * @return 父流程信息
     */
    Map<String, Object> getParentProcess(String subProcessInstanceId);

    /**
     * 同步子流程变量到父流程
     * @param subProcessInstanceId 子流程实例ID
     */
    void syncSubProcessVariables(String subProcessInstanceId);

    /**
     * 取消子流程
     * @param subProcessInstanceId 子流程实例ID
     * @param reason 取消原因
     */
    void cancelSubProcess(String subProcessInstanceId, String reason);

    /**
     * 获取子流程层级结构
     * @param processInstanceId 流程实例ID
     * @return 层级结构
     */
    Map<String, Object> getProcessHierarchy(String processInstanceId);

    /**
     * 查询子流程执行历史
     * @param parentProcessInstanceId 父流程实例ID
     * @return 执行历史
     */
    List<Map<String, Object>> getSubProcessHistory(String parentProcessInstanceId);
}
