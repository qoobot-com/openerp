package com.qoobot.qooerp.workflow.service.impl;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.service.WorkflowNotificationService;
import com.qoobot.qooerp.workflow.service.WorkflowTimeoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流程超时处理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowTimeoutServiceImpl implements WorkflowTimeoutService {

    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final HistoryService historyService;
    private final WorkflowNotificationService notificationService;

    // 超时配置缓存
    private final Map<String, Map<String, Object>> timeoutConfigCache = new ConcurrentHashMap<>();

    // 超时提醒缓存
    private final Map<String, Integer> timeoutReminderCache = new ConcurrentHashMap<>();

    @Override
    @Scheduled(fixedRate = 60000)  // 每分钟执行一次
    public List<Map<String, Object>> checkTimeoutTasks() {
        List<Map<String, Object>> timeoutTasks = new ArrayList<>();

        try {
            // 查询所有有到期时间的任务
            List<Task> tasks = taskService.createTaskQuery()
                    .active()
                    .taskDueBefore(new Date())
                    .list();

            for (Task task : tasks) {
                if (task.getDueDate() != null && new Date().after(task.getDueDate())) {
                    Map<String, Object> timeoutInfo = buildTimeoutInfo(task);
                    timeoutTasks.add(timeoutInfo);

                    // 检查是否需要自动处理
                    handleTimeoutIfConfigured(task, timeoutInfo);

                    // 发送超时提醒
                    sendTimeoutReminder(task, timeoutInfo);
                }
            }

            // 检查即将超时的任务（提前提醒）
            checkUpcomingTimeouts();

            if (!timeoutTasks.isEmpty()) {
                log.info("检测到超时任务: count={}", timeoutTasks.size());
            }

        } catch (Exception e) {
            log.error("检查超时任务失败", e);
        }

        return timeoutTasks;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleTimeoutTask(String taskId, String action, Map<String, Object> actionParams) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(WorkflowErrorCode.TASK_NOT_FOUND.getCode(),
                        WorkflowErrorCode.TASK_NOT_FOUND.getMessage());
            }

            switch (action) {
                case "remind":
                    // 发送提醒
                    sendTimeoutNotification(task, "任务即将超时，请及时处理");
                    break;

                case "delegate":
                    // 转派给其他人
                    String targetUserId = (String) actionParams.get("targetUserId");
                    if (targetUserId != null) {
                        taskService.setAssignee(taskId, targetUserId);
                        addTimeoutComment(task, "任务超时，自动转派给: " + targetUserId);
                        log.info("超时任务转派: taskId={}, targetUserId={}", taskId, targetUserId);
                    }
                    break;

                case "reject":
                    // 自动驳回
                    String reason = (String) actionParams.getOrDefault("reason", "任务超时自动驳回");
                    taskService.addComment(taskId, task.getProcessInstanceId(), reason);
                    Map<String, Object> variables = new HashMap<>();
                    variables.put("approvalResult", "rejected");
                    variables.put("rejectReason", reason);
                    variables.put("timeout", true);
                    taskService.complete(taskId, variables);
                    log.info("超时任务自动驳回: taskId={}, reason={}", taskId, reason);
                    break;

                case "auto_complete":
                    // 自动完成
                    Map<String, Object> completeVars = (Map<String, Object>) actionParams.getOrDefault("variables", new HashMap<>());
                    taskService.complete(taskId, completeVars);
                    addTimeoutComment(task, "任务超时自动完成");
                    log.info("超时任务自动完成: taskId={}", taskId);
                    break;

                default:
                    throw new BusinessException(WorkflowErrorCode.TIMEOUT_HANDLE_FAILED.getCode(),
                            "不支持的超时处理方式: " + action);
            }

            log.info("处理超时任务成功: taskId={}, action={}", taskId, action);

        } catch (Exception e) {
            log.error("处理超时任务失败: taskId={}, action={}", taskId, action, e);
            throw new BusinessException(WorkflowErrorCode.TIMEOUT_HANDLE_FAILED.getCode(),
                    WorkflowErrorCode.TIMEOUT_HANDLE_FAILED.getMessage());
        }
    }

    @Override
    public void setTimeoutReminder(String taskId, int remindBefore) {
        timeoutReminderCache.put(taskId, remindBefore);
        log.info("设置任务超时提醒: taskId={}, remindBefore={}分钟", taskId, remindBefore);
    }

    @Override
    public void cancelTimeoutReminder(String taskId) {
        timeoutReminderCache.remove(taskId);
        log.info("取消任务超时提醒: taskId={}", taskId);
    }

    @Override
    public Map<String, Object> getTimeoutConfig(String processDefinitionId, String taskDefinitionKey) {
        String cacheKey = processDefinitionId + ":" + taskDefinitionKey;
        return timeoutConfigCache.getOrDefault(cacheKey, new HashMap<>());
    }

    @Override
    public void setTimeoutConfig(String processDefinitionId, String taskDefinitionKey, Map<String, Object> config) {
        String cacheKey = processDefinitionId + ":" + taskDefinitionKey;
        timeoutConfigCache.put(cacheKey, config);
        log.info("设置任务超时配置: cacheKey={}, config={}", cacheKey, config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchHandleTimeoutTasks(String processDefinitionId) {
        Map<String, Object> result = new HashMap<>();
        result.put("handledCount", 0);
        result.put("failedCount", 0);
        result.put("errors", new ArrayList<>());

        try {
            List<Task> timeoutTasks = taskService.createTaskQuery()
                    .processDefinitionId(processDefinitionId)
                    .taskDueBefore(new Date())
                    .list();

            for (Task task : timeoutTasks) {
                try {
                    // 获取超时配置
                    Map<String, Object> config = getTimeoutConfig(
                            processDefinitionId, task.getTaskDefinitionKey());

                    if (!config.isEmpty()) {
                        String action = (String) config.get("action");
                        @SuppressWarnings("unchecked")
                        Map<String, Object> actionParams = (Map<String, Object>) config.get("actionParams");

                        handleTimeoutTask(task.getId(), action, actionParams);
                        result.put("handledCount", (Integer) result.get("handledCount") + 1);
                    }

                } catch (Exception e) {
                    result.put("failedCount", (Integer) result.get("failedCount") + 1);
                    @SuppressWarnings("unchecked")
                    List<String> errors = (List<String>) result.get("errors");
                    errors.add(task.getId() + ": " + e.getMessage());
                    log.error("批量处理超时任务失败: taskId={}", task.getId(), e);
                }
            }

            log.info("批量处理超时任务完成: handledCount={}, failedCount={}",
                    result.get("handledCount"), result.get("failedCount"));

        } catch (Exception e) {
            log.error("批量处理超时任务失败: processDefinitionId={}", processDefinitionId, e);
            throw new BusinessException(WorkflowErrorCode.TIMEOUT_HANDLE_FAILED.getCode(),
                    WorkflowErrorCode.TIMEOUT_HANDLE_FAILED.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> getTimeoutStatistics(String processDefinitionId, String startDate, String endDate) {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // 查询指定时间段内完成的任务
            List<org.flowable.task.api.history.HistoricTaskInstance> completedTasks =
                    historyService.createHistoricTaskInstanceQuery()
                            .processDefinitionId(processDefinitionId)
                            .finished()
                            .taskCompletedAfter(parseDate(startDate))
                            .taskCompletedBefore(parseDate(endDate))
                            .list();

            int totalTasks = completedTasks.size();
            int timeoutTasks = 0;
            long totalDuration = 0;
            long timeoutDuration = 0;

            for (org.flowable.task.api.history.HistoricTaskInstance task : completedTasks) {
                long duration = task.getDurationInMillis();
                totalDuration += duration;

                // 如果有到期时间且实际完成时间超过到期时间
                if (task.getDueDate() != null && task.getEndTime() != null &&
                        task.getEndTime().after(task.getDueDate())) {
                    timeoutTasks++;
                    timeoutDuration += (task.getEndTime().getTime() - task.getDueDate().getTime());
                }
            }

            statistics.put("totalTasks", totalTasks);
            statistics.put("timeoutTasks", timeoutTasks);
            statistics.put("timeoutRate", totalTasks > 0 ? (timeoutTasks * 100.0 / totalTasks) : 0.0);
            statistics.put("averageDuration", totalTasks > 0 ? (totalDuration / totalTasks) : 0);
            statistics.put("averageTimeoutDuration", timeoutTasks > 0 ? (timeoutDuration / timeoutTasks) : 0);

            log.info("获取超时统计: processDefinitionId={}, totalTasks={}, timeoutTasks={}, timeoutRate={}",
                    processDefinitionId, totalTasks, timeoutTasks, statistics.get("timeoutRate"));

        } catch (Exception e) {
            log.error("获取超时统计失败: processDefinitionId={}", processDefinitionId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_STATISTICS_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_STATISTICS_FAILED.getMessage());
        }

        return statistics;
    }

    /**
     * 构建超时信息
     */
    private Map<String, Object> buildTimeoutInfo(Task task) {
        Map<String, Object> info = new HashMap<>();
        info.put("taskId", task.getId());
        info.put("taskName", task.getName());
        info.put("taskDefinitionKey", task.getTaskDefinitionKey());
        info.put("processInstanceId", task.getProcessInstanceId());
        info.put("processDefinitionId", task.getProcessDefinitionId());
        info.put("assignee", task.getAssignee());
        info.put("createTime", task.getCreateTime());
        info.put("dueDate", task.getDueDate());
        info.put("currentTime", new Date());

        // 计算超时时长
        if (task.getDueDate() != null) {
            long timeoutMillis = new Date().getTime() - task.getDueDate().getTime();
            info.put("timeoutMillis", timeoutMillis);
            info.put("timeoutMinutes", timeoutMillis / 60000);
            info.put("timeoutHours", timeoutMillis / 3600000);
        }

        return info;
    }

    /**
     * 根据配置自动处理超时
     */
    private void handleTimeoutIfConfigured(Task task, Map<String, Object> timeoutInfo) {
        Map<String, Object> config = getTimeoutConfig(
                task.getProcessDefinitionId(), task.getTaskDefinitionKey());

        if (config.containsKey("autoAction")) {
            String action = (String) config.get("autoAction");
            @SuppressWarnings("unchecked")
            Map<String, Object> actionParams = (Map<String, Object>) config.get("actionParams");

            try {
                handleTimeoutTask(task.getId(), action, actionParams);
            } catch (Exception e) {
                log.error("自动处理超时任务失败: taskId={}", task.getId(), e);
            }
        }
    }

    /**
     * 发送超时提醒
     */
    private void sendTimeoutReminder(Task task, Map<String, Object> timeoutInfo) {
        try {
            Long timeoutMinutes = (Long) timeoutInfo.get("timeoutMinutes");
            if (timeoutMinutes != null && timeoutMinutes % 60 == 0) {
                // 每小时提醒一次
                sendTimeoutNotification(task, "任务已超时" + (timeoutMinutes / 60) + "小时，请尽快处理");
            }
        } catch (Exception e) {
            log.error("发送超时提醒失败: taskId={}", task.getId(), e);
        }
    }

    /**
     * 检查即将超时的任务
     */
    private void checkUpcomingTimeouts() {
        List<Task> tasks = taskService.createTaskQuery()
                .active()
                .list();

        Date now = new Date();

        for (Task task : tasks) {
            if (task.getDueDate() != null && task.getAssignee() != null) {
                long timeToDue = task.getDueDate().getTime() - now.getTime();
                int timeToDueMinutes = (int) (timeToDue / 60000);

                // 检查是否需要提醒
                Integer remindBefore = timeoutReminderCache.get(task.getId());
                if (remindBefore != null && timeToDueMinutes <= remindBefore && timeToDueMinutes > 0) {
                    sendTimeoutNotification(task, "任务即将在" + timeToDueMinutes + "分钟后超时，请及时处理");
                }
            }
        }
    }

    /**
     * 发送超时通知
     */
    private void sendTimeoutNotification(Task task, String message) {
        try {
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("type", "timeout");
            notificationData.put("message", message);
            notificationData.put("taskId", task.getId());
            notificationData.put("taskName", task.getName());
            notificationData.put("dueDate", task.getDueDate());

            // 调用通知服务发送通知
            // notificationService.sendNotification(task.getAssignee(), notificationData);
            log.info("发送超时通知: taskId={}, assignee={}, message={}",
                    task.getId(), task.getAssignee(), message);

        } catch (Exception e) {
            log.error("发送超时通知失败: taskId={}", task.getId(), e);
        }
    }

    /**
     * 添加超时评论
     */
    private void addTimeoutComment(Task task, String message) {
        taskService.addComment(task.getId(), task.getProcessInstanceId(), message);
    }

    /**
     * 解析日期
     */
    private Date parseDate(String dateStr) {
        try {
            return java.text.SimpleDateFormat.getInstance().parse(dateStr);
        } catch (Exception e) {
            log.warn("日期解析失败: {}", dateStr, e);
            return null;
        }
    }
}
