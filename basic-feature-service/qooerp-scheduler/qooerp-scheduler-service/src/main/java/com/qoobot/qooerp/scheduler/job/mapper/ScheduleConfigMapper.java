package com.qoobot.qooerp.scheduler.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务配置 Mapper 接口
 */
@Mapper
public interface ScheduleConfigMapper extends BaseMapper<ScheduleConfig> {
}
