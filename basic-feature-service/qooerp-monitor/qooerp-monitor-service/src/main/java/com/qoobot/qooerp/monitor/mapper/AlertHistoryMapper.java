package com.qoobot.qooerp.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.monitor.entity.AlertHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 告警历史 Mapper 接口
 */
@Mapper
public interface AlertHistoryMapper extends BaseMapper<AlertHistory> {
}
