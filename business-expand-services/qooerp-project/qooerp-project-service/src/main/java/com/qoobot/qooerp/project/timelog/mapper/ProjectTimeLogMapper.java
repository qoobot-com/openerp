package com.qoobot.qooerp.project.timelog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.project.timelog.domain.ProjectTimeLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工时记录Mapper接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Mapper
public interface ProjectTimeLogMapper extends BaseMapper<ProjectTimeLog> {
}
