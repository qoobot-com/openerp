package com.qoobot.qooerp.scheduler.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.scheduler.job.domain.ScheduleJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务 Mapper 接口
 */
@Mapper
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {
}
