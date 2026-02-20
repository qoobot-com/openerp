package com.qoobot.qooerp.workflow.service;

import java.util.List;
import java.util.Map;

/**
 * 流程网关服务
 * 提供条件网关、并行网关等高级网关功能
 */
public interface WorkflowGatewayService {

    /**
     * 获取条件网关的可用条件
     * @param processDefinitionId 流程定义ID
     * @param gatewayId 网关ID
     * @return 条件列表
     */
    List<Map<String, Object>> getGatewayConditions(String processDefinitionId, String gatewayId);

    /**
     * 获取当前流程实例的条件网关决策信息
     * @param processInstanceId 流程实例ID
     * @param gatewayId 网关ID
     * @return 决策信息
     */
    Map<String, Object> getGatewayDecision(String processInstanceId, String gatewayId);

    /**
     * 设置条件网关的流程变量
     * @param processInstanceId 流程实例ID
     * @param variables 流程变量
     */
    void setGatewayVariables(String processInstanceId, Map<String, Object> variables);

    /**
     * 预览条件网关的执行路径
     * @param processDefinitionId 流程定义ID
     * @param gatewayId 网关ID
     * @param variables 流程变量
     * @return 执行路径
     */
    List<String> previewGatewayPath(String processDefinitionId, String gatewayId,
                                     Map<String, Object> variables);

    /**
     * 获取并行网关的分支信息
     * @param processInstanceId 流程实例ID
     * @param gatewayId 网关ID
     * @return 分支信息
     */
    Map<String, Object> getParallelGatewayStatus(String processInstanceId, String gatewayId);

    /**
     * 查询流程实例的所有网关执行历史
     * @param processInstanceId 流程实例ID
     * @return 网关执行历史
     */
    List<Map<String, Object>> getGatewayHistory(String processInstanceId);
}
