package com.qoobot.qooerp.scheduler.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleNotify;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务报警 Mapper 接口
 */
@Mapper
public interface ScheduleNotifyMapper extends BaseMapper<ScheduleNotify> {
}
