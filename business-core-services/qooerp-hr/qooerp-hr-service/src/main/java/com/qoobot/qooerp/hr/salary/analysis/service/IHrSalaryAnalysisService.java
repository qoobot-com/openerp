package com.qoobot.qooerp.hr.salary.analysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.salary.analysis.domain.HrSalaryAnalysis;
import java.util.List;
import java.util.Map;

/**
 * 薪酬分析服务接口
 */
public interface IHrSalaryAnalysisService extends IService<HrSalaryAnalysis> {

    /**
     * 创建薪酬分析
     */
    HrSalaryAnalysis createAnalysis(HrSalaryAnalysis analysis);

    /**
     * 删除薪酬分析
     */
    void deleteAnalysis(Long id);

    /**
     * 部门薪酬分析
     */
    HrSalaryAnalysis analyzeByDepartment(Long departmentId, Integer year, Integer month);

    /**
     * 岗位薪酬分析
     */
    HrSalaryAnalysis analyzeByPosition(Long positionId, Integer year, Integer month);

    /**
     * 个人薪酬分析
     */
    HrSalaryAnalysis analyzeByEmployee(Long employeeId, Integer year, Integer month);

    /**
     * 周期薪酬分析
     */
    HrSalaryAnalysis analyzeByPeriod(Integer startYear, Integer startMonth, Integer endYear, Integer endMonth);

    /**
     * 部门薪酬对比
     */
    List<Map<String, Object>> compareDepartments(Integer year, Integer month);

    /**
     * 岗位薪酬对比
     */
    List<Map<String, Object>> comparePositions(Integer year, Integer month);

    /**
     * 薪酬趋势分析
     */
    List<Map<String, Object>> salaryTrend(Long departmentId, Integer year);

    /**
     * 查询历史分析
     */
    List<HrSalaryAnalysis> getAnalysisHistory(String analysisType, Long departmentId);
}
