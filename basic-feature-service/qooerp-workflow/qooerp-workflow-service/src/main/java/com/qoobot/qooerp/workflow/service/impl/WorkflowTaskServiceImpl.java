package com.qoobot.qooerp.workflow.service.impl;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.workflow.dto.TaskDTO;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.service.WorkflowTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowTaskServiceImpl implements WorkflowTaskService {

    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final HistoryService historyService;

    @Override
    public List<TaskDTO> listTodoTasks(String userId) {
        try {
            List<Task> tasks = taskService.createTaskQuery()
                    .taskAssignee(userId)
                    .active()
                    .orderByTaskCreateTime()
                    .desc()
                    .list();

            return tasks.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询待办任务失败: userId={}", userId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_QUERY_FAILED.getCode(), WorkflowErrorCode.TASK_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public PageResult<TaskDTO> pageTodoTasks(Long current, Long size, String userId,
                                             String processDefinitionKey, String taskName) {
        try {
            org.flowable.task.api.TaskQuery query = taskService.createTaskQuery()
                    .taskAssignee(userId)
                    .active()
                    .orderByTaskCreateTime()
                    .desc();

            if (processDefinitionKey != null && !processDefinitionKey.isEmpty()) {
                query.processDefinitionKey(processDefinitionKey);
            }
            if (taskName != null && !taskName.isEmpty()) {
                query.taskNameLike("%" + taskName + "%");
            }

            long total = query.count();
            List<Task> tasks = query.listPage(
                    (int) ((current - 1) * size),
                    (int) size.longValue()
            );

            List<TaskDTO> dtoList = tasks.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return new PageResult<>(current, size, total, dtoList);
        } catch (Exception e) {
            log.error("分页查询待办任务失败: userId={}", userId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_QUERY_FAILED.getCode(), WorkflowErrorCode.TASK_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public List<TaskDTO> listDoneTasks(String userId) {
        try {
            List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(userId)
                    .finished()
                    .orderByHistoricTaskInstanceEndTime()
                    .desc()
                    .list();

            return tasks.stream()
                    .map(this::convertHistoricToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询已办任务失败: userId={}", userId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_QUERY_FAILED.getCode(), WorkflowErrorCode.TASK_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public PageResult<TaskDTO> pageDoneTasks(Long current, Long size, String userId,
                                             String processDefinitionKey, String taskName) {
        try {
            org.flowable.task.api.history.HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(userId)
                    .finished()
                    .orderByHistoricTaskInstanceEndTime()
                    .desc();

            if (processDefinitionKey != null && !processDefinitionKey.isEmpty()) {
                query.processDefinitionKey(processDefinitionKey);
            }
            if (taskName != null && !taskName.isEmpty()) {
                query.taskNameLike("%" + taskName + "%");
            }

            long total = query.count();
            List<HistoricTaskInstance> tasks = query.listPage(
                    (int) ((current - 1) * size),
                    (int) size.longValue()
            );

            List<TaskDTO> dtoList = tasks.stream()
                    .map(this::convertHistoricToDTO)
                    .collect(Collectors.toList());

            return new PageResult<>(current, size, total, dtoList);
        } catch (Exception e) {
            log.error("分页查询已办任务失败: userId={}", userId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_QUERY_FAILED.getCode(), WorkflowErrorCode.TASK_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public List<TaskDTO> listCcTasks(String userId) {
        // TODO: 实现抄送任务查询
        return new ArrayList<>();
    }

    @Override
    public TaskDTO getTask(String taskId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            return convertToDTO(task);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询任务详情失败: taskId={}", taskId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_QUERY_FAILED.getCode(), WorkflowErrorCode.TASK_QUERY_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveTask(String taskId, String comment, Map<String, Object> variables, String userId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 添加审批意见
            if (comment != null && !comment.isEmpty()) {
                taskService.addComment(taskId, task.getProcessInstanceId(), comment);
            }

            // 完成任务
            if (variables == null) {
                variables = new HashMap<>();
            }
            variables.put("approvalUser", userId);
            variables.put("approvalResult", "approved");

            taskService.complete(taskId, variables);

            log.info("审批通过任务成功: taskId={}, userId={}", taskId, userId);
        } catch (Exception e) {
            log.error("审批通过任务失败: taskId={}, userId={}", taskId, userId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_APPROVE_FAILED.getCode(), WorkflowErrorCode.TASK_APPROVE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectTask(String taskId, String comment, String userId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 添加审批意见
            if (comment != null && !comment.isEmpty()) {
                taskService.addComment(taskId, task.getProcessInstanceId(), comment);
            }

            // 设置驳回变量
            Map<String, Object> variables = new HashMap<>();
            variables.put("approvalUser", userId);
            variables.put("approvalResult", "rejected");
            variables.put("rejectReason", comment);

            // 完成任务，流程会根据配置处理驳回
            taskService.complete(taskId, variables);

            log.info("审批驳回任务成功: taskId={}, userId={}", taskId, userId);
        } catch (Exception e) {
            log.error("审批驳回任务失败: taskId={}, userId={}", taskId, userId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_REJECT_FAILED.getCode(), WorkflowErrorCode.TASK_REJECT_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(String taskId, String targetUserId, String comment, String userId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 添加转派说明
            if (comment != null && !comment.isEmpty()) {
                taskService.addComment(taskId, task.getProcessInstanceId(), comment);
            }

            // 委派任务
            taskService.delegateTask(taskId, targetUserId);

            log.info("任务转派成功: taskId={}, fromUserId={}, toUserId={}", taskId, userId, targetUserId);
        } catch (Exception e) {
            log.error("任务转派失败: taskId={}, targetUserId={}", taskId, targetUserId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_DELEGATE_FAILED.getCode(), WorkflowErrorCode.TASK_DELEGATE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTask(String taskId, String targetUserId, String comment, String userId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 添加委派说明
            if (comment != null && !comment.isEmpty()) {
                taskService.addComment(taskId, task.getProcessInstanceId(), comment);
            }

            // 设置任务处理人
            taskService.setAssignee(taskId, targetUserId);

            log.info("任务委派成功: taskId={}, targetUserId={}", taskId, targetUserId);
        } catch (Exception e) {
            log.error("任务委派失败: taskId={}, targetUserId={}", taskId, targetUserId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_ASSIGN_FAILED.getCode(), WorkflowErrorCode.TASK_ASSIGN_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdrawTask(String taskId, String reason, String userId) {
        try {
            HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery()
                    .taskId(taskId)
                    .singleResult();

            if (historicTask == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 删除任务
            taskService.deleteTask(taskId, true);

            // 添加撤回原因
            taskService.addComment(taskId, historicTask.getProcessInstanceId(), reason);

            log.info("任务撤回成功: taskId={}, userId={}", taskId, userId);
        } catch (Exception e) {
            log.error("任务撤回失败: taskId={}, userId={}", taskId, userId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_WITHDRAW_FAILED.getCode(), WorkflowErrorCode.TASK_WITHDRAW_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSignBefore(String taskId, List<String> userIds, String comment, String userId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 添加加签说明
            if (comment != null && !comment.isEmpty()) {
                taskService.addComment(taskId, task.getProcessInstanceId(), comment);
            }

            // 向前加签：在当前任务之前插入新任务
            for (String targetUserId : userIds) {
                // 获取流程定义
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(task.getProcessDefinitionId())
                        .singleResult();

                // TODO: 使用Flowable的运行时API动态插入任务
                // 这需要修改BPMN模型，暂时不支持
            }

            log.info("任务向前加签成功: taskId={}, userIds={}", taskId, userIds);
        } catch (Exception e) {
            log.error("任务向前加签失败: taskId={}, userIds={}", taskId, userIds, e);
            throw new BusinessException(WorkflowErrorCode.TASK_ADD_SIGN_FAILED.getCode(), WorkflowErrorCode.TASK_ADD_SIGN_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSignAfter(String taskId, List<String> userIds, String comment, String userId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 添加加签说明
            if (comment != null && !comment.isEmpty()) {
                taskService.addComment(taskId, task.getProcessInstanceId(), comment);
            }

            // 向后加签：在当前任务之后插入新任务
            // TODO: 实现向后加签逻辑

            log.info("任务向后加签成功: taskId={}, userIds={}", taskId, userIds);
        } catch (Exception e) {
            log.error("任务向后加签失败: taskId={}, userIds={}", taskId, userIds, e);
            throw new BusinessException(WorkflowErrorCode.TASK_ADD_SIGN_FAILED.getCode(), WorkflowErrorCode.TASK_ADD_SIGN_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSign(String taskId, List<String> userIds, String userId) {
        try {
            // 减签：删除指定的用户任务
            for (String targetUserId : userIds) {
                List<Task> tasks = taskService.createTaskQuery()
                        .processInstanceId(getTaskById(taskId).getProcessInstanceId())
                        .taskAssignee(targetUserId)
                        .list();

                for (Task task : tasks) {
                    taskService.deleteTask(task.getId(), true);
                }
            }

            log.info("任务减签成功: taskId={}, userIds={}", taskId, userIds);
        } catch (Exception e) {
            log.error("任务减签失败: taskId={}, userIds={}", taskId, userIds, e);
            throw new BusinessException(WorkflowErrorCode.TASK_REMOVE_SIGN_FAILED.getCode(), WorkflowErrorCode.TASK_REMOVE_SIGN_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ccTask(String taskId, List<String> userIds, String userId) {
        try {
            // TODO: 实现抄送功能
            // 需要建立抄送表来记录抄送关系
            log.info("任务抄送成功: taskId={}, userIds={}", taskId, userIds);
        } catch (Exception e) {
            log.error("任务抄送失败: taskId={}, userIds={}", taskId, userIds, e);
            throw new BusinessException(WorkflowErrorCode.TASK_CC_FAILED.getCode(), WorkflowErrorCode.TASK_CC_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getTaskForm(String taskId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 获取流程变量
            Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());

            // 获取任务局部变量
            Map<String, Object> taskVariables = taskService.getVariables(taskId);
            variables.putAll(taskVariables);

            return variables;
        } catch (Exception e) {
            log.error("获取任务表单失败: taskId={}", taskId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_FORM_GET_FAILED.getCode(), WorkflowErrorCode.TASK_FORM_GET_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTaskForm(String taskId, Map<String, Object> formData, String userId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 保存任务局部变量
            taskService.setVariables(taskId, formData);

            log.info("保存任务表单成功: taskId={}, userId={}", taskId, userId);
        } catch (Exception e) {
            log.error("保存任务表单失败: taskId={}, userId={}", taskId, userId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_FORM_SAVE_FAILED.getCode(), WorkflowErrorCode.TASK_FORM_SAVE_FAILED.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> listTaskHistory(String taskId) {
        try {
            List<Map<String, Object>> historyList = new ArrayList<>();

            // 查询任务的历史记录
            HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery()
                    .taskId(taskId)
                    .singleResult();

            if (historicTask != null) {
                Map<String, Object> history = new HashMap<>();
                history.put("taskId", historicTask.getId());
                history.put("taskName", historicTask.getName());
                history.put("assignee", historicTask.getAssignee());
                history.put("startTime", historicTask.getCreateTime());
                history.put("endTime", historicTask.getEndTime());
                history.put("duration", historicTask.getDurationInMillis());
                historyList.add(history);
            }

            // 查询评论
            List<org.flowable.engine.task.Comment> comments = taskService.getTaskComments(taskId);
            for (org.flowable.engine.task.Comment comment : comments) {
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("type", "comment");
                commentMap.put("userId", comment.getUserId());
                commentMap.put("time", comment.getTime());
                commentMap.put("message", comment.getFullMessage());
                historyList.add(commentMap);
            }

            return historyList;
        } catch (Exception e) {
            log.error("查询任务历史失败: taskId={}", taskId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_HISTORY_QUERY_FAILED.getCode(), WorkflowErrorCode.TASK_HISTORY_QUERY_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTaskDueDate(String taskId, String dueDate) {
        try {
            Date date = new Date(java.text.SimpleDateFormat.getInstance().parse(dueDate).getTime());
            taskService.setDueDate(taskId, date);
            log.info("设置任务到期时间成功: taskId={}, dueDate={}", taskId, dueDate);
        } catch (Exception e) {
            log.error("设置任务到期时间失败: taskId={}, dueDate={}", taskId, dueDate, e);
            throw new BusinessException(WorkflowErrorCode.TASK_DUE_DATE_SET_FAILED.getCode(), WorkflowErrorCode.TASK_DUE_DATE_SET_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveMultiInstanceTask(String taskId, String comment, String userId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 添加审批意见
            if (comment != null && !comment.isEmpty()) {
                taskService.addComment(taskId, task.getProcessInstanceId(), comment);
            }

            // 获取多实例任务信息
            Integer nrOfInstances = (Integer) taskService.getVariable(taskId, "nrOfInstances");
            Integer nrOfCompletedInstances = (Integer) taskService.getVariable(taskId, "nrOfCompletedInstances");
            Integer loopCounter = (Integer) taskService.getVariable(taskId, "loopCounter");

            log.info("会签任务审批: taskId={}, loopCounter={}, nrOfCompletedInstances={}, nrOfInstances={}",
                    taskId, loopCounter, nrOfCompletedInstances, nrOfInstances);

            // 完成当前任务
            Map<String, Object> variables = new HashMap<>();
            variables.put("approvalUser", userId);
            variables.put("approvalResult", "approved");
            taskService.complete(taskId, variables);

            log.info("会签任务审批通过: taskId={}, userId={}", taskId, userId);
        } catch (Exception e) {
            log.error("会签任务审批失败: taskId={}, userId={}", taskId, userId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_APPROVE_FAILED.getCode(), WorkflowErrorCode.TASK_APPROVE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveOrSignTask(String taskId, String comment, String userId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            // 添加审批意见
            if (comment != null && !comment.isEmpty()) {
                taskService.addComment(taskId, task.getProcessInstanceId(), comment);
            }

            // 或签：设置变量以便其他任务可以跳过
            Map<String, Object> variables = new HashMap<>();
            variables.put("approvalUser", userId);
            variables.put("approvalResult", "approved");
            variables.put("orSignApproved", true);  // 标记或签已通过
            variables.put("orSignUserId", userId);

            // 完成当前任务
            taskService.complete(taskId, variables);

            // 取消同一多实例组的其他任务
            String executionId = task.getExecutionId();
            List<Task> siblingTasks = taskService.createTaskQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .list();

            for (Task siblingTask : siblingTasks) {
                if (!siblingTask.getId().equals(taskId)) {
                    try {
                        taskService.resolveTask(siblingTask.getId());
                        taskService.complete(siblingTask.getId(), variables);
                        log.info("或签取消其他任务: taskId={}", siblingTask.getId());
                    } catch (Exception e) {
                        log.warn("取消或签任务失败: taskId={}", siblingTask.getId(), e);
                    }
                }
            }

            log.info("或签任务审批通过: taskId={}, userId={}", taskId, userId);
        } catch (Exception e) {
            log.error("或签任务审批失败: taskId={}, userId={}", taskId, userId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_APPROVE_FAILED.getCode(), WorkflowErrorCode.TASK_APPROVE_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getMultiInstanceTaskStatus(String taskId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(), WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            Map<String, Object> status = new HashMap<>();

            // 获取多实例变量
            Object nrOfInstancesObj = taskService.getVariable(taskId, "nrOfInstances");
            Object nrOfCompletedInstancesObj = taskService.getVariable(taskId, "nrOfCompletedInstances");
            Object nrOfActiveInstancesObj = taskService.getVariable(taskId, "nrOfActiveInstances");
            Object loopCounterObj = taskService.getVariable(taskId, "loopCounter");

            Integer nrOfInstances = nrOfInstancesObj != null ? Integer.parseInt(nrOfInstancesObj.toString()) : 0;
            Integer nrOfCompletedInstances = nrOfCompletedInstancesObj != null ? Integer.parseInt(nrOfCompletedInstancesObj.toString()) : 0;
            Integer nrOfActiveInstances = nrOfActiveInstancesObj != null ? Integer.parseInt(nrOfActiveInstancesObj.toString()) : 0;
            Integer loopCounter = loopCounterObj != null ? Integer.parseInt(loopCounterObj.toString()) : 0;

            status.put("nrOfInstances", nrOfInstances);  // 总实例数
            status.put("nrOfCompletedInstances", nrOfCompletedInstances);  // 已完成实例数
            status.put("nrOfActiveInstances", nrOfActiveInstances);  // 活跃实例数
            status.put("nrOfPendingInstances", nrOfActiveInstances);  // 待处理实例数
            status.put("loopCounter", loopCounter);  // 当前循环计数
            status.put("completionPercentage", nrOfInstances > 0 ?
                    (nrOfCompletedInstances * 100.0 / nrOfInstances) : 0.0);  // 完成百分比

            // 获取所有相关任务
            List<Task> allTasks = taskService.createTaskQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .list();

            List<Map<String, Object>> taskList = new ArrayList<>();
            for (Task t : allTasks) {
                Map<String, Object> taskInfo = new HashMap<>();
                taskInfo.put("taskId", t.getId());
                taskInfo.put("assignee", t.getAssignee());
                taskInfo.put("createTime", t.getCreateTime());
                taskInfo.put("dueDate", t.getDueDate());
                taskInfo.put("status", "pending");

                // 判断当前任务是否已完成
                HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery()
                        .taskId(t.getId())
                        .singleResult();
                if (historicTask != null && historicTask.getEndTime() != null) {
                    taskInfo.put("status", "completed");
                }

                taskList.add(taskInfo);
            }

            status.put("tasks", taskList);

            return status;
        } catch (Exception e) {
            log.error("获取会签任务状态失败: taskId={}", taskId, e);
            throw new BusinessException(WorkflowErrorCode.TASK_QUERY_FAILED.getCode(), WorkflowErrorCode.TASK_QUERY_FAILED.getMessage());
        }
    }

    /**
     * 获取任务
     */
    private Task getTaskById(String taskId) {
        return taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
    }

    /**
     * 转换为DTO（运行中任务）
     */
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskId(task.getId());
        dto.setTaskName(task.getName());
        dto.setTaskDefinitionKey(task.getTaskDefinitionKey());
        dto.setProcessInstanceId(task.getProcessInstanceId());
        dto.setProcessDefinitionId(task.getProcessDefinitionId());
        dto.setAssigneeId(task.getAssignee());
        dto.setPriority(task.getPriority());
        dto.setCreateTime(task.getCreateTime());
        dto.setDueDate(task.getDueDate());
        dto.setStatus("pending");

        // 判断是否超时
        if (task.getDueDate() != null && new Date().after(task.getDueDate())) {
            dto.setIsTimeout(true);
        } else {
            dto.setIsTimeout(false);
        }

        // 获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();

        if (processInstance != null) {
            dto.setBusinessKey(processInstance.getBusinessKey());
            dto.setStartUserId(processInstance.getStartUserId());

            // 获取流程定义
            ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();

            if (definition != null) {
                dto.setProcessDefinitionKey(definition.getKey());
                dto.setProcessDefinitionName(definition.getName());
            }
        }

        // 获取候选人
        List<org.flowable.identitylink.api.IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
        List<String> candidateUsers = identityLinks.stream()
                .filter(link -> link.getUserId() != null)
                .map(org.flowable.identitylink.api.IdentityLink::getUserId)
                .collect(Collectors.toList());
        List<String> candidateGroups = identityLinks.stream()
                .filter(link -> link.getGroupId() != null)
                .map(org.flowable.identitylink.api.IdentityLink::getGroupId)
                .collect(Collectors.toList());

        dto.setCandidateUserIds(candidateUsers);
        dto.setCandidateGroupIds(candidateGroups);

        return dto;
    }

    /**
     * 转换为DTO（历史任务）
     */
    private TaskDTO convertHistoricToDTO(HistoricTaskInstance task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskId(task.getId());
        dto.setTaskName(task.getName());
        dto.setTaskDefinitionKey(task.getTaskDefinitionKey());
        dto.setProcessInstanceId(task.getProcessInstanceId());
        dto.setProcessDefinitionId(task.getProcessDefinitionId());
        dto.setAssigneeId(task.getAssignee());
        dto.setPriority(task.getPriority());
        dto.setCreateTime(task.getCreateTime());
        dto.setDueDate(task.getDueDate());
        dto.setStatus("completed");

        // 获取流程实例
        HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();

        if (historicInstance != null) {
            dto.setBusinessKey(historicInstance.getBusinessKey());
            dto.setStartUserId(historicInstance.getStartUserId());

            // 获取流程定义
            ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();

            if (definition != null) {
                dto.setProcessDefinitionKey(definition.getKey());
                dto.setProcessDefinitionName(definition.getName());
            }
        }

        return dto;
    }
}
