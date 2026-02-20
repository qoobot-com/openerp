package com.qoobot.qooerp.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.permission.entity.PermissionAuditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志Mapper
 */
@Mapper
public interface PermissionAuditLogMapper extends BaseMapper<PermissionAuditLog> {
}
