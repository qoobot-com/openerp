package com.qoobot.qooerp.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审计日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission_audit_log")
public class PermissionAuditLog extends BaseEntity {

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 日志类型：OPERATION-操作 AUDIT-审计
     */
    private String logType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作资源类型
     */
    private String resourceType;

    /**
     * 操作资源ID
     */
    private Long resourceId;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 变更前数据
     */
    private String beforeData;

    /**
     * 变更后数据
     */
    private String afterData;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
}
