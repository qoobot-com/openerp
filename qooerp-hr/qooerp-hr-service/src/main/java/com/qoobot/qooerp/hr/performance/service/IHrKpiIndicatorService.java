package com.qoobot.qooerp.hr.performance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.performance.domain.HrKpiIndicator;
import com.qoobot.qooerp.common.result.Result;

import java.util.List;

/**
 * KPI指标服务接口
 */
public interface IHrKpiIndicatorService extends IService<HrKpiIndicator> {

    /**
     * 创建KPI指标
     */
    Result<HrKpiIndicator> createIndicator(HrKpiIndicator indicator);

    /**
     * 更新KPI指标
     */
    Result<Boolean> updateIndicator(HrKpiIndicator indicator);

    /**
     * 删除KPI指标
     */
    Result<Boolean> deleteIndicator(Long id);

    /**
     * 获取KPI指标详情
     */
    Result<HrKpiIndicator> getIndicator(Long id);

    /**
     * 分页查询KPI指标列表
     */
    Result<IPage<HrKpiIndicator>> listIndicators(IPage<?> page, Integer indicatorType, Boolean isEnabled);

    /**
     * 获取启用的指标列表
     */
    Result<List<HrKpiIndicator>> listEnabledIndicators();

    /**
     * 根据岗位获取KPI指标
     */
    Result<List<HrKpiIndicator>> listIndicatorsByPosition(Long positionId);

    /**
     * 根据类型获取KPI指标
     */
    Result<List<HrKpiIndicator>> listIndicatorsByType(Integer indicatorType);
}
