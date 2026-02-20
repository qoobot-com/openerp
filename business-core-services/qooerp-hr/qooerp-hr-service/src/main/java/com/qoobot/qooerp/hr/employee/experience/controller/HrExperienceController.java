package com.qoobot.qooerp.hr.employee.experience.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.employee.experience.domain.HrExperience;
import com.qoobot.qooerp.hr.employee.experience.mapper.HrExperienceMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作经历管理控制器
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@RestController
@RequestMapping("/experience")
@RequiredArgsConstructor
@Tag(name = "工作经历管理", description = "工作履历管理")
@Validated
public class HrExperienceController {

    private final HrExperienceMapper experienceMapper;

    @Operation(summary = "创建工作经历")
    @PostMapping
    public Result<Long> createExperience(@Valid @RequestBody HrExperience experience) {
        experienceMapper.insert(experience);
        return Result.success(experience.getId());
    }

    @Operation(summary = "更新工作经历")
    @PutMapping
    public Result<Boolean> updateExperience(@Valid @RequestBody HrExperience experience) {
        return Result.success(experienceMapper.updateById(experience) > 0);
    }

    @Operation(summary = "删除工作经历")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteExperience(@PathVariable Long id) {
        return Result.success(experienceMapper.deleteById(id) > 0);
    }

    @Operation(summary = "获取工作经历详情")
    @GetMapping("/{id}")
    public Result<HrExperience> getExperience(@PathVariable Long id) {
        return Result.success(experienceMapper.selectById(id));
    }

    @Operation(summary = "获取员工工作经历列表")
    @GetMapping("/employee/{employeeId}")
    public Result<List<HrExperience>> getExperiencesByEmployee(@PathVariable Long employeeId) {
        LambdaQueryWrapper<HrExperience> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrExperience::getEmployeeId, employeeId);
        wrapper.orderByDesc(HrExperience::getStartDate);
        return Result.success(experienceMapper.selectList(wrapper));
    }
}
