package com.qoobot.qooerp.hr.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.hr.attendance.domain.HrAttendance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤记录Mapper
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Mapper
public interface HrAttendanceMapper extends BaseMapper<HrAttendance> {
}
