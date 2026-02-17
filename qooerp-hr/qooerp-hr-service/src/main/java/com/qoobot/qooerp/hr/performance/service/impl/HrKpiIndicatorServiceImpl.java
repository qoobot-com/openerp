package com.qoobot.qooerp.hr.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.performance.domain.HrKpiIndicator;
import com.qoobot.qooerp.hr.performance.mapper.HrKpiIndicatorMapper;
import com.qoobot.qooerp.hr.performance.service.IHrKpiIndicatorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * KPI指标服务实现
 */
@Service
public class HrKpiIndicatorServiceImpl extends ServiceImpl<HrKpiIndicatorMapper, HrKpiIndicator> 
        implements IHrKpiIndicatorService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<HrKpiIndicator> createIndicator(HrKpiIndicator indicator) {
        // 检查编码是否已存在
        LambdaQueryWrapper<HrKpiIndicator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrKpiIndicator::getIndicatorCode, indicator.getIndicatorCode());
        if (this.count(wrapper) > 0) {
            return Result.error("KPI指标编码已存在");
        }

        indicator.setCreateTime(LocalDateTime.now());
        indicator.setUpdateTime(LocalDateTime.now());
        boolean success = this.save(indicator);
        return success ? Result.success(indicator) : Result.error("创建失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateIndicator(HrKpiIndicator indicator) {
        HrKpiIndicator existing = this.getById(indicator.getId());
        if (existing == null) {
            return Result.error("KPI指标不存在");
        }

        indicator.setUpdateTime(LocalDateTime.now());
        boolean success = this.updateById(indicator);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteIndicator(Long id) {
        HrKpiIndicator indicator = this.getById(id);
        if (indicator == null) {
            return Result.error("KPI指标不存在");
        }

        boolean success = this.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    @Override
    public Result<HrKpiIndicator> getIndicator(Long id) {
        HrKpiIndicator indicator = this.getById(id);
        return indicator != null ? Result.success(indicator) : Result.error("KPI指标不存在");
    }

    @Override
    public Result<IPage<HrKpiIndicator>> listIndicators(IPage<?> page, Integer indicatorType, Boolean isEnabled) {
        LambdaQueryWrapper<HrKpiIndicator> wrapper = new LambdaQueryWrapper<>();
        if (indicatorType != null) {
            wrapper.eq(HrKpiIndicator::getIndicatorType, indicatorType);
        }
        if (isEnabled != null) {
            wrapper.eq(HrKpiIndicator::getIsEnabled, isEnabled);
        }
        wrapper.orderByDesc(HrKpiIndicator::getCreateTime);

        IPage<HrKpiIndicator> result = this.page((Page<HrKpiIndicator>) page, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<List<HrKpiIndicator>> listEnabledIndicators() {
        LambdaQueryWrapper<HrKpiIndicator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrKpiIndicator::getIsEnabled, true);
        wrapper.orderByDesc(HrKpiIndicator::getCreateTime);

        List<HrKpiIndicator> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<List<HrKpiIndicator>> listIndicatorsByPosition(Long positionId) {
        LambdaQueryWrapper<HrKpiIndicator> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.isNull(HrKpiIndicator::getPositionId).or().eq(HrKpiIndicator::getPositionId, positionId));
        wrapper.eq(HrKpiIndicator::getIsEnabled, true);
        wrapper.orderByDesc(HrKpiIndicator::getCreateTime);

        List<HrKpiIndicator> list = this.list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<List<HrKpiIndicator>> listIndicatorsByType(Integer indicatorType) {
        LambdaQueryWrapper<HrKpiIndicator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrKpiIndicator::getIndicatorType, indicatorType);
        wrapper.eq(HrKpiIndicator::getIsEnabled, true);
        wrapper.orderByDesc(HrKpiIndicator::getCreateTime);

        List<HrKpiIndicator> list = this.list(wrapper);
        return Result.success(list);
    }
}
