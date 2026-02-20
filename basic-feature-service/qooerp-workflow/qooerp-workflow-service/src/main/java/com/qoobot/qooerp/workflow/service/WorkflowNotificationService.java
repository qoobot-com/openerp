package com.qoobot.qooerp.workflow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.workflow.entity.WorkflowNotification;

import java.util.List;

/**
 * 流程通知服务
 */
public interface WorkflowNotificationService {

    /**
     * 发送待办通知
     * @param taskId 任务ID
     * @param assigneeId 处理人ID
     * @param processInstanceId 流程实例ID
     */
    void sendTaskAssignedNotification(String taskId, String assigneeId, String processInstanceId);

    /**
     * 发送审批通知
     * @param taskId 任务ID
     * @param assigneeId 处理人ID
     * @param processInstanceId 流程实例ID
     * @param approved 是否通过
     */
    void sendTaskApprovalNotification(String taskId, String assigneeId, String processInstanceId, boolean approved);

    /**
     * 发送超时提醒
     * @param taskId 任务ID
     * @param assigneeId 处理人ID
     * @param processInstanceId 流程实例ID
     */
    void sendTimeoutNotification(String taskId, String assigneeId, String processInstanceId);

    /**
     * 发送流程完成通知
     * @param processInstanceId 流程实例ID
     * @param startUserId 发起人ID
     */
    void sendProcessCompletedNotification(String processInstanceId, String startUserId);

    /**
     * 发送抄送通知
     * @param taskId 任务ID
     * @param userIds 抄送用户ID列表
     * @param processInstanceId 流程实例ID
     */
    void sendCcNotification(String taskId, List<String> userIds, String processInstanceId);

    /**
     * 创建通知
     * @param notification 通知对象
     * @return 通知ID
     */
    String createNotification(WorkflowNotification notification);

    /**
     * 批量创建通知
     * @param notifications 通知列表
     */
    void batchCreateNotifications(List<WorkflowNotification> notifications);

    /**
     * 发送通知
     * @param notificationId 通知ID
     */
    void sendNotification(String notificationId);

    /**
     * 批量发送通知
     * @param notificationIds 通知ID列表
     */
    void batchSendNotifications(List<String> notificationIds);

    /**
     * 标记通知已读
     * @param notificationId 通知ID
     */
    void markAsRead(String notificationId);

    /**
     * 批量标记已读
     * @param notificationIds 通知ID列表
     */
    void batchMarkAsRead(List<String> notificationIds);

    /**
     * 查询用户通知列表
     * @param userId 用户ID
     * @param isRead 是否已读
     * @return 通知列表
     */
    List<WorkflowNotification> listUserNotifications(String userId, Integer isRead);

    /**
     * 分页查询用户通知
     * @param current 当前页
     * @param size 每页大小
     * @param userId 用户ID
     * @param isRead 是否已读
     * @return 分页结果
     */
    Page<WorkflowNotification> pageUserNotifications(Long current, Long size, String userId, Integer isRead);

    /**
     * 查询未读通知数量
     * @param userId 用户ID
     * @return 未读数量
     */
    Long getUnreadCount(String userId);

    /**
     * 删除通知
     * @param notificationId 通知ID
     */
    void deleteNotification(String notificationId);

    /**
     * 批量删除通知
     * @param notificationIds 通知ID列表
     */
    void batchDeleteNotifications(List<String> notificationIds);

    /**
     * 查询通知详情
     * @param notificationId 通知ID
     * @return 通知对象
     */
    WorkflowNotification getNotification(String notificationId);
}
