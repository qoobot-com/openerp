package com.qoobot.qooerp.system.controller;

import com.qoobot.qooerp.common.response.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.system.dto.SystemAuditLogQueryDTO;
import com.qoobot.qooerp.system.entity.SystemAuditLog;
import com.qoobot.qooerp.system.service.SystemAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 审计日志Controller
 */
@RestController
@RequestMapping("/api/system/audit")
@RequiredArgsConstructor
public class SystemAuditLogController {

    private final SystemAuditLogService auditLogService;

    /**
     * 分页查询审计日志
     */
    @GetMapping("/log/page")
    public Result<IPage<SystemAuditLog>> page(SystemAuditLogQueryDTO query) {
        return Result.success(auditLogService.page(query));
    }

    /**
     * 查看数据变更详情
     */
    @GetMapping("/log/{id}")
    public Result<SystemAuditLog> getDetail(@PathVariable Long id) {
        return Result.success(auditLogService.getDetail(id));
    }

    /**
     * 导出审计日志
     */
    @GetMapping("/log/export")
    public void export(SystemAuditLogQueryDTO query, HttpServletResponse response) {
        // TODO: 实现审计日志导出功能
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=audit_log.xlsx");
    }
}
