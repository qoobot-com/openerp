package com.qoobot.qooerp.hr.employee.skill.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.employee.skill.domain.HrSkill;
import com.qoobot.qooerp.hr.employee.skill.mapper.HrSkillMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工技能管理控制器
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
@Tag(name = "员工技能管理", description = "技能证书管理")
@Validated
public class HrSkillController {

    private final HrSkillMapper skillMapper;

    @Operation(summary = "创建员工技能")
    @PostMapping
    public Result<Long> createSkill(@Valid @RequestBody HrSkill skill) {
        skillMapper.insert(skill);
        return Result.success(skill.getId());
    }

    @Operation(summary = "更新员工技能")
    @PutMapping
    public Result<Boolean> updateSkill(@Valid @RequestBody HrSkill skill) {
        return Result.success(skillMapper.updateById(skill) > 0);
    }

    @Operation(summary = "删除员工技能")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteSkill(@PathVariable Long id) {
        return Result.success(skillMapper.deleteById(id) > 0);
    }

    @Operation(summary = "获取技能详情")
    @GetMapping("/{id}")
    public Result<HrSkill> getSkill(@PathVariable Long id) {
        return Result.success(skillMapper.selectById(id));
    }

    @Operation(summary = "获取员工技能列表")
    @GetMapping("/employee/{employeeId}")
    public Result<List<HrSkill>> getSkillsByEmployee(@PathVariable Long employeeId) {
        LambdaQueryWrapper<HrSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSkill::getEmployeeId, employeeId);
        wrapper.orderByDesc(HrSkill::getProficiency);
        return Result.success(skillMapper.selectList(wrapper));
    }
}
