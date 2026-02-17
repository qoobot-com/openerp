package com.qoobot.qooerp.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.monitor.entity.AlertRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface AlertRuleMapper extends BaseMapper<AlertRule> {

    /**
     * 查询启用的告警规则
     *
     * @return 启用的告警规则列表
     */
    @Select("""
        SELECT * FROM alert_rule
        WHERE enabled = true
          AND (silence_start IS NULL OR silence_end IS NULL
               OR NOW() < silence_start OR NOW() > silence_end)
          AND deleted = 0
    """)
    List<AlertRule> selectEnabledRules();
}
