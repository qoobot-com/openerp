package com.qoobot.qooerp.hr.salary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.salary.domain.HrSalaryStructure;
import com.qoobot.qooerp.hr.salary.mapper.HrSalaryStructureMapper;
import com.qoobot.qooerp.hr.salary.service.IHrSalaryStructureService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 薪酬结构服务实现
 */
@Service
public class HrSalaryStructureServiceImpl extends ServiceImpl<HrSalaryStructureMapper, HrSalaryStructure> 
        implements IHrSalaryStructureService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<HrSalaryStructure> createStructure(HrSalaryStructure structure) {
        // 检查编码是否已存在
        LambdaQueryWrapper<HrSalaryStructure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSalaryStructure::getStructureCode, structure.getStructureCode());
        if (this.count(wrapper) > 0) {
            return Result.error("薪酬方案编码已存在");
        }

        structure.setCreateTime(LocalDateTime.now());
        structure.setUpdateTime(LocalDateTime.now());
        boolean success = this.save(structure);
        return success ? Result.success(structure) : Result.error("创建失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateStructure(HrSalaryStructure structure) {
        HrSalaryStructure existing = this.getById(structure.getId());
        if (existing == null) {
            return Result.error("薪酬方案不存在");
        }

        structure.setUpdateTime(LocalDateTime.now());
        boolean success = this.updateById(structure);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteStructure(Long id) {
        HrSalaryStructure structure = this.getById(id);
        if (structure == null) {
            return Result.error("薪酬方案不存在");
        }

        boolean success = this.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    @Override
    public Result<HrSalaryStructure> getStructure(Long id) {
        HrSalaryStructure structure = this.getById(id);
        return structure != null ? Result.success(structure) : Result.error("薪酬方案不存在");
    }

    @Override
    public Result<IPage<HrSalaryStructure>> listStructures(IPage<?> page, String structureName, Boolean isEnabled) {
        LambdaQueryWrapper<HrSalaryStructure> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(structureName)) {
            wrapper.like(HrSalaryStructure::getStructureName, structureName);
        }
        if (isEnabled != null) {
            wrapper.eq(HrSalaryStructure::getIsEnabled, isEnabled);
        }
        wrapper.orderByDesc(HrSalaryStructure::getCreateTime);

        IPage<HrSalaryStructure> result = this.page((Page<HrSalaryStructure>) page, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<List<HrSalaryStructure>> listEnabledStructures() {
        LambdaQueryWrapper<HrSalaryStructure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSalaryStructure::getIsEnabled, true);
        wrapper.orderByDesc(HrSalaryStructure::getCreateTime);

        List<HrSalaryStructure> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<HrSalaryStructure> getStructureByDepartment(Long departmentId) {
        LambdaQueryWrapper<HrSalaryStructure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSalaryStructure::getDepartmentId, departmentId);
        wrapper.eq(HrSalaryStructure::getIsEnabled, true);
        wrapper.orderByDesc(HrSalaryStructure::getCreateTime);
        wrapper.last("LIMIT 1");

        HrSalaryStructure structure = this.getOne(wrapper);
        return Result.success(structure);
    }

    @Override
    public Result<HrSalaryStructure> getStructureByPosition(Long positionId) {
        LambdaQueryWrapper<HrSalaryStructure> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrSalaryStructure::getPositionId, positionId);
        wrapper.eq(HrSalaryStructure::getIsEnabled, true);
        wrapper.orderByDesc(HrSalaryStructure::getCreateTime);
        wrapper.last("LIMIT 1");

        HrSalaryStructure structure = this.getOne(wrapper);
        return Result.success(structure);
    }
}
