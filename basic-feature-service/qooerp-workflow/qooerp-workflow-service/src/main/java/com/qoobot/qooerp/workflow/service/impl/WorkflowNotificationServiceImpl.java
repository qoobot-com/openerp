package com.qoobot.qooerp.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.workflow.entity.WorkflowNotification;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.mapper.WorkflowNotificationMapper;
import com.qoobot.qooerp.workflow.service.WorkflowNotificationService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 流程通知服务实现
 */
@Service
@RequiredArgsConstructor
public class WorkflowNotificationServiceImpl implements WorkflowNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowNotificationServiceImpl.class);

    private final WorkflowNotificationMapper notificationMapper;
    private final TaskService taskService;
    private final HistoryService historyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendTaskAssignedNotification(String taskId, String assigneeId, String processInstanceId) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                return;
            }

            WorkflowNotification notification = new WorkflowNotification();
            notification.setId(UUID.randomUUID().toString());
            notification.setNotificationType("task_assigned");
            notification.setReceiverId(assigneeId);
            notification.setProcessInstanceId(processInstanceId);
            notification.setTaskId(taskId);
            notification.setTitle("您有新的待办任务");
            notification.setContent("您有一个新的待办任务：" + task.getName());
            notification.setIsRead(0); // 未读
            notification.setSendStatus(1); // 已发送
            notification.setTenantId(getCurrentTenantId());
            notificationMapper.insert(notification);

            // TODO: 发送实际通知（企业微信、钉钉等）
            log.info("发送待办通知成功: taskId={}, assigneeId={}", taskId, assigneeId);
        } catch (Exception e) {
            log.error("发送待办通知失败: taskId={}, assigneeId={}", taskId, assigneeId, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendTaskApprovalNotification(String taskId, String assigneeId, String processInstanceId, boolean approved) {
        try {
            HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery()
                    .taskId(taskId)
                    .singleResult();

            if (historicTask == null) {
                return;
            }

            HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (historicInstance == null) {
                return;
            }

            // 通知发起人
            WorkflowNotification notification = new WorkflowNotification();
            notification.setId(UUID.randomUUID().toString());
            notification.setNotificationType("task_approval");
            notification.setReceiverId(historicInstance.getStartUserId());
            notification.setProcessInstanceId(processInstanceId);
            notification.setTaskId(taskId);
            notification.setTitle(approved ? "审批通过" : "审批驳回");
            notification.setContent(String.format("您的%s已被%s",
                    historicTask.getName(),
                    approved ? "审批通过" : "审批驳回"));
            notification.setIsRead(0);
            notification.setSendStatus(1);
            notification.setTenantId(getCurrentTenantId());
            notificationMapper.insert(notification);

            log.info("发送审批通知成功: taskId={}, approved={}", taskId, approved);
        } catch (Exception e) {
            log.error("发送审批通知失败: taskId={}, approved={}", taskId, approved, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendTimeoutNotification(String taskId, String assigneeId, String processInstanceId) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                return;
            }

            WorkflowNotification notification = new WorkflowNotification();
            notification.setId(UUID.randomUUID().toString());
            notification.setNotificationType("task_timeout");
            notification.setReceiverId(assigneeId);
            notification.setProcessInstanceId(processInstanceId);
            notification.setTaskId(taskId);
            notification.setTitle("任务超时提醒");
            notification.setContent("您的任务已超时，请及时处理：" + task.getName());
            notification.setIsRead(0);
            notification.setSendStatus(1);
            notification.setTenantId(getCurrentTenantId());
            notificationMapper.insert(notification);

            log.info("发送超时通知成功: taskId={}, assigneeId={}", taskId, assigneeId);
        } catch (Exception e) {
            log.error("发送超时通知失败: taskId={}, assigneeId={}", taskId, assigneeId, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendProcessCompletedNotification(String processInstanceId, String startUserId) {
        try {
            HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (historicInstance == null) {
                return;
            }

            WorkflowNotification notification = new WorkflowNotification();
            notification.setId(UUID.randomUUID().toString());
            notification.setNotificationType("process_completed");
            notification.setReceiverId(startUserId);
            notification.setProcessInstanceId(processInstanceId);
            notification.setTitle("流程已结束");
            notification.setContent("您发起的流程已结束：" + historicInstance.getProcessDefinitionId());
            notification.setIsRead(0);
            notification.setSendStatus(1);
            notification.setTenantId(getCurrentTenantId());
            notificationMapper.insert(notification);

            log.info("发送流程完成通知成功: processInstanceId={}, startUserId={}", processInstanceId, startUserId);
        } catch (Exception e) {
            log.error("发送流程完成通知失败: processInstanceId={}, startUserId={}", processInstanceId, startUserId, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendCcNotification(String taskId, List<String> userIds, String processInstanceId) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                return;
            }

            List<WorkflowNotification> notifications = new ArrayList<>();
            for (String userId : userIds) {
                WorkflowNotification notification = new WorkflowNotification();
                notification.setId(UUID.randomUUID().toString());
                notification.setNotificationType("task_cc");
                notification.setReceiverId(userId);
                notification.setProcessInstanceId(processInstanceId);
                notification.setTaskId(taskId);
                notification.setTitle("任务抄送");
                notification.setContent("您被抄送了任务：" + task.getName());
                notification.setIsRead(0);
                notification.setSendStatus(1);
                notification.setTenantId(getCurrentTenantId());
                notifications.add(notification);
            }

            if (!notifications.isEmpty()) {
                notifications.forEach(notificationMapper::insert);
            }

            log.info("发送抄送通知成功: taskId={}, userIds={}", taskId, userIds);
        } catch (Exception e) {
            log.error("发送抄送通知失败: taskId={}, userIds={}", taskId, userIds, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createNotification(WorkflowNotification notification) {
        try {
            if (notification.getId() == null) {
                notification.setId(UUID.randomUUID().toString());
            }
            if (notification.getIsRead() == null) {
                notification.setIsRead(0);
            }
            if (notification.getTenantId() == null) {
                notification.setTenantId(getCurrentTenantId());
            }
            notificationMapper.insert(notification);
            return notification.getId();
        } catch (Exception e) {
            log.error("创建通知失败", e);
            throw new BusinessException(WorkflowErrorCode.NOTIFICATION_CREATE_FAILED.getCode(),
                    WorkflowErrorCode.NOTIFICATION_CREATE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateNotifications(List<WorkflowNotification> notifications) {
        try {
            for (WorkflowNotification notification : notifications) {
                if (notification.getId() == null) {
                    notification.setId(UUID.randomUUID().toString());
                }
                if (notification.getIsRead() == null) {
                    notification.setIsRead(0);
                }
                if (notification.getTenantId() == null) {
                    notification.setTenantId(getCurrentTenantId());
                }
                notificationMapper.insert(notification);
            }
        } catch (Exception e) {
            log.error("批量创建通知失败", e);
            throw new BusinessException(WorkflowErrorCode.NOTIFICATION_CREATE_FAILED.getCode(),
                    WorkflowErrorCode.NOTIFICATION_CREATE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotification(String notificationId) {
        try {
            WorkflowNotification notification = notificationMapper.selectById(notificationId);
            if (notification == null) {
                throw new BusinessException(WorkflowErrorCode.NOTIFICATION_NOT_FOUND.getCode(), WorkflowErrorCode.NOTIFICATION_NOT_FOUND.getMessage());
            }

            // TODO: 发送实际通知（企业微信、钉钉等）
            notification.setSendTime(LocalDateTime.now());
            notification.setNotifyMethod("system");
            notificationMapper.updateById(notification);

            log.info("发送通知成功: notificationId={}", notificationId);
        } catch (Exception e) {
            log.error("发送通知失败: notificationId={}", notificationId, e);
            throw new BusinessException(WorkflowErrorCode.NOTIFICATION_SEND_FAILED.getCode(), WorkflowErrorCode.NOTIFICATION_SEND_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSendNotifications(List<String> notificationIds) {
        for (String notificationId : notificationIds) {
            sendNotification(notificationId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(String notificationId) {
        try {
            WorkflowNotification notification = notificationMapper.selectById(notificationId);
            if (notification == null) {
                throw new BusinessException(WorkflowErrorCode.NOTIFICATION_NOT_FOUND.getCode(), WorkflowErrorCode.NOTIFICATION_NOT_FOUND.getMessage());
            }

            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(notification);

            log.info("标记通知已读成功: notificationId={}", notificationId);
        } catch (Exception e) {
            log.error("标记通知已读失败: notificationId={}", notificationId, e);
            throw new BusinessException(WorkflowErrorCode.NOTIFICATION_MARK_READ_FAILED.getCode(),
                    WorkflowErrorCode.NOTIFICATION_MARK_READ_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkAsRead(List<String> notificationIds) {
        for (String notificationId : notificationIds) {
            markAsRead(notificationId);
        }
    }

    @Override
    public List<WorkflowNotification> listUserNotifications(String userId, Integer isRead) {
        try {
            LambdaQueryWrapper<WorkflowNotification> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowNotification::getReceiverId, userId);
            if (isRead != null) {
                wrapper.eq(WorkflowNotification::getIsRead, isRead);
            }
            wrapper.orderByDesc(WorkflowNotification::getCreateTime);
            return notificationMapper.selectList(wrapper);
        } catch (Exception e) {
            log.error("查询用户通知列表失败: userId={}", userId, e);
            throw new BusinessException(WorkflowErrorCode.NOTIFICATION_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.NOTIFICATION_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Page<WorkflowNotification> pageUserNotifications(Long current, Long size, String userId, Integer isRead) {
        try {
            LambdaQueryWrapper<WorkflowNotification> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowNotification::getReceiverId, userId);
            if (isRead != null) {
                wrapper.eq(WorkflowNotification::getIsRead, isRead);
            }
            wrapper.orderByDesc(WorkflowNotification::getCreateTime);
            Page<WorkflowNotification> page = new Page<>(current, size);
            return notificationMapper.selectPage(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询用户通知失败: userId={}", userId, e);
            throw new BusinessException(WorkflowErrorCode.NOTIFICATION_QUERY_FAILED.getCode(), WorkflowErrorCode.NOTIFICATION_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Long getUnreadCount(String userId) {
        try {
            LambdaQueryWrapper<WorkflowNotification> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowNotification::getReceiverId, userId);
            wrapper.eq(WorkflowNotification::getIsRead, 0);
            return notificationMapper.selectCount(wrapper);
        } catch (Exception e) {
            log.error("查询未读通知数量失败: userId={}", userId, e);
            throw new BusinessException(WorkflowErrorCode.NOTIFICATION_QUERY_FAILED.getCode(), WorkflowErrorCode.NOTIFICATION_QUERY_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(String notificationId) {
        try {
            notificationMapper.deleteById(notificationId);
            log.info("删除通知成功: notificationId={}", notificationId);
        } catch (Exception e) {
            log.error("删除通知失败: notificationId={}", notificationId, e);
            throw new BusinessException(WorkflowErrorCode.NOTIFICATION_DELETE_FAILED.getCode(), WorkflowErrorCode.NOTIFICATION_DELETE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteNotifications(List<String> notificationIds) {
        for (String notificationId : notificationIds) {
            deleteNotification(notificationId);
        }
    }

    @Override
    public WorkflowNotification getNotification(String notificationId) {
        try {
            return notificationMapper.selectById(notificationId);
        } catch (Exception e) {
            log.error("查询通知详情失败: notificationId={}", notificationId, e);
            throw new BusinessException(WorkflowErrorCode.NOTIFICATION_QUERY_FAILED.getCode(), WorkflowErrorCode.NOTIFICATION_QUERY_FAILED.getMessage());
        }
    }

    /**
     * 获取当前租户ID
     */
    private String getCurrentTenantId() {
        return "default";
    }
}
