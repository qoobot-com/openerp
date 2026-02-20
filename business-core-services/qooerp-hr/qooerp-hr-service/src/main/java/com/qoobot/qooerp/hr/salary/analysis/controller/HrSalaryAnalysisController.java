package com.qoobot.qooerp.hr.salary.analysis.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.salary.analysis.domain.HrSalaryAnalysis;
import com.qoobot.qooerp.hr.salary.analysis.service.IHrSalaryAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 薪酬分析控制器
 */
@RestController
@RequestMapping("/hr/salary/analysis")
public class HrSalaryAnalysisController {

    @Autowired
    private IHrSalaryAnalysisService analysisService;

    /**
     * 创建薪酬分析
     */
    @PostMapping("/create")
    public Result<HrSalaryAnalysis> create(@RequestBody HrSalaryAnalysis analysis) {
        return Result.success(analysisService.createAnalysis(analysis));
    }

    /**
     * 删除薪酬分析
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        analysisService.deleteAnalysis(id);
        return Result.success();
    }

    /**
     * 查询分析详情
     */
    @GetMapping("/detail/{id}")
    public Result<HrSalaryAnalysis> detail(@PathVariable Long id) {
        return Result.success(analysisService.getById(id));
    }

    /**
     * 部门薪酬分析
     */
    @PostMapping("/byDepartment")
    public Result<HrSalaryAnalysis> analyzeByDepartment(
            @RequestParam Long departmentId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return Result.success(analysisService.analyzeByDepartment(departmentId, year, month));
    }

    /**
     * 岗位薪酬分析
     */
    @PostMapping("/byPosition")
    public Result<HrSalaryAnalysis> analyzeByPosition(
            @RequestParam Long positionId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return Result.success(analysisService.analyzeByPosition(positionId, year, month));
    }

    /**
     * 个人薪酬分析
     */
    @PostMapping("/byEmployee")
    public Result<HrSalaryAnalysis> analyzeByEmployee(
            @RequestParam Long employeeId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return Result.success(analysisService.analyzeByEmployee(employeeId, year, month));
    }

    /**
     * 周期薪酬分析
     */
    @PostMapping("/byPeriod")
    public Result<HrSalaryAnalysis> analyzeByPeriod(
            @RequestParam Integer startYear,
            @RequestParam Integer startMonth,
            @RequestParam Integer endYear,
            @RequestParam Integer endMonth) {
        return Result.success(analysisService.analyzeByPeriod(startYear, startMonth, endYear, endMonth));
    }

    /**
     * 部门薪酬对比
     */
    @GetMapping("/compareDepartments")
    public Result<List<Map<String, Object>>> compareDepartments(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return Result.success(analysisService.compareDepartments(year, month));
    }

    /**
     * 岗位薪酬对比
     */
    @GetMapping("/comparePositions")
    public Result<List<Map<String, Object>>> comparePositions(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return Result.success(analysisService.comparePositions(year, month));
    }

    /**
     * 薪酬趋势分析
     */
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> salaryTrend(
            @RequestParam Long departmentId,
            @RequestParam Integer year) {
        return Result.success(analysisService.salaryTrend(departmentId, year));
    }

    /**
     * 查询历史分析
     */
    @GetMapping("/history")
    public Result<List<HrSalaryAnalysis>> history(
            @RequestParam String analysisType,
            @RequestParam(required = false) Long departmentId) {
        return Result.success(analysisService.getAnalysisHistory(analysisType, departmentId));
    }
}
