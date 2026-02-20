package com.qoobot.qooerp.scheduler.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务日志 Mapper 接口
 */
@Mapper
public interface ScheduleLogMapper extends BaseMapper<ScheduleLog> {
}
