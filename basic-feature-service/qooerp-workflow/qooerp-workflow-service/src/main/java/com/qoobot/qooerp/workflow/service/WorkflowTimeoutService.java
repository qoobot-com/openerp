package com.qoobot.qooerp.workflow.service;

import java.util.List;
import java.util.Map;

/**
 * 流程超时处理服务
 * 提供任务超时检测、提醒、自动处理等功能
 */
public interface WorkflowTimeoutService {

    /**
     * 检查超时任务
     * @return 超时任务列表
     */
    List<Map<String, Object>> checkTimeoutTasks();

    /**
     * 处理超时任务
     * @param taskId 任务ID
     * @param action 处理方式（remind, delegate, reject, auto_complete等）
     * @param actionParams 处理参数
     */
    void handleTimeoutTask(String taskId, String action, Map<String, Object> actionParams);

    /**
     * 设置任务超时提醒
     * @param taskId 任务ID
     * @param remindBefore 提前提醒时间（分钟）
     */
    void setTimeoutReminder(String taskId, int remindBefore);

    /**
     * 取消任务超时提醒
     * @param taskId 任务ID
     */
    void cancelTimeoutReminder(String taskId);

    /**
     * 获取超时配置
     * @param processDefinitionId 流程定义ID
     * @param taskDefinitionKey 任务定义Key
     * @return 超时配置
     */
    Map<String, Object> getTimeoutConfig(String processDefinitionId, String taskDefinitionKey);

    /**
     * 设置超时配置
     * @param processDefinitionId 流程定义ID
     * @param taskDefinitionKey 任务定义Key
     * @param config 超时配置
     */
    void setTimeoutConfig(String processDefinitionId, String taskDefinitionKey, Map<String, Object> config);

    /**
     * 批量处理超时任务
     * @param processDefinitionId 流程定义ID
     * @return 处理结果
     */
    Map<String, Object> batchHandleTimeoutTasks(String processDefinitionId);

    /**
     * 获取超时统计信息
     * @param processDefinitionId 流程定义ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计信息
     */
    Map<String, Object> getTimeoutStatistics(String processDefinitionId, String startDate, String endDate);
}
