package com.qoobot.qooerp.hr.performance.interview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.performance.interview.domain.HrPerformanceInterview;
import com.qoobot.qooerp.hr.performance.interview.mapper.HrPerformanceInterviewMapper;
import com.qoobot.qooerp.hr.performance.interview.service.IHrPerformanceInterviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 绩效面谈服务实现
 */
@Service
public class HrPerformanceInterviewServiceImpl extends ServiceImpl<HrPerformanceInterviewMapper, HrPerformanceInterview> 
    implements IHrPerformanceInterviewService {

    @Override
    public HrPerformanceInterview createInterview(HrPerformanceInterview interview) {
        interview.setStatus("SCHEDULED");
        interview.setEmployeeSigned(false);
        interview.setCreateTime(java.time.LocalDateTime.now());
        save(interview);
        return interview;
    }

    @Override
    public void updateInterview(HrPerformanceInterview interview) {
        interview.setUpdateTime(java.time.LocalDateTime.now());
        updateById(interview);
    }

    @Override
    public void deleteInterview(Long id) {
        removeById(id);
    }

    @Override
    public void completeInterview(Long id) {
        HrPerformanceInterview interview = getById(id);
        if (interview != null) {
            interview.setStatus("COMPLETED");
            interview.setUpdateTime(java.time.LocalDateTime.now());
            updateById(interview);
        }
    }

    @Override
    public void cancelInterview(Long id) {
        HrPerformanceInterview interview = getById(id);
        if (interview != null && "SCHEDULED".equals(interview.getStatus())) {
            interview.setStatus("CANCELLED");
            interview.setUpdateTime(java.time.LocalDateTime.now());
            updateById(interview);
        }
    }

    @Override
    public void employeeSign(Long id) {
        HrPerformanceInterview interview = getById(id);
        if (interview != null && "COMPLETED".equals(interview.getStatus())) {
            interview.setEmployeeSigned(true);
            interview.setSignDate(LocalDate.now());
            interview.setUpdateTime(java.time.LocalDateTime.now());
            updateById(interview);
        }
    }

    @Override
    public java.util.List<HrPerformanceInterview> getByPerformanceId(Long performanceId) {
        return lambdaQuery()
            .eq(HrPerformanceInterview::getPerformanceId, performanceId)
            .orderByDesc(HrPerformanceInterview::getCreateTime)
            .list();
    }

    @Override
    public java.util.List<HrPerformanceInterview> getByEmployeeId(Long employeeId) {
        return lambdaQuery()
            .eq(HrPerformanceInterview::getEmployeeId, employeeId)
            .orderByDesc(HrPerformanceInterview::getCreateTime)
            .list();
    }

    @Override
    public java.util.List<HrPerformanceInterview> getScheduledInterviews() {
        return lambdaQuery()
            .eq(HrPerformanceInterview::getStatus, "SCHEDULED")
            .orderByAsc(HrPerformanceInterview::getInterviewDate)
            .list();
    }
}
