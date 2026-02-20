package com.qoobot.qooerp.workflow.service.impl;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.workflow.dto.ProcessInstanceDTO;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.service.WorkflowInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程实例服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowInstanceServiceImpl implements WorkflowInstanceService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final RepositoryService repositoryService;
    private final org.flowable.engine.HistoryService historyService;
    private final ProcessDiagramGenerator processDiagramGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startProcess(String processDefinitionKey, String businessKey,
                              Map<String, Object> variables, String startUserId) {
        try {
            // 设置发起人
            if (variables == null) {
                variables = new HashMap<>();
            }
            variables.put("startUserId", startUserId);

            // 启动流程
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                    processDefinitionKey,
                    businessKey,
                    variables
            );

            log.info("启动流程成功: processDefinitionKey={}, processInstanceId={}, businessKey={}",
                    processDefinitionKey, processInstance.getId(), businessKey);
            return processInstance.getId();
        } catch (Exception e) {
            log.error("启动流程失败: processDefinitionKey={}", processDefinitionKey, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_START_FAILED.getCode(), WorkflowErrorCode.PROCESS_START_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeProcess(String processInstanceId) {
        try {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (processInstance == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getMessage());
            }

            // 完成所有当前任务
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .list();

            for (Task task : tasks) {
                taskService.complete(task.getId());
            }

            log.info("完成流程成功: processInstanceId={}", processInstanceId);
        } catch (Exception e) {
            log.error("完成流程失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_COMPLETE_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_COMPLETE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelProcess(String processInstanceId, String reason) {
        try {
            // 添加取消原因
            if (reason != null) {
                runtimeService.setVariable(processInstanceId, "cancelReason", reason);
            }

            // 删除流程实例
            runtimeService.deleteProcessInstance(processInstanceId, reason);

            log.info("取消流程成功: processInstanceId={}, reason={}", processInstanceId, reason);
        } catch (Exception e) {
            log.error("取消流程失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_CANCEL_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_CANCEL_FAILED.getMessage());
        }
    }

    @Override
    public void suspendProcess(String processInstanceId) {
        try {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            log.info("挂起流程成功: processInstanceId={}", processInstanceId);
        } catch (Exception e) {
            log.error("挂起流程失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_SUSPEND_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_SUSPEND_FAILED.getMessage());
        }
    }

    @Override
    public void activateProcess(String processInstanceId) {
        try {
            runtimeService.activateProcessInstanceById(processInstanceId);
            log.info("恢复流程成功: processInstanceId={}", processInstanceId);
        } catch (Exception e) {
            log.error("恢复流程失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_RESUME_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_RESUME_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdrawProcess(String processInstanceId, String taskId, String reason) {
        try {
            // 查询历史任务
            HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery()
                    .taskId(taskId)
                    .singleResult();

            if (historicTask == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(),
                        WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 级联删除当前任务
            List<Task> currentTasks = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .list();

            for (Task task : currentTasks) {
                taskService.deleteTask(task.getId(), true);
            }

            log.info("撤回流程成功: processInstanceId={}, taskId={}, reason={}", processInstanceId, taskId, reason);
            } catch (Exception e) {
            log.error("撤回流程失败: processInstanceId={}, taskId={}", processInstanceId, taskId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_WITHDRAW_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_WITHDRAW_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferProcess(String processInstanceId, String targetUserId, String reason) {
        try {
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .list();

            for (Task task : tasks) {
                taskService.setOwner(task.getId(), task.getAssignee());
                taskService.setAssignee(task.getId(), targetUserId);
                taskService.addComment(task.getId(), processInstanceId, reason);
            }

            log.info("转办流程成功: processInstanceId={}, targetUserId={}", processInstanceId, targetUserId);
        } catch (Exception e) {
            log.error("转办流程失败: processInstanceId={}, targetUserId={}", processInstanceId, targetUserId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_TRANSFER_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_TRANSFER_FAILED.getMessage());
        }
    }

    @Override
    public ProcessInstanceDTO getProcessInstance(String processInstanceId) {
        try {
            // 先查询运行中的实例
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (processInstance == null) {
                // 查询历史实例
                HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .singleResult();

                if (historicInstance == null) {
                    throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getCode(),
                            WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getMessage());
                }

                return convertToDTO(historicInstance);
            }

            return convertToDTO(processInstance);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询流程实例失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public ProcessInstanceDTO getProcessInstanceByBusinessKey(String businessKey) {
        try {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceBusinessKey(businessKey)
                    .singleResult();

            if (processInstance == null) {
                HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceBusinessKey(businessKey)
                        .singleResult();

                if (historicInstance == null) {
                    throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getCode(),
                            WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getMessage());
                }

                return convertToDTO(historicInstance);
            }

            return convertToDTO(processInstance);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询流程实例失败: businessKey={}", businessKey, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public PageResult<ProcessInstanceDTO> pageProcessInstances(Long current, Long size,
                                                               String processDefinitionKey, String status,
                                                               String startUserId) {
        try {
            // 查询总数
            long total = buildProcessInstanceQuery(processDefinitionKey, status, startUserId).count();

            // 分页查询
            List<ProcessInstance> processInstances = buildProcessInstanceQuery(processDefinitionKey, status, startUserId)
                    .listPage((int) ((current - 1) * size), (int) size.longValue());

            List<ProcessInstanceDTO> dtoList = processInstances.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return new PageResult<>(current, size, total, dtoList);
        } catch (Exception e) {
            log.error("分页查询流程实例失败", e);
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

            // Flowable 7.0.0 中 generateDiagram 的签名
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
            throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getProcessLogs(String processInstanceId) {
        try {
            Map<String, Object> logs = new HashMap<>();

            // 查询流程实例
            HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            logs.put("processInstance", instance);

            // 查询历史任务
            List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricTaskInstanceEndTime()
                    .asc()
                    .list();

            logs.put("tasks", tasks);

            // 查询历史活动
            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceEndTime()
                    .asc()
                    .list();

            logs.put("activities", activities);

            // 查询评论
            List<org.flowable.engine.task.Comment> comments = taskService.getProcessInstanceComments(processInstanceId);
            logs.put("comments", comments);

            return logs;
        } catch (Exception e) {
            log.error("获取流程日志失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProcess(String processInstanceId, boolean cascade) {
        try {
            // 检查流程是否存在
            HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (historicInstance == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getMessage());
            }

            // 级联删除
            if (cascade) {
                historyService.deleteHistoricProcessInstance(processInstanceId);
            } else {
                // 只删除运行中的实例
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .singleResult();

                if (processInstance != null) {
                    runtimeService.deleteProcessInstance(processInstanceId, "删除流程");
                }
            }

            log.info("删除流程成功: processInstanceId={}, cascade={}", processInstanceId, cascade);
        } catch (Exception e) {
            log.error("删除流程失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_DELETE_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_DELETE_FAILED.getMessage());
        }
    }

    /**
     * 构建流程实例查询
     */
    private ProcessInstanceQuery buildProcessInstanceQuery(String processDefinitionKey, String status, String startUserId) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery()
                .orderByStartTime()
                .desc();

        if (processDefinitionKey != null && !processDefinitionKey.isEmpty()) {
            query.processDefinitionKey(processDefinitionKey);
        }
        if (startUserId != null && !startUserId.isEmpty()) {
            query.startedBy(startUserId);
        }
        if (status != null) {
            switch (status) {
                case "suspended":
                    query.suspended();
                    break;
                case "active":
                    query.active();
                    break;
            }
        }

        return query;
    }

    /**
     * 转换为DTO（运行中实例）
     */
    private ProcessInstanceDTO convertToDTO(ProcessInstance processInstance) {
        ProcessInstanceDTO dto = new ProcessInstanceDTO();
        dto.setProcessInstanceId(processInstance.getId());
        dto.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        dto.setBusinessKey(processInstance.getBusinessKey());
        dto.setStartUserId(processInstance.getStartUserId());
        dto.setStartTime(processInstance.getStartTime());

        // 判断状态
        if (processInstance.isSuspended()) {
            dto.setStatus("suspended");
        } else if (processInstance.isEnded()) {
            dto.setStatus("completed");
        } else {
            dto.setStatus("running");
        }

        // 获取流程定义信息
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processInstance.getProcessDefinitionId())
                .singleResult();

        if (definition != null) {
            dto.setProcessDefinitionKey(definition.getKey());
            dto.setProcessDefinitionName(definition.getName());
            dto.setProcessDefinitionVersion(definition.getVersion());
        }

        // 获取当前任务
        List<Task> currentTasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();

        if (!currentTasks.isEmpty()) {
            Task task = currentTasks.get(0);
            dto.setCurrentTaskName(task.getName());
            dto.setCurrentAssignee(task.getAssignee());
        }

        return dto;
    }

    /**
     * 转换为DTO（历史实例）
     */
    private ProcessInstanceDTO convertToDTO(HistoricProcessInstance historicInstance) {
        ProcessInstanceDTO dto = new ProcessInstanceDTO();
        dto.setProcessInstanceId(historicInstance.getId());
        dto.setProcessDefinitionId(historicInstance.getProcessDefinitionId());
        dto.setBusinessKey(historicInstance.getBusinessKey());
        dto.setStartUserId(historicInstance.getStartUserId());
        dto.setStartTime(historicInstance.getStartTime());
        dto.setEndTime(historicInstance.getEndTime());

        // 判断状态
        if (historicInstance.getEndTime() != null) {
            dto.setStatus("completed");
        } else if (historicInstance.getDeleteReason() != null) {
            dto.setStatus("terminated");
        } else {
            dto.setStatus("running");
        }

        // 计算持续时间
        if (historicInstance.getDurationInMillis() != null) {
            dto.setDuration(historicInstance.getDurationInMillis() / 1000);
        }

        // 获取流程定义信息
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(historicInstance.getProcessDefinitionId())
                .singleResult();

        if (definition != null) {
            dto.setProcessDefinitionKey(definition.getKey());
            dto.setProcessDefinitionName(definition.getName());
            dto.setProcessDefinitionVersion(definition.getVersion());
        }

        return dto;
    }
}
