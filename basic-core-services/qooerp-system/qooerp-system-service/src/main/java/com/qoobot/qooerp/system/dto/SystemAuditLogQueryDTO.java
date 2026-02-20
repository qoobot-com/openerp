package com.qoobot.qooerp.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审计日志查询DTO
 */
@Data
public class SystemAuditLogQueryDTO {

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页条数
     */
    private Integer size = 10;

    /**
     * 审计类型：data_change-数据变更，sensitive_operation-敏感操作，login-登录
     */
    private String auditType;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 目标表名
     */
    private String targetTable;

    /**
     * 目标ID
     */
    private String targetId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 操作时间-开始
     */
    private LocalDateTime operateTimeStart;

    /**
     * 操作时间-结束
     */
    private LocalDateTime operateTimeEnd;
}
