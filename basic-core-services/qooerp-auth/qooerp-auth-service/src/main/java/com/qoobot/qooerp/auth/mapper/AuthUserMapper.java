package com.qoobot.qooerp.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.auth.entity.AuthUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface AuthUserMapper extends BaseMapper<AuthUser> {
}
