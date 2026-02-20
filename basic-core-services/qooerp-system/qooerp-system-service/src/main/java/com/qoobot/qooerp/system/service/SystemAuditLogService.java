package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemAuditLogQueryDTO;
import com.qoobot.qooerp.system.entity.SystemAuditLog;

/**
 * 审计日志Service
 */
public interface SystemAuditLogService extends IService<SystemAuditLog> {

    /**
     * 分页查询审计日志
     */
    IPage<SystemAuditLog> page(SystemAuditLogQueryDTO query);

    /**
     * 查看数据变更详情
     */
    SystemAuditLog getDetail(Long id);

    /**
     * 记录审计日志
     */
    void record(SystemAuditLog auditLog);
}
