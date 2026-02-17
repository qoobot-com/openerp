package com.qoobot.qooerp.hr.salary.analysis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.salary.analysis.domain.HrSalaryAnalysis;
import com.qoobot.qooerp.hr.salary.analysis.mapper.HrSalaryAnalysisMapper;
import com.qoobot.qooerp.hr.salary.analysis.service.IHrSalaryAnalysisService;
import com.qoobot.qooerp.hr.salary.domain.HrSalary;
import com.qoobot.qooerp.hr.salary.mapper.HrSalaryMapper;
import com.qoobot.qooerp.hr.employee.domain.HrEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 薪酬分析服务实现
 */
@Service
public class HrSalaryAnalysisServiceImpl extends ServiceImpl<HrSalaryAnalysisMapper, HrSalaryAnalysis> 
    implements IHrSalaryAnalysisService {

    @Autowired
    private HrSalaryMapper salaryMapper;

    @Override
    public HrSalaryAnalysis createAnalysis(HrSalaryAnalysis analysis) {
        analysis.setAnalysisDate(LocalDate.now());
        analysis.setCreateTime(java.time.LocalDateTime.now());
        save(analysis);
        return analysis;
    }

    @Override
    public void deleteAnalysis(Long id) {
        removeById(id);
    }

    @Override
    public HrSalaryAnalysis analyzeByDepartment(Long departmentId, Integer year, Integer month) {
        String salaryMonth = String.format("%d-%02d", year, month);
        List<HrSalary> salaries = salaryMapper.selectList(null).stream()
            .filter(s -> salaryMonth.equals(s.getSalaryMonth()))
            .collect(Collectors.toList());

        // TODO: 关联员工表，根据部门ID过滤

        return calculateStatistics(salaries, "DEPARTMENT", departmentId, null, null, year, month);
    }

    @Override
    public HrSalaryAnalysis analyzeByPosition(Long positionId, Integer year, Integer month) {
        String salaryMonth = String.format("%d-%02d", year, month);
        List<HrSalary> salaries = salaryMapper.selectList(null).stream()
            .filter(s -> salaryMonth.equals(s.getSalaryMonth()))
            .collect(Collectors.toList());

        // TODO: 关联员工表，根据岗位ID过滤

        return calculateStatistics(salaries, "POSITION", null, positionId, null, year, month);
    }

    @Override
    public HrSalaryAnalysis analyzeByEmployee(Long employeeId, Integer year, Integer month) {
        String salaryMonth = String.format("%d-%02d", year, month);
        HrSalary salary = salaryMapper.selectList(null).stream()
            .filter(s -> s.getEmployeeId().equals(employeeId) &&
                        salaryMonth.equals(s.getSalaryMonth()))
            .findFirst().orElse(null);

        HrSalaryAnalysis analysis = new HrSalaryAnalysis();
        if (salary != null) {
            analysis.setMinSalary(salary.getNetSalary());
            analysis.setMaxSalary(salary.getNetSalary());
            analysis.setAvgSalary(salary.getNetSalary());
            analysis.setTotalSalary(salary.getGrossSalary());
            analysis.setTotalBaseSalary(salary.getBaseSalary());
            analysis.setTotalPerformanceSalary(salary.getPerformanceSalary());
            analysis.setTotalBonus(salary.getBonus());
            analysis.setTotalSocialSecurity(salary.getSocialPersonal());
            analysis.setTotalTax(salary.getPersonalTax());
            analysis.setTotalNetSalary(salary.getNetSalary());
            analysis.setHeadcount(1);
        }

        analysis.setAnalysisType("INDIVIDUAL");
        analysis.setEmployeeId(employeeId);
        analysis.setYear(year);
        analysis.setMonth(month);
        analysis.setAnalysisDate(LocalDate.now());
        return analysis;
    }

    @Override
    public HrSalaryAnalysis analyzeByPeriod(Integer startYear, Integer startMonth, Integer endYear, Integer endMonth) {
        List<HrSalary> salaries = salaryMapper.selectList(null).stream()
            .filter(s -> isInPeriod(s.getSalaryMonth(), startYear, startMonth, endYear, endMonth))
            .collect(Collectors.toList());

        return calculateStatistics(salaries, "PERIOD", null, null, null, startYear, startMonth);
    }

    @Override
    public List<Map<String, Object>> compareDepartments(Integer year, Integer month) {
        // TODO: 实现部门薪酬对比
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> comparePositions(Integer year, Integer month) {
        // TODO: 实现岗位薪酬对比
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> salaryTrend(Long departmentId, Integer year) {
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Map<String, Object> data = new HashMap<>();
            data.put("month", month);
            data.put("avgSalary", BigDecimal.ZERO);
            trend.add(data);
        }
        return trend;
    }

    @Override
    public List<HrSalaryAnalysis> getAnalysisHistory(String analysisType, Long departmentId) {
        return lambdaQuery()
            .eq(HrSalaryAnalysis::getAnalysisType, analysisType)
            .eq(departmentId != null, HrSalaryAnalysis::getDepartmentId, departmentId)
            .orderByDesc(HrSalaryAnalysis::getCreateTime)
            .list();
    }

    private HrSalaryAnalysis calculateStatistics(List<HrSalary> salaries, String type, 
            Long departmentId, Long positionId, Long employeeId, Integer year, Integer month) {
        HrSalaryAnalysis analysis = new HrSalaryAnalysis();
        
        if (salaries.isEmpty()) {
            return analysis;
        }

        List<BigDecimal> netSalaries = salaries.stream()
            .map(HrSalary::getNetSalary)
            .sorted()
            .collect(Collectors.toList());

        BigDecimal min = netSalaries.get(0);
        BigDecimal max = netSalaries.get(netSalaries.size() - 1);
        BigDecimal avg = netSalaries.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(netSalaries.size()), 2, RoundingMode.HALF_UP);
        
        // 计算中位数
        BigDecimal median = netSalaries.size() % 2 == 0 
            ? netSalaries.get(netSalaries.size() / 2 - 1).add(netSalaries.get(netSalaries.size() / 2))
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP)
            : netSalaries.get(netSalaries.size() / 2);

        analysis.setAnalysisType(type);
        analysis.setDepartmentId(departmentId);
        analysis.setPositionId(positionId);
        analysis.setEmployeeId(employeeId);
        analysis.setYear(year);
        analysis.setMonth(month);
        analysis.setMinSalary(min);
        analysis.setMaxSalary(max);
        analysis.setAvgSalary(avg);
        analysis.setMedianSalary(median);
        analysis.setTotalSalary(salaries.stream().map(HrSalary::getGrossSalary).reduce(BigDecimal.ZERO, BigDecimal::add));
        analysis.setTotalBaseSalary(salaries.stream().map(HrSalary::getBaseSalary).reduce(BigDecimal.ZERO, BigDecimal::add));
        analysis.setTotalPerformanceSalary(salaries.stream().map(HrSalary::getPerformanceSalary).reduce(BigDecimal.ZERO, BigDecimal::add));
        analysis.setTotalBonus(salaries.stream().map(HrSalary::getBonus).reduce(BigDecimal.ZERO, BigDecimal::add));
        analysis.setTotalSocialSecurity(salaries.stream().map(HrSalary::getSocialPersonal).reduce(BigDecimal.ZERO, BigDecimal::add));
        analysis.setTotalTax(salaries.stream().map(HrSalary::getPersonalTax).reduce(BigDecimal.ZERO, BigDecimal::add));
        analysis.setTotalNetSalary(salaries.stream().map(HrSalary::getNetSalary).reduce(BigDecimal.ZERO, BigDecimal::add));
        analysis.setHeadcount(salaries.size());
        analysis.setAnalysisDate(LocalDate.now());

        return analysis;
    }

    private boolean isInPeriod(String salaryMonth, Integer startYear, Integer startMonth,
            Integer endYear, Integer endMonth) {
        String[] parts = salaryMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        if (year < startYear || year > endYear) return false;
        if (year == startYear && month < startMonth) return false;
        if (year == endYear && month > endMonth) return false;
        return true;
    }
}
