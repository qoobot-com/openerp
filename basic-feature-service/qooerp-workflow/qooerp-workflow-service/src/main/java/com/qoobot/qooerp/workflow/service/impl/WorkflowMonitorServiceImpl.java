package com.qoobot.qooerp.workflow.service.impl;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.service.WorkflowMonitorService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程监控服务实现
 */
@Service
@RequiredArgsConstructor
public class WorkflowMonitorServiceImpl implements WorkflowMonitorService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowMonitorServiceImpl.class);

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final RepositoryService repositoryService;
    private final ProcessDiagramGenerator processDiagramGenerator;

    @Override
    public Map<String, Object> getProcessInstanceDetail(String processInstanceId) {
        try {
            Map<String, Object> detail = new HashMap<>();

            // 查询流程实例
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            HistoricProcessInstance historicInstance = null;
            if (processInstance == null) {
                historicInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .singleResult();
            }

            detail.put("processInstance", processInstance != null ? processInstance : historicInstance);

            // 查询当前任务
            List<Task> currentTasks = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .list();
            detail.put("currentTasks", currentTasks);

            // 查询流程定义
            String processDefinitionId = processInstance != null
                    ? processInstance.getProcessDefinitionId()
                    : historicInstance.getProcessDefinitionId();

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();
            detail.put("processDefinition", processDefinition);

            // 查询流程变量
            Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
            detail.put("variables", variables);

            return detail;
        } catch (Exception e) {
            log.error("查询流程实例详情失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getProcessProgress(String processInstanceId) {
        try {
            Map<String, Object> progress = new HashMap<>();

            // 查询已完成的活动实例
            List<HistoricActivityInstance> completedActivities = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .finished()
                    .orderByHistoricActivityInstanceEndTime()
                    .asc()
                    .list();

            // 查询当前活动实例
            List<HistoricActivityInstance> currentActivities = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .unfinished()
                    .list();

            progress.put("completedActivities", completedActivities);
            progress.put("currentActivities", currentActivities);
            progress.put("completedCount", completedActivities.size());
            progress.put("totalCount", completedActivities.size() + currentActivities.size());
            progress.put("progress", calculateProgress(completedActivities.size(), currentActivities.size()));

            return progress;
        } catch (Exception e) {
            log.error("获取流程进度失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_PROGRESS_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_PROGRESS_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public byte[] getProcessDiagram(String processInstanceId) {
        try {
            HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (historicInstance == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getMessage());
            }

            org.flowable.bpmn.model.BpmnModel bpmnModel = repositoryService.getBpmnModel(
                    historicInstance.getProcessDefinitionId()
            );

            // 查询已完成的活动节点
            List<String> completedActivityIds = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .finished()
                    .list()
                    .stream()
                    .map(HistoricActivityInstance::getActivityId)
                    .distinct()
                    .collect(Collectors.toList());

            // 查询当前活动节点
            List<String> currentActivityIds = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .unfinished()
                    .list()
                    .stream()
                    .map(HistoricActivityInstance::getActivityId)
                    .distinct()
                    .collect(Collectors.toList());

            try (var inputStream = processDiagramGenerator.generateDiagram(
                    bpmnModel,
                    "png",
                    completedActivityIds,
                    currentActivityIds,
                    false
            )) {
                return inputStream.readAllBytes();
            }
        } catch (Exception e) {
            log.error("获取流程图失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_DIAGRAM_GET_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_DIAGRAM_GET_FAILED.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getProcessLogs(String processInstanceId) {
        try {
            List<Map<String, Object>> logs = new ArrayList<>();

            // 查询历史任务
            List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricTaskInstanceEndTime()
                    .asc()
                    .list();

            for (HistoricTaskInstance task : tasks) {
                Map<String, Object> log = new HashMap<>();
                log.put("type", "task");
                log.put("taskId", task.getId());
                log.put("taskName", task.getName());
                log.put("assignee", task.getAssignee());
                log.put("startTime", task.getCreateTime());
                log.put("endTime", task.getEndTime());
                log.put("duration", task.getDurationInMillis());
                logs.add(log);
            }

            // 查询评论
            List<org.flowable.engine.task.Comment> comments = taskService.getProcessInstanceComments(processInstanceId);
            for (org.flowable.engine.task.Comment comment : comments) {
                Map<String, Object> log = new HashMap<>();
                log.put("type", "comment");
                log.put("userId", comment.getUserId());
                log.put("time", comment.getTime());
                log.put("message", comment.getFullMessage());
                logs.add(log);
            }

            return logs;
        } catch (Exception e) {
            log.error("获取流程日志失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_LOGS_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_LOGS_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> statisticsProcess(String startTime, String endTime, String processDefinitionKey) {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 查询流程实例统计
            org.flowable.engine.history.HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();

            if (processDefinitionKey != null && !processDefinitionKey.isEmpty()) {
                query.processDefinitionKey(processDefinitionKey);
            }

            List<HistoricProcessInstance> allInstances = query.list();
            List<HistoricProcessInstance> completedInstances = query.finished().list();
            List<HistoricProcessInstance> runningInstances = query.unfinished().list();

            statistics.put("totalCount", allInstances.size());
            statistics.put("completedCount", completedInstances.size());
            statistics.put("runningCount", runningInstances.size());

            // 计算平均处理时间
            long totalDuration = 0;
            for (HistoricProcessInstance instance : completedInstances) {
                if (instance.getDurationInMillis() != null) {
                    totalDuration += instance.getDurationInMillis();
                }
            }
            long avgDuration = completedInstances.isEmpty() ? 0 : totalDuration / completedInstances.size();
            statistics.put("avgDuration", avgDuration);

            return statistics;
        } catch (Exception e) {
            log.error("流程统计分析失败", e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_STATISTICS_FAILED.getCode(), WorkflowErrorCode.PROCESS_STATISTICS_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> analyzeProcessPerformance(String startTime, String endTime, String processDefinitionKey) {
        try {
            Map<String, Object> performance = new HashMap<>();

            // 查询历史任务统计
            org.flowable.task.api.history.HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();

            List<HistoricTaskInstance> allTasks = query.list();
            List<HistoricTaskInstance> completedTasks = query.finished().list();

            performance.put("totalTasks", allTasks.size());
            performance.put("completedTasks", completedTasks.size());

            // 计算平均任务处理时间
            long totalDuration = 0;
            for (HistoricTaskInstance task : completedTasks) {
                if (task.getDurationInMillis() != null) {
                    totalDuration += task.getDurationInMillis();
                }
            }
            long avgDuration = completedTasks.isEmpty() ? 0 : totalDuration / completedTasks.size();
            performance.put("avgTaskDuration", avgDuration);

            // TODO: 更多性能指标

            return performance;
        } catch (Exception e) {
            log.error("流程性能分析失败", e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_PERFORMANCE_ANALYSIS_FAILED.getCode(), WorkflowErrorCode.PROCESS_PERFORMANCE_ANALYSIS_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> listTimeoutTasks(Long current, Long size) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 查询超时任务
            List<Task> allTimeoutTasks = taskService.createTaskQuery()
                    .active()
                    .list()
                    .stream()
                    .filter(task -> task.getDueDate() != null && new Date().after(task.getDueDate()))
                    .collect(Collectors.toList());

            long total = allTimeoutTasks.size();
            List<Task> tasks = allTimeoutTasks.stream()
                    .skip((current - 1) * size)
                    .limit(size)
                    .collect(Collectors.toList());

            result.put("total", total);
            result.put("current", current);
            result.put("size", size);
            result.put("tasks", tasks);

            return result;
        } catch (Exception e) {
            log.error("查询超时任务失败", e);
            throw new BusinessException(WorkflowErrorCode.TASK_TIMEOUT_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.TASK_TIMEOUT_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getProcessHotspot(String startTime, String endTime) {
        try {
            Map<String, Object> hotspot = new HashMap<>();

            // 统计各流程定义的启动次数
            List<HistoricProcessInstance> instances = historyService.createHistoricProcessInstanceQuery()
                    .finished()
                    .list();

            Map<String, Long> processCountMap = instances.stream()
                    .collect(Collectors.groupingBy(
                            HistoricProcessInstance::getProcessDefinitionKey,
                            Collectors.counting()
                    ));

            // 找出启动次数最多的流程
            String hotProcessKey = processCountMap.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            hotspot.put("hotProcessKey", hotProcessKey);
            hotspot.put("hotProcessCount", hotProcessKey != null ? processCountMap.get(hotProcessKey) : 0);
            hotspot.put("processStatistics", processCountMap);

            return hotspot;
        } catch (Exception e) {
            log.error("获取流程热点分析失败", e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_HOTSPOT_ANALYSIS_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_HOTSPOT_ANALYSIS_FAILED.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getProcessTrend(String startTime, String endTime, String processDefinitionKey) {
        try {
            List<Map<String, Object>> trend = new ArrayList<>();

            // TODO: 按天统计流程启动趋势
            // 需要根据startTime和endTime按天分组统计

            return trend;
        } catch (Exception e) {
            log.error("获取流程趋势分析失败", e);
            throw new BusinessException(WorkflowErrorCode.MONITOR_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.MONITOR_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> statisticsUserProcess(String userId, String startTime, String endTime) {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 统计用户发起的流程
            long startedCount = historyService.createHistoricProcessInstanceQuery()
                    .startedBy(userId)
                    .count();

            // 统计用户处理的任务
            long completedTaskCount = historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(userId)
                    .finished()
                    .count();

            // 统计用户的待办任务
            long todoTaskCount = taskService.createTaskQuery()
                    .taskAssignee(userId)
                    .active()
                    .count();

            statistics.put("startedCount", startedCount);
            statistics.put("completedTaskCount", completedTaskCount);
            statistics.put("todoTaskCount", todoTaskCount);

            return statistics;
        } catch (Exception e) {
            log.error("用户流程统计失败: userId={}", userId, e);
            throw new BusinessException(WorkflowErrorCode.MONITOR_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.MONITOR_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> statisticsDeptProcess(String deptId, String startTime, String endTime) {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // TODO: 统计部门的流程数据
            // 需要用户部门关联表

            return statistics;
        } catch (Exception e) {
            log.error("部门流程统计失败: deptId={}", deptId, e);
            throw new BusinessException(WorkflowErrorCode.MONITOR_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.MONITOR_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getMonitorDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();

            // 流程统计
            long totalInstances = historyService.createHistoricProcessInstanceQuery().count();
            long runningInstances = runtimeService.createProcessInstanceQuery().count();
            long completedInstances = historyService.createHistoricProcessInstanceQuery().finished().count();

            // 任务统计
            long totalTasks = taskService.createTaskQuery().active().count();

            // 流程定义统计
            long totalDefinitions = repositoryService.createProcessDefinitionQuery().count();

            dashboard.put("totalInstances", totalInstances);
            dashboard.put("runningInstances", runningInstances);
            dashboard.put("completedInstances", completedInstances);
            dashboard.put("totalTasks", totalTasks);
            dashboard.put("totalDefinitions", totalDefinitions);

            // 今日统计
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = today.withHour(0).withMinute(0).withSecond(0).withNano(0);

            // TODO: 今日启动流程数、完成任务数等

            return dashboard;
        } catch (Exception e) {
            log.error("获取监控大屏数据失败", e);
            throw new BusinessException(WorkflowErrorCode.MONITOR_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.MONITOR_QUERY_FAILED.getMessage());
        }
    }

    /**
     * 计算进度百分比
     */
    private int calculateProgress(int completed, int current) {
        int total = completed + current;
        if (total == 0) {
            return 0;
        }
        return (int) ((completed * 100.0) / total);
    }
}
