package com.qoobot.qooerp.hr.performance.interview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.performance.interview.domain.HrPerformanceInterview;
import java.util.List;

/**
 * 绩效面谈服务接口
 */
public interface IHrPerformanceInterviewService extends IService<HrPerformanceInterview> {

    /**
     * 创建绩效面谈
     */
    HrPerformanceInterview createInterview(HrPerformanceInterview interview);

    /**
     * 更新绩效面谈
     */
    void updateInterview(HrPerformanceInterview interview);

    /**
     * 删除绩效面谈
     */
    void deleteInterview(Long id);

    /**
     * 完成面谈
     */
    void completeInterview(Long id);

    /**
     * 取消面谈
     */
    void cancelInterview(Long id);

    /**
     * 员工签字确认
     */
    void employeeSign(Long id);

    /**
     * 按绩效评估ID查询面谈
     */
    List<HrPerformanceInterview> getByPerformanceId(Long performanceId);

    /**
     * 按员工ID查询面谈记录
     */
    List<HrPerformanceInterview> getByEmployeeId(Long employeeId);

    /**
     * 查询待完成面谈列表
     */
    List<HrPerformanceInterview> getScheduledInterviews();
}
