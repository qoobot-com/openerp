package com.qoobot.qooerp.hr.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.employee.domain.HrEmployee;
import com.qoobot.qooerp.hr.employee.service.IHrEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 员工管理控制器
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Tag(name = "员工管理", description = "员工信息管理")
@Validated
public class HrEmployeeController {

    private final IHrEmployeeService employeeService;

    @Operation(summary = "查询员工详情")
    @GetMapping("/{id}")
    public Result<HrEmployee> getEmployeeById(@PathVariable Long id) {
        HrEmployee employee = employeeService.getEmployeeById(id);
        return Result.success(employee);
    }

    @Operation(summary = "分页查询员工列表")
    @GetMapping("/page")
    public Result<IPage<HrEmployee>> getEmployeePage(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String departmentId) {
        // TODO: 实现查询条件过滤
        IPage<HrEmployee> page = employeeService.getEmployeePage(current, size);
        return Result.success(page);
    }

    @Operation(summary = "创建员工")
    @PostMapping
    public Result<Void> createEmployee(@Validated @RequestBody HrEmployee employee) {
        boolean result = employeeService.createEmployee(employee);
        return result ? Result.success() : Result.error("创建员工失败");
    }

    @Operation(summary = "更新员工")
    @PutMapping
    public Result<Void> updateEmployee(@Validated @RequestBody HrEmployee employee) {
        boolean result = employeeService.updateEmployee(employee);
        return result ? Result.success() : Result.error("更新员工失败");
    }

    @Operation(summary = "删除员工")
    @DeleteMapping("/{id}")
    public Result<Void> deleteEmployee(@PathVariable Long id) {
        boolean result = employeeService.deleteEmployee(id);
        return result ? Result.success() : Result.error("删除员工失败");
    }

    @Operation(summary = "员工转正")
    @PutMapping("/{employeeId}/regular")
    public Result<Void> becomeRegular(
            @PathVariable Long employeeId,
            @RequestBody LocalDate regularDate) {
        boolean result = employeeService.becomeRegular(employeeId, regularDate);
        return result ? Result.success() : Result.error("转正失败");
    }

    @Operation(summary = "员工离职")
    @PutMapping("/{employeeId}/resign")
    public Result<Void> resign(
            @PathVariable Long employeeId,
            @RequestBody LocalDate resignationDate) {
        boolean result = employeeService.resign(employeeId, resignationDate);
        return result ? Result.success() : Result.error("离职处理失败");
    }
}
