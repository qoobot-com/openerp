package com.qoobot.qooerp.hr.salary.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.salary.domain.HrSalaryStructure;
import com.qoobot.qooerp.hr.salary.service.IHrSalaryStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 薪酬结构控制器
 */
@RestController
@RequestMapping("/api/hr/salary-structure")
@RequiredArgsConstructor
public class HrSalaryStructureController {

    private final IHrSalaryStructureService salaryStructureService;

    @PostMapping("/create")
    public Result<HrSalaryStructure> create(@RequestBody HrSalaryStructure structure) {
        return salaryStructureService.createStructure(structure);
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody HrSalaryStructure structure) {
        return salaryStructureService.updateStructure(structure);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return salaryStructureService.deleteStructure(id);
    }

    @GetMapping("/get/{id}")
    public Result<HrSalaryStructure> get(@PathVariable Long id) {
        return salaryStructureService.getStructure(id);
    }

    @GetMapping("/list")
    public Result<IPage<HrSalaryStructure>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String structureName,
            @RequestParam(required = false) Boolean isEnabled) {
        return salaryStructureService.listStructures(new Page<>(current, size), structureName, isEnabled);
    }

    @GetMapping("/enabled")
    public Result<java.util.List<HrSalaryStructure>> listEnabled() {
        return salaryStructureService.listEnabledStructures();
    }

    @GetMapping("/by-department/{departmentId}")
    public Result<HrSalaryStructure> getByDepartment(@PathVariable Long departmentId) {
        return salaryStructureService.getStructureByDepartment(departmentId);
    }

    @GetMapping("/by-position/{positionId}")
    public Result<HrSalaryStructure> getByPosition(@PathVariable Long positionId) {
        return salaryStructureService.getStructureByPosition(positionId);
    }
}
