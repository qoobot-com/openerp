package com.qoobot.qooerp.permission.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.permission.entity.PermissionAuditLog;

import java.time.LocalDateTime;

/**
 * 审计日志服务接口
 */
public interface PermissionAuditService {

    /**
     * 记录操作日志
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param operationType 操作类型
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @param description 描述
     * @param beforeData 变更前数据
     * @param afterData 变更后数据
     */
    void logOperation(Long userId, String username, String operationType, String resourceType,
                      Long resourceId, String description, String beforeData, String afterData);

    /**
     * 记录审计日志
     *
     * @param log 审计日志
     */
    void logAudit(PermissionAuditLog log);

    /**
     * 分页查询审计日志
     *
     * @param current 当前页
     * @param size 每页大小
     * @param userId 用户ID（可选）
     * @param logType 日志类型（可选）
     * @param operationType 操作类型（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    IPage<PermissionAuditLog> pageAuditLogs(Long current, Long size, Long userId, String logType,
                                            String operationType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户的操作日志
     *
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 操作日志列表
     */
    List<PermissionAuditLog> getUserOperationLogs(Long userId, Integer limit);

    /**
     * 生成合规报告
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param resourceType 资源类型（可选）
     * @return 合规报告
     */
    String generateComplianceReport(LocalDateTime startTime, LocalDateTime endTime, String resourceType);

    /**
     * 获取资源变更历史
     *
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return 变更历史列表
     */
    List<PermissionAuditLog> getResourceChangeHistory(String resourceType, Long resourceId);

    /**
     * 清理过期日志
     *
     * @param days 保留天数
     */
    void cleanExpiredLogs(Integer days);
}
