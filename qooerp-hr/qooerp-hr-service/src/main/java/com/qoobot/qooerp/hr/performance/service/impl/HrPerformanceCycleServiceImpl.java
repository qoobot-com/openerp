package com.qoobot.qooerp.hr.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.performance.domain.HrPerformanceCycle;
import com.qoobot.qooerp.hr.performance.mapper.HrPerformanceCycleMapper;
import com.qoobot.qooerp.hr.performance.service.IHrPerformanceCycleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 绩效周期服务实现
 */
@Service
public class HrPerformanceCycleServiceImpl extends ServiceImpl<HrPerformanceCycleMapper, HrPerformanceCycle> 
        implements IHrPerformanceCycleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<HrPerformanceCycle> createCycle(HrPerformanceCycle cycle) {
        // 检查编码是否已存在
        LambdaQueryWrapper<HrPerformanceCycle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrPerformanceCycle::getCycleCode, cycle.getCycleCode());
        if (this.count(wrapper) > 0) {
            return Result.error("绩效周期编码已存在");
        }

        cycle.setStatus(0); // 未开始
        cycle.setCreateTime(LocalDateTime.now());
        cycle.setUpdateTime(LocalDateTime.now());
        boolean success = this.save(cycle);
        return success ? Result.success(cycle) : Result.error("创建失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateCycle(HrPerformanceCycle cycle) {
        HrPerformanceCycle existing = this.getById(cycle.getId());
        if (existing == null) {
            return Result.error("绩效周期不存在");
        }

        cycle.setUpdateTime(LocalDateTime.now());
        boolean success = this.updateById(cycle);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteCycle(Long id) {
        HrPerformanceCycle cycle = this.getById(id);
        if (cycle == null) {
            return Result.error("绩效周期不存在");
        }
        if (cycle.getStatus() != 0) {
            return Result.error("周期已开始，无法删除");
        }

        boolean success = this.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> enableCycle(Long id) {
        HrPerformanceCycle cycle = this.getById(id);
        if (cycle == null) {
            return Result.error("绩效周期不存在");
        }

        cycle.setIsEnabled(true);
        cycle.setUpdateTime(LocalDateTime.now());
        boolean success = this.updateById(cycle);
        return success ? Result.success(true) : Result.error("启用失败");
    }

    @Override
    public Result<HrPerformanceCycle> getCycle(Long id) {
        HrPerformanceCycle cycle = this.getById(id);
        return cycle != null ? Result.success(cycle) : Result.error("绩效周期不存在");
    }

    @Override
    public Result<IPage<HrPerformanceCycle>> listCycles(IPage<?> page, Integer year, Integer cycleType) {
        LambdaQueryWrapper<HrPerformanceCycle> wrapper = new LambdaQueryWrapper<>();
        if (year != null) {
            wrapper.eq(HrPerformanceCycle::getYear, year);
        }
        if (cycleType != null) {
            wrapper.eq(HrPerformanceCycle::getCycleType, cycleType);
        }
        wrapper.orderByDesc(HrPerformanceCycle::getYear, HrPerformanceCycle::getStartMonth);

        IPage<HrPerformanceCycle> result = this.page((Page<HrPerformanceCycle>) page, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<List<HrPerformanceCycle>> listEnabledCycles() {
        LambdaQueryWrapper<HrPerformanceCycle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrPerformanceCycle::getIsEnabled, true);
        wrapper.orderByDesc(HrPerformanceCycle::getYear, HrPerformanceCycle::getStartMonth);

        List<HrPerformanceCycle> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<HrPerformanceCycle> getCurrentCycle() {
        LambdaQueryWrapper<HrPerformanceCycle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrPerformanceCycle::getIsEnabled, true);
        wrapper.le(HrPerformanceCycle::getStartDate, LocalDate.now());
        wrapper.ge(HrPerformanceCycle::getEndDate, LocalDate.now());
        wrapper.last("LIMIT 1");

        HrPerformanceCycle cycle = this.getOne(wrapper);
        return Result.success(cycle);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> startCycle(Long id) {
        HrPerformanceCycle cycle = this.getById(id);
        if (cycle == null) {
            return Result.error("绩效周期不存在");
        }
        if (cycle.getStatus() != 0) {
            return Result.error("周期已开始");
        }

        cycle.setStatus(1); // 自评中
        cycle.setUpdateTime(LocalDateTime.now());
        boolean success = this.updateById(cycle);
        return success ? Result.success(true) : Result.error("启动失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> endCycle(Long id) {
        HrPerformanceCycle cycle = this.getById(id);
        if (cycle == null) {
            return Result.error("绩效周期不存在");
        }

        cycle.setStatus(3); // 已完成
        cycle.setUpdateTime(LocalDateTime.now());
        boolean success = this.updateById(cycle);
        return success ? Result.success(true) : Result.error("结束失败");
    }
}
