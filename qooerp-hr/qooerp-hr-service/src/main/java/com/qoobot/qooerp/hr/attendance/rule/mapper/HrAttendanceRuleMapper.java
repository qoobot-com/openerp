package com.qoobot.qooerp.hr.attendance.rule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.hr.attendance.rule.domain.HrAttendanceRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤规则Mapper
 */
@Mapper
public interface HrAttendanceRuleMapper extends BaseMapper<HrAttendanceRule> {
}
