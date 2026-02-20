package com.qoobot.qooerp.project.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.project.project.domain.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 项目Mapper接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 根据项目编号查询
     */
    Project selectByProjectNo(@Param("projectNo") String projectNo, @Param("tenantId") Long tenantId);

    /**
     * 统计项目数量
     */
    Long countByStatus(@Param("status") Integer status, @Param("tenantId") Long tenantId);
}
