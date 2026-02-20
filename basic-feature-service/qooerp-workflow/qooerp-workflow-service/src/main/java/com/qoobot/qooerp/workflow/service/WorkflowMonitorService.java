package com.qoobot.qooerp.workflow.service;

import java.util.List;
import java.util.Map;

/**
 * 流程监控服务
 */
public interface WorkflowMonitorService {

    /**
     * 查询流程实例详情
     * @param processInstanceId 流程实例ID
     * @return 流程实例详情
     */
    Map<String, Object> getProcessInstanceDetail(String processInstanceId);

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
    List<Map<String, Object>> getProcessLogs(String processInstanceId);

    /**
     * 流程统计分析
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param processDefinitionKey 流程定义Key
     * @return 统计结果
     */
    Map<String, Object> statisticsProcess(String startTime, String endTime, String processDefinitionKey);

    /**
     * 流程性能分析
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param processDefinitionKey 流程定义Key
     * @return 性能分析结果
     */
    Map<String, Object> analyzeProcessPerformance(String startTime, String endTime, String processDefinitionKey);

    /**
     * 查询超时任务列表
     * @param current 当前页
     * @param size 每页大小
     * @return 超时任务列表
     */
    Map<String, Object> listTimeoutTasks(Long current, Long size);

    /**
     * 获取流程热点分析
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 热点分析结果
     */
    Map<String, Object> getProcessHotspot(String startTime, String endTime);

    /**
     * 获取流程趋势分析
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param processDefinitionKey 流程定义Key
     * @return 趋势分析结果
     */
    List<Map<String, Object>> getProcessTrend(String startTime, String endTime, String processDefinitionKey);

    /**
     * 查询用户流程统计
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    Map<String, Object> statisticsUserProcess(String userId, String startTime, String endTime);

    /**
     * 查询部门流程统计
     * @param deptId 部门ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    Map<String, Object> statisticsDeptProcess(String deptId, String startTime, String endTime);

    /**
     * 获取监控大屏数据
     * @return 监控大屏数据
     */
    Map<String, Object> getMonitorDashboard();
}
