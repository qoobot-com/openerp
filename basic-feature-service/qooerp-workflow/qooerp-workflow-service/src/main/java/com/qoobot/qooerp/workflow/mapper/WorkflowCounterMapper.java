package com.qoobot.qooerp.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.workflow.entity.WorkflowCounter;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程统计Mapper
 */
@Mapper
public interface WorkflowCounterMapper extends BaseMapper<WorkflowCounter> {
}
