package com.qoobot.qooerp.permission.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.permission.entity.PermissionAuditLog;
import com.qoobot.qooerp.permission.service.PermissionAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志控制器
 */
@RestController
@RequestMapping("/api/permission/audit")
@RequiredArgsConstructor
public class PermissionAuditController {

    private final PermissionAuditService auditService;

    /**
     * 记录操作日志
     */
    @PostMapping("/operation")
    public Result<Void> logOperation(@RequestParam Long userId,
                                      @RequestParam String username,
                                      @RequestParam String operationType,
                                      @RequestParam String resourceType,
                                      @RequestParam(required = false) Long resourceId,
                                      @RequestParam(required = false) String description,
                                      @RequestParam(required = false) String beforeData,
                                      @RequestParam(required = false) String afterData) {
        auditService.logOperation(userId, username, operationType, resourceType,
                resourceId, description, beforeData, afterData);
        return Result.success();
    }

    /**
     * 分页查询审计日志
     */
    @GetMapping("/logs")
    public Result<IPage<PermissionAuditLog>> pageAuditLogs(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String logType,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(auditService.pageAuditLogs(current, size, userId, logType,
                operationType, startTime, endTime));
    }

    /**
     * 获取用户的操作日志
     */
    @GetMapping("/user/{userId}")
    public Result<List<PermissionAuditLog>> getUserOperationLogs(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "100") Integer limit) {
        return Result.success(auditService.getUserOperationLogs(userId, limit));
    }

    /**
     * 生成合规报告
     */
    @GetMapping("/report/compliance")
    public Result<String> generateComplianceReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String resourceType) {
        return Result.success(auditService.generateComplianceReport(startTime, endTime, resourceType));
    }

    /**
     * 获取资源变更历史
     */
    @GetMapping("/history/{resourceType}/{resourceId}")
    public Result<List<PermissionAuditLog>> getResourceChangeHistory(
            @PathVariable String resourceType,
            @PathVariable Long resourceId) {
        return Result.success(auditService.getResourceChangeHistory(resourceType, resourceId));
    }

    /**
     * 清理过期日志
     */
    @DeleteMapping("/clean")
    public Result<Void> cleanExpiredLogs(@RequestParam Integer days) {
        auditService.cleanExpiredLogs(days);
        return Result.success();
    }
}
