package com.qoobot.qooerp.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.permission.entity.PermissionAuditLog;
import com.qoobot.qooerp.permission.mapper.PermissionAuditLogMapper;
import com.qoobot.qooerp.permission.service.PermissionAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionAuditServiceImpl extends ServiceImpl<PermissionAuditLogMapper, PermissionAuditLog>
        implements PermissionAuditService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String AUDIT_LOG_QUEUE = "audit:log:queue";

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void logOperation(Long userId, String username, String operationType, String resourceType,
                              Long resourceId, String description, String beforeData, String afterData) {
        try {
            PermissionAuditLog log = new PermissionAuditLog();
            log.setLogType("OPERATION");
            log.setUserId(userId);
            log.setUsername(username);
            log.setOperationType(operationType);
            log.setResourceType(resourceType);
            log.setResourceId(resourceId);
            log.setDescription(description);
            log.setBeforeData(beforeData);
            log.setAfterData(afterData);
            log.setOperateTime(LocalDateTime.now());

            // 异步写入数据库
            save(log);
            log.debug("操作日志记录成功: userId={}, operation={}, resource={}", userId, operationType, resourceType);
        } catch (Exception e) {
            log.error("操作日志记录失败", e);
        }
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void logAudit(PermissionAuditLog log) {
        try {
            log.setOperateTime(LocalDateTime.now());
            save(log);
            log.debug("审计日志记录成功: logType={}, userId={}", log.getLogType(), log.getUserId());
        } catch (Exception e) {
            log.error("审计日志记录失败", e);
        }
    }

    @Override
    public IPage<PermissionAuditLog> pageAuditLogs(Long current, Long size, Long userId, String logType,
                                                       String operationType, LocalDateTime startTime, LocalDateTime endTime) {
        Page<PermissionAuditLog> page = new Page<>(current, size);

        LambdaQueryWrapper<PermissionAuditLog> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(PermissionAuditLog::getUserId, userId);
        }
        if (logType != null && !logType.isEmpty()) {
            wrapper.eq(PermissionAuditLog::getLogType, logType);
        }
        if (operationType != null && !operationType.isEmpty()) {
            wrapper.eq(PermissionAuditLog::getOperationType, operationType);
        }
        if (startTime != null) {
            wrapper.ge(PermissionAuditLog::getOperateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(PermissionAuditLog::getOperateTime, endTime);
        }

        wrapper.orderByDesc(PermissionAuditLog::getOperateTime);

        return page(page, wrapper);
    }

    @Override
    public List<PermissionAuditLog> getUserOperationLogs(Long userId, Integer limit) {
        LambdaQueryWrapper<PermissionAuditLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionAuditLog::getUserId, userId)
                .eq(PermissionAuditLog::getLogType, "OPERATION")
                .orderByDesc(PermissionAuditLog::getOperateTime)
                .last("LIMIT " + (limit != null ? limit : 100));

        return list(wrapper);
    }

    @Override
    public String generateComplianceReport(LocalDateTime startTime, LocalDateTime endTime, String resourceType) {
        StringBuilder report = new StringBuilder();
        report.append("# 权限合规审计报告\n\n");
        report.append("审计时间：").append(startTime).append(" ~ ").append(endTime).append("\n");
        report.append("生成时间：").append(LocalDateTime.now()).append("\n\n");

        // 统计操作日志
        LambdaQueryWrapper<PermissionAuditLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionAuditLog::getLogType, "OPERATION")
                .ge(PermissionAuditLog::getOperateTime, startTime)
                .le(PermissionAuditLog::getOperateTime, endTime);

        if (resourceType != null && !resourceType.isEmpty()) {
            wrapper.eq(PermissionAuditLog::getResourceType, resourceType);
        }

        long totalLogs = count(wrapper);
        report.append("## 操作统计\n");
        report.append("- 总操作次数：").append(totalLogs).append("\n\n");

        // 按资源类型统计
        List<PermissionAuditLog> logs = list(wrapper);
        Map<String, Long> resourceStats = logs.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        PermissionAuditLog::getResourceType,
                        java.util.stream.Collectors.counting()
                ));

        report.append("### 资源类型分布\n");
        resourceStats.forEach((type, count) ->
                report.append("- ").append(type).append("：").append(count).append(" 次\n"));
        report.append("\n");

        // 按操作类型统计
        Map<String, Long> operationStats = logs.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        PermissionAuditLog::getOperationType,
                        java.util.stream.Collectors.counting()
                ));

        report.append("### 操作类型分布\n");
        operationStats.forEach((type, count) ->
                report.append("- ").append(type).append("：").append(count).append(" 次\n"));
        report.append("\n");

        // 风险提示
        report.append("## 风险提示\n");
        long deleteCount = logs.stream()
                .filter(log -> "DELETE".equals(log.getOperationType()))
                .count();

        if (deleteCount > 0) {
            report.append("- ⚠️  警告：检测到 ").append(deleteCount).append(" 次删除操作\n");
        } else {
            report.append("- ✅ 无高风险操作\n");
        }

        return report.toString();
    }

    @Override
    public List<PermissionAuditLog> getResourceChangeHistory(String resourceType, Long resourceId) {
        LambdaQueryWrapper<PermissionAuditLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionAuditLog::getLogType, "AUDIT")
                .eq(PermissionAuditLog::getResourceType, resourceType)
                .eq(PermissionAuditLog::getResourceId, resourceId)
                .orderByDesc(PermissionAuditLog::getOperateTime);

        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanExpiredLogs(Integer days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<PermissionAuditLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(PermissionAuditLog::getOperateTime, expireTime);

        int count = count(wrapper);
        if (count > 0) {
            remove(wrapper);
            log.info("清理过期审计日志: {} 条", count);
        }
    }
}
