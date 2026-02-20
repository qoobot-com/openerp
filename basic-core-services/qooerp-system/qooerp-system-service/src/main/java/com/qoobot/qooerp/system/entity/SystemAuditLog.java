package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审计日志实体类
 */
@Data
@TableName("system_audit_log")
public class SystemAuditLog {

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 审计类型：data_change-数据变更，sensitive_operation-敏感操作，login-登录
     */
    private String auditType;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 目标表名
     */
    private String targetTable;

    /**
     * 目标ID
     */
    private String targetId;

    /**
     * 变更前值（JSON格式）
     */
    private String oldValue;

    /**
     * 变更后值（JSON格式）
     */
    private String newValue;

    /**
     * 变更字段列表（JSON格式）
     */
    private String changedFields;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
}
