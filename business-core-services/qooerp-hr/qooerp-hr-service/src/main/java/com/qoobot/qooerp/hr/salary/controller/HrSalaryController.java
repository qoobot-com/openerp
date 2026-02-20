package com.qoobot.qooerp.hr.salary.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.salary.domain.HrSalary;
import com.qoobot.qooerp.hr.salary.service.IHrSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 薪资发放控制器
 */
@RestController
@RequestMapping("/api/hr/salary")
@RequiredArgsConstructor
public class HrSalaryController {

    private final IHrSalaryService salaryService;

    @PostMapping("/calculate")
    public Result<HrSalary> calculate(@RequestParam Long employeeId, @RequestParam String salaryMonth) {
        return salaryService.calculateSalary(employeeId, salaryMonth);
    }

    @PostMapping("/batch-calculate")
    public Result<List<HrSalary>> batchCalculate(@RequestParam Long departmentId, @RequestParam String salaryMonth) {
        return salaryService.batchCalculateSalary(departmentId, salaryMonth);
    }

    @PostMapping("/pay/{salaryId}")
    public Result<Boolean> pay(@PathVariable Long salaryId) {
        return salaryService.paySalary(salaryId);
    }

    @PostMapping("/batch-pay")
    public Result<Boolean> batchPay(@RequestBody List<Long> salaryIds) {
        return salaryService.batchPaySalary(salaryIds);
    }

    @PostMapping("/cancel/{salaryId}")
    public Result<Boolean> cancel(@PathVariable Long salaryId) {
        return salaryService.cancelSalary(salaryId);
    }

    @GetMapping("/get/{id}")
    public Result<HrSalary> get(@PathVariable Long id) {
        return salaryService.getSalary(id);
    }

    @GetMapping("/by-employee-month")
    public Result<HrSalary> getByEmployeeAndMonth(@RequestParam Long employeeId, @RequestParam String salaryMonth) {
        return salaryService.getSalaryByEmployeeAndMonth(employeeId, salaryMonth);
    }

    @GetMapping("/employee-history")
    public Result<IPage<HrSalary>> listEmployeeHistory(
            @RequestParam Long employeeId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return salaryService.listEmployeeSalaries(new Page<>(current, size), employeeId);
    }

    @GetMapping("/monthly-list")
    public Result<IPage<HrSalary>> listMonthly(
            @RequestParam(required = false) String salaryMonth,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return salaryService.listMonthlySalaries(new Page<>(current, size), salaryMonth);
    }
}
