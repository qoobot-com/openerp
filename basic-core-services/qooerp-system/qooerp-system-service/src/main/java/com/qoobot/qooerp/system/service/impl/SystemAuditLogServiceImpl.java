package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.system.dto.SystemAuditLogQueryDTO;
import com.qoobot.qooerp.system.entity.SystemAuditLog;
import com.qoobot.qooerp.system.mapper.SystemAuditLogMapper;
import com.qoobot.qooerp.system.service.SystemAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 审计日志Service实现
 */
@Service
@RequiredArgsConstructor
public class SystemAuditLogServiceImpl extends ServiceImpl<SystemAuditLogMapper, SystemAuditLog>
        implements SystemAuditLogService {

    private final SystemAuditLogMapper auditLogMapper;

    @Override
    public IPage<SystemAuditLog> page(SystemAuditLogQueryDTO query) {
        LambdaQueryWrapper<SystemAuditLog> wrapper = new LambdaQueryWrapper<>();
        
        if (query.getAuditType() != null) {
            wrapper.eq(SystemAuditLog::getAuditType, query.getAuditType());
        }
        if (query.getModuleName() != null) {
            wrapper.like(SystemAuditLog::getModuleName, query.getModuleName());
        }
        if (query.getTargetTable() != null) {
            wrapper.eq(SystemAuditLog::getTargetTable, query.getTargetTable());
        }
        if (query.getTargetId() != null) {
            wrapper.eq(SystemAuditLog::getTargetId, query.getTargetId());
        }
        if (query.getUserId() != null) {
            wrapper.eq(SystemAuditLog::getUserId, query.getUserId());
        }
        if (query.getOperateTimeStart() != null) {
            wrapper.ge(SystemAuditLog::getOperateTime, query.getOperateTimeStart());
        }
        if (query.getOperateTimeEnd() != null) {
            wrapper.le(SystemAuditLog::getOperateTime, query.getOperateTimeEnd());
        }
        
        wrapper.orderByDesc(SystemAuditLog::getOperateTime);
        
        return auditLogMapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public SystemAuditLog getDetail(Long id) {
        return super.getById(id);
    }

    @Override
    public void record(SystemAuditLog auditLog) {
        if (auditLog.getOperateTime() == null) {
            auditLog.setOperateTime(LocalDateTime.now());
        }
        save(auditLog);
    }
}
