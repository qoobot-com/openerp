package com.qoobot.qooerp.hr.employee.family.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.employee.family.domain.HrFamily;
import com.qoobot.qooerp.hr.employee.family.mapper.HrFamilyMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家庭信息管理控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@RestController
@RequestMapping("/family")
@RequiredArgsConstructor
@Tag(name = "家庭信息管理", description = "家庭成员管理")
@Validated
public class HrFamilyController {

    private final HrFamilyMapper familyMapper;

    @Operation(summary = "创建家庭信息")
    @PostMapping
    public Result<Long> createFamily(@Valid @RequestBody HrFamily family) {
        familyMapper.insert(family);
        return Result.success(family.getId());
    }

    @Operation(summary = "更新家庭信息")
    @PutMapping
    public Result<Boolean> updateFamily(@Valid @RequestBody HrFamily family) {
        return Result.success(familyMapper.updateById(family) > 0);
    }

    @Operation(summary = "删除家庭信息")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteFamily(@PathVariable Long id) {
        return Result.success(familyMapper.deleteById(id) > 0);
    }

    @Operation(summary = "获取家庭信息详情")
    @GetMapping("/{id}")
    public Result<HrFamily> getFamily(@PathVariable Long id) {
        return Result.success(familyMapper.selectById(id));
    }

    @Operation(summary = "获取员工家庭成员列表")
    @GetMapping("/employee/{employeeId}")
    public Result<List<HrFamily>> getFamiliesByEmployee(@PathVariable Long employeeId) {
        LambdaQueryWrapper<HrFamily> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrFamily::getEmployeeId, employeeId);
        return Result.success(familyMapper.selectList(wrapper));
    }
}
