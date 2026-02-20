package com.qoobot.qooerp.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.auth.entity.AuthLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志Mapper
 */
@Mapper
public interface AuthLoginLogMapper extends BaseMapper<AuthLoginLog> {
}
