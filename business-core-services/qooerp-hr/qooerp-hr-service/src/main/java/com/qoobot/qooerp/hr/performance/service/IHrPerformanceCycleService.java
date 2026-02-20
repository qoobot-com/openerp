package com.qoobot.qooerp.hr.performance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.performance.domain.HrPerformanceCycle;
import com.qoobot.qooerp.common.result.Result;

import java.util.List;

/**
 * 绩效周期服务接口
 */
public interface IHrPerformanceCycleService extends IService<HrPerformanceCycle> {

    /**
     * 创建绩效周期
     */
    Result<HrPerformanceCycle> createCycle(HrPerformanceCycle cycle);

    /**
     * 更新绩效周期
     */
    Result<Boolean> updateCycle(HrPerformanceCycle cycle);

    /**
     * 删除绩效周期
     */
    Result<Boolean> deleteCycle(Long id);

    /**
     * 启用绩效周期
     */
    Result<Boolean> enableCycle(Long id);

    /**
     * 获取绩效周期详情
     */
    Result<HrPerformanceCycle> getCycle(Long id);

    /**
     * 分页查询绩效周期列表
     */
    Result<IPage<HrPerformanceCycle>> listCycles(IPage<?> page, Integer year, Integer cycleType);

    /**
     * 获取启用的周期列表
     */
    Result<List<HrPerformanceCycle>> listEnabledCycles();

    /**
     * 获取当前周期
     */
    Result<HrPerformanceCycle> getCurrentCycle();

    /**
     * 启动周期
     */
    Result<Boolean> startCycle(Long id);

    /**
     * 结束周期
     */
    Result<Boolean> endCycle(Long id);
}
