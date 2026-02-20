package com.qoobot.qooerp.hr.performance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.performance.domain.HrDegree360Evaluation;
import com.qoobot.qooerp.common.result.Result;

import java.util.List;

/**
 * 360度评估服务接口
 */
public interface IHrDegree360EvaluationService extends IService<HrDegree360Evaluation> {

    /**
     * 提交360度评估
     */
    Result<HrDegree360Evaluation> submitEvaluation(HrDegree360Evaluation evaluation);

    /**
     * 获取评估详情
     */
    Result<HrDegree360Evaluation> getEvaluation(Long id);

    /**
     * 分页查询绩效评估的360度评价列表
     */
    Result<IPage<HrDegree360Evaluation>> listPerformanceEvaluations(IPage<?> page, Long performanceId);

    /**
     * 分页查询员工的评估记录
     */
    Result<IPage<HrDegree360Evaluation>> listEmployeeEvaluations(IPage<?> page, Long employeeId);

    /**
     * 计算平均360度得分
     */
    java.math.BigDecimal calculateAverageScore(Long performanceId);

    /**
     * 删除评估
     */
    Result<Boolean> deleteEvaluation(Long id);
}
