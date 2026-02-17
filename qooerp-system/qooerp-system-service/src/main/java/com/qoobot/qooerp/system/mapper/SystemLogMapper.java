package com.qoobot.qooerp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.system.entity.SystemLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Mapper
public interface SystemLogMapper extends BaseMapper<SystemLog> {
}
