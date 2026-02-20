package com.qoobot.qooerp.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.permission.entity.PermissionAbacPolicy;
import org.apache.ibatis.annotations.Mapper;

/**
 * ABAC策略Mapper
 */
@Mapper
public interface PermissionAbacPolicyMapper extends BaseMapper<PermissionAbacPolicy> {
}
