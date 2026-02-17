package com.qoobot.qooerp.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.system.entity.SystemJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务Mapper
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Mapper
public interface SystemJobMapper extends BaseMapper<SystemJob> {
}
