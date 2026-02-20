package com.qoobot.qooerp.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程通知表
 */
@Data
public class WorkflowNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 通知类型 (task_assigned-任务分配, task_completed-任务完成, task_timeout-任务超时, process_completed-流程完成)
     */
    private String notificationType;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 接收人ID
     */
    private String receiverId;

    /**
     * 接收人姓名
     */
    private String receiverName;

    /**
     * 通知方式 (system-系统消息, email-邮件, sms-短信, wechat-微信, dingtalk-钉钉)
     */
    private String notifyMethod;

    /**
     * 发送状态 (0-待发送, 1-已发送, 2-发送失败)
     */
    private Integer sendStatus;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    /**
     * 失败原因
     */
    private String failureReason;

    /**
     * 是否已读 (0-未读, 1-已读)
     */
    private Integer isRead;

    /**
     * 已读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 删除标志 (0-未删除, 1-已删除)
     */
    @TableLogic
    private Integer deleted;
}
