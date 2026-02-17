package com.qoobot.qooerp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.system.entity.SystemHealthCheck;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康检查Mapper
 */
@Mapper
public interface SystemHealthCheckMapper extends BaseMapper<SystemHealthCheck> {
}
