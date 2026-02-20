package com.qoobot.qooerp.project.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.project.comment.domain.ProjectComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目评论Mapper接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Mapper
public interface ProjectCommentMapper extends BaseMapper<ProjectComment> {
}
