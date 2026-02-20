package com.qoobot.qooerp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.system.entity.SystemAuditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志Mapper
 */
@Mapper
public interface SystemAuditLogMapper extends BaseMapper<SystemAuditLog> {
}
