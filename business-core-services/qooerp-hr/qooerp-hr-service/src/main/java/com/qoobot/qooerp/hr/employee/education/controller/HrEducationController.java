package com.qoobot.qooerp.hr.employee.education.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.employee.education.domain.HrEducation;
import com.qoobot.qooerp.hr.employee.education.mapper.HrEducationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教育背景管理控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@RestController
@RequestMapping("/education")
@RequiredArgsConstructor
@Tag(name = "教育背景管理", description = "学历信息管理")
@Validated
public class HrEducationController {

    private final HrEducationMapper educationMapper;

    @Operation(summary = "创建教育背景")
    @PostMapping
    public Result<Long> createEducation(@Valid @RequestBody HrEducation education) {
        educationMapper.insert(education);
        return Result.success(education.getId());
    }

    @Operation(summary = "更新教育背景")
    @PutMapping
    public Result<Boolean> updateEducation(@Valid @RequestBody HrEducation education) {
        return Result.success(educationMapper.updateById(education) > 0);
    }

    @Operation(summary = "删除教育背景")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteEducation(@PathVariable Long id) {
        return Result.success(educationMapper.deleteById(id) > 0);
    }

    @Operation(summary = "获取教育背景详情")
    @GetMapping("/{id}")
    public Result<HrEducation> getEducation(@PathVariable Long id) {
        return Result.success(educationMapper.selectById(id));
    }

    @Operation(summary = "获取员工教育背景列表")
    @GetMapping("/employee/{employeeId}")
    public Result<List<HrEducation>> getEducationsByEmployee(@PathVariable Long employeeId) {
        LambdaQueryWrapper<HrEducation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrEducation::getEmployeeId, employeeId);
        wrapper.orderByDesc(HrEducation::getIsHighest);
        return Result.success(educationMapper.selectList(wrapper));
    }
}
