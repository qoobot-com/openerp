package com.qoobot.qooerp.project.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.project.task.domain.ProjectTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 项目任务Mapper接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Mapper
public interface ProjectTaskMapper extends BaseMapper<ProjectTask> {

    /**
     * 根据项目ID统计任务数
     */
    Long countByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据状态和项目ID统计任务数
     */
    Long countByStatusAndProjectId(@Param("status") Integer status, @Param("projectId") Long projectId);

    /**
     * 统计逾期任务数
     */
    Long countOverdueByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据负责人ID统计任务数
     */
    Long countByAssigneeId(@Param("assigneeId") Long assigneeId);
}
