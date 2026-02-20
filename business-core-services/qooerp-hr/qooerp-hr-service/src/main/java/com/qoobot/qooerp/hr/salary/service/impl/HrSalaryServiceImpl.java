package com.qoobot.qooerp.hr.salary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.attendance.summary.mapper.HrAttendanceSummaryMapper;
import com.qoobot.qooerp.hr.employee.domain.HrEmployee;
import com.qoobot.qooerp.hr.employee.mapper.HrEmployeeMapper;
import com.qoobot.qooerp.hr.salary.domain.HrSalary;
import com.qoobot.qooerp.hr.salary.domain.HrSalaryStructure;
import com.qoobot.qooerp.hr.salary.mapper.HrSalaryMapper;
import com.qoobot.qooerp.hr.salary.service.IHrSalaryService;
import com.qoobot.qooerp.hr.salary.service.IHrSalaryStructureService;
import com.qoobot.qooerp.hr.attendance.summary.domain.HrAttendanceSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 薪资发放服务实现
 */
@Service
@RequiredArgsConstructor
public class HrSalaryServiceImpl extends ServiceImpl<HrSalaryMapper, HrSalary> 
        implements IHrSalaryService {

    private final HrEmployeeMapper employeeMapper;
    private final IHrSalaryStructureService salaryStructureService;
    private final HrAttendanceSummaryMapper attendanceSummaryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<HrSalary> calculateSalary(Long employeeId, String salaryMonth) {
        // 查询员工信息
        HrEmployee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            return Result.error("员工不存在");
        }

        // 获取薪酬方案
        Result<HrSalaryStructure> structureResult = salaryStructureService.getStructureByDepartment(employee.getDepartmentId());
        if (structureResult.getData() == null) {
            Result<List<HrSalaryStructure>> listResult = salaryStructureService.listEnabledStructures();
            if (listResult.getData() != null && !listResult.getData().isEmpty()) {
                structureResult.setData(listResult.getData().get(0));
            }
        }

        HrSalaryStructure structure = structureResult.getData() != null ? structureResult.getData() : new HrSalaryStructure();

        // 查询考勤汇总
        LambdaQueryWrapper<HrAttendanceSummary> summaryWrapper = new LambdaQueryWrapper<>();
        summaryWrapper.eq(HrAttendanceSummary::getEmployeeId, employeeId);
        summaryWrapper.eq(HrAttendanceSummary::getSummaryMonth, salaryMonth);
        HrAttendanceSummary summary = attendanceSummaryMapper.selectOne(summaryWrapper);

        // 创建薪资记录
        HrSalary salary = new HrSalary();
        salary.setEmployeeId(employeeId);
        salary.setSalaryNo("SAL" + System.currentTimeMillis() + (int)(Math.random() * 1000));
        salary.setSalaryMonth(salaryMonth);
        salary.setStructureId(structure.getId());

        // 基础工资
        salary.setBaseSalary(structure.getBaseSalary() != null ? structure.getBaseSalary() : BigDecimal.ZERO);
        salary.setPostSalary(structure.getPostSalary() != null ? structure.getPostSalary() : BigDecimal.ZERO);
        salary.setPerformanceSalary(structure.getPerformanceSalary() != null ? structure.getPerformanceSalary() : BigDecimal.ZERO);
        salary.setSenioritySalary(structure.getSenioritySalary() != null ? structure.getSenioritySalary() : BigDecimal.ZERO);

        // 补贴
        salary.setPostAllowance(structure.getPostAllowance() != null ? structure.getPostAllowance() : BigDecimal.ZERO);
        salary.setTransportAllowance(structure.getTransportAllowance() != null ? structure.getTransportAllowance() : BigDecimal.ZERO);
        salary.setCommunicationAllowance(structure.getCommunicationAllowance() != null ? structure.getCommunicationAllowance() : BigDecimal.ZERO);
        salary.setMealAllowance(structure.getMealAllowance() != null ? structure.getMealAllowance() : BigDecimal.ZERO);
        salary.setHousingAllowance(structure.getHousingAllowance() != null ? structure.getHousingAllowance() : BigDecimal.ZERO);
        salary.setOtherAllowance(structure.getOtherAllowance() != null ? structure.getOtherAllowance() : BigDecimal.ZERO);

        // 考勤数据
        if (summary != null) {
            salary.setWorkDays(22);
            salary.setAttendanceDays(summary.getAttendanceDays() != null ? summary.getAttendanceDays() : 0);
            salary.setLeaveDays(summary.getLeaveDays() != null ? summary.getLeaveDays() : BigDecimal.ZERO);
            salary.setOvertimeHours(summary.getOvertimeHours() != null ? summary.getOvertimeHours() : BigDecimal.ZERO);
            if (summary.getOvertimeHours() != null && structure.getBaseSalary() != null) {
                salary.setOvertimePay(summary.getOvertimeHours().multiply(structure.getBaseSalary().divide(BigDecimal.valueOf(22), 2, RoundingMode.HALF_UP)).divide(BigDecimal.valueOf(8), 2, RoundingMode.HALF_UP));
            } else {
                salary.setOvertimePay(BigDecimal.ZERO);
            }
        } else {
            salary.setWorkDays(22);
            salary.setAttendanceDays(22);
            salary.setLeaveDays(BigDecimal.ZERO);
            salary.setOvertimeHours(BigDecimal.ZERO);
            salary.setOvertimePay(BigDecimal.ZERO);
        }

        // 计算津贴合计
        BigDecimal totalAllowance = salary.getPostAllowance()
                .add(salary.getTransportAllowance())
                .add(salary.getCommunicationAllowance())
                .add(salary.getMealAllowance())
                .add(salary.getHousingAllowance())
                .add(salary.getOtherAllowance());
        salary.setTotalAllowance(totalAllowance);

        // 应发工资
        BigDecimal grossSalary = salary.getBaseSalary()
                .add(salary.getPostSalary())
                .add(salary.getPerformanceSalary())
                .add(salary.getSenioritySalary())
                .add(totalAllowance)
                .add(salary.getOvertimePay());
        salary.setGrossSalary(grossSalary);

        // 社保和公积金
        BigDecimal socialPersonal = grossSalary.multiply(structure.getSocialPersonalRate() != null ? structure.getSocialPersonalRate() : BigDecimal.ZERO);
        BigDecimal fundPersonal = grossSalary.multiply(structure.getFundPersonalRate() != null ? structure.getFundPersonalRate() : BigDecimal.ZERO);
        salary.setSocialPersonal(socialPersonal);
        salary.setFundPersonal(fundPersonal);

        // 个人所得税
        BigDecimal taxableIncome = grossSalary.subtract(socialPersonal).subtract(fundPersonal).subtract(BigDecimal.valueOf(5000));
        BigDecimal personalTax = calculatePersonalTax(taxableIncome);
        salary.setPersonalTax(personalTax);

        // 考勤扣款
        BigDecimal attendanceDeduction = BigDecimal.ZERO;
        if (salary.getAttendanceDays() < salary.getWorkDays()) {
            BigDecimal dailySalary = salary.getBaseSalary().divide(BigDecimal.valueOf(salary.getWorkDays()), 2, RoundingMode.HALF_UP);
            attendanceDeduction = dailySalary.multiply(BigDecimal.valueOf(salary.getWorkDays() - salary.getAttendanceDays()));
        }
        salary.setAttendanceDeduction(attendanceDeduction);
        salary.setOtherDeduction(BigDecimal.ZERO);

        // 扣款合计
        BigDecimal totalDeduction = socialPersonal.add(fundPersonal).add(personalTax).add(attendanceDeduction);
        salary.setTotalDeduction(totalDeduction);

        // 实发工资
        salary.setNetSalary(grossSalary.subtract(totalDeduction));
        salary.setBonus(BigDecimal.ZERO);
        salary.setStatus(1); // 已核算
        salary.setCreateTime(LocalDateTime.now());
        salary.setUpdateTime(LocalDateTime.now());

        this.saveOrUpdate(salary, new LambdaQueryWrapper<HrSalary>()
                .eq(HrSalary::getEmployeeId, employeeId)
                .eq(HrSalary::getSalaryMonth, salaryMonth));

        return Result.success(salary);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<HrSalary>> batchCalculateSalary(Long departmentId, String salaryMonth) {
        // TODO: 实现批量计算
        return Result.success(List.of());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> paySalary(Long salaryId) {
        HrSalary salary = this.getById(salaryId);
        if (salary == null) {
            return Result.error("薪资记录不存在");
        }
        if (salary.getStatus() != 1) {
            return Result.error("薪资记录未核算，无法发放");
        }

        salary.setStatus(2); // 已发放
        salary.setPayDate(LocalDate.now());
        salary.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(salary);
        return success ? Result.success(true) : Result.error("发放失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> batchPaySalary(List<Long> salaryIds) {
        for (Long id : salaryIds) {
            Result<Boolean> result = paySalary(id);
            if (!result.isSuccess()) {
                return result;
            }
        }
        return Result.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> cancelSalary(Long salaryId) {
        HrSalary salary = this.getById(salaryId);
        if (salary == null) {
            return Result.error("薪资记录不存在");
        }
        if (salary.getStatus() == 2) {
            return Result.error("薪资已发放，无法作废");
        }

        salary.setStatus(3); // 已作废
        salary.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(salary);
        return success ? Result.success(true) : Result.error("作废失败");
    }

    @Override
    public Result<HrSalary> getSalary(Long id) {
        HrSalary salary = this.getById(id);
        return salary != null ? Result.success(salary) : Result.error("薪资记录不存在");
    }

    @Override
    public Result<HrSalary> getSalaryByEmployeeAndMonth(Long employeeId, String salaryMonth) {
        LambdaQueryWrapper<HrSalary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSalary::getEmployeeId, employeeId);
        wrapper.eq(HrSalary::getSalaryMonth, salaryMonth);
        HrSalary salary = this.getOne(wrapper);
        return Result.success(salary);
    }

    @Override
    public Result<IPage<HrSalary>> listEmployeeSalaries(IPage<?> page, Long employeeId) {
        LambdaQueryWrapper<HrSalary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSalary::getEmployeeId, employeeId);
        wrapper.orderByDesc(HrSalary::getSalaryMonth);

        IPage<HrSalary> result = this.page((Page<HrSalary>) page, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<IPage<HrSalary>> listMonthlySalaries(IPage<?> page, String salaryMonth) {
        LambdaQueryWrapper<HrSalary> wrapper = new LambdaQueryWrapper<>();
        if (salaryMonth != null) {
            wrapper.eq(HrSalary::getSalaryMonth, salaryMonth);
        }
        wrapper.orderByDesc(HrSalary::getSalaryMonth);

        IPage<HrSalary> result = this.page((Page<HrSalary>) page, wrapper);
        return Result.success(result);
    }

    @Override
    public BigDecimal calculatePersonalTax(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 简化版个税计算（七级累进税率）
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal[] brackets = {BigDecimal.valueOf(3000), BigDecimal.valueOf(9000), BigDecimal.valueOf(20000), 
                                  BigDecimal.valueOf(40000), BigDecimal.valueOf(60000), BigDecimal.valueOf(80000)};
        BigDecimal[] rates = {BigDecimal.valueOf(0.03), BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.20),
                             BigDecimal.valueOf(0.25), BigDecimal.valueOf(0.30), BigDecimal.valueOf(0.35), BigDecimal.valueOf(0.45)};
        BigDecimal[] quickDeductions = {BigDecimal.valueOf(0), BigDecimal.valueOf(210), BigDecimal.valueOf(1410),
                                       BigDecimal.valueOf(2660), BigDecimal.valueOf(4410), BigDecimal.valueOf(7160), BigDecimal.valueOf(15160)};

        BigDecimal remaining = taxableIncome;
        for (int i = 0; i < brackets.length; i++) {
            if (remaining.compareTo(brackets[i]) <= 0) {
                tax = tax.add(remaining.multiply(rates[i]).subtract(quickDeductions[i]));
                break;
            } else {
                tax = tax.add(brackets[i].multiply(rates[i]).subtract(quickDeductions[i]));
                remaining = remaining.subtract(brackets[i]);
            }
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            tax = tax.add(remaining.multiply(rates[6]).subtract(quickDeductions[6]));
        }

        return tax.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}
