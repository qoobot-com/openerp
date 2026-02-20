package com.qoobot.qooerp.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.workflow.entity.WorkflowNotification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程通知Mapper
 */
@Mapper
public interface WorkflowNotificationMapper extends BaseMapper<WorkflowNotification> {
}
