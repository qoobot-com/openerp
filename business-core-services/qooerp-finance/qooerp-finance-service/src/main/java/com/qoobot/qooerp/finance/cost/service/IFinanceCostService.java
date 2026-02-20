package com.qoobot.qooerp.finance.cost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.finance.cost.domain.FinanceCostCenter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 成本服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IFinanceCostService extends IService<FinanceCostCenter> {

    /**
     * 创建成本中心
     *
     * @param costCenter 成本中心信息
     * @return 创建后的成本中心
     */
    FinanceCostCenter createCostCenter(FinanceCostCenter costCenter);

    /**
     * 成本分摊
     *
     * @param sourceCenterId 源成本中心ID
     * @param targetCenterIds 目标成本中心ID列表
     * @param amount 分摊金额
     * @param periodId 期间ID
     */
    void allocateCost(Long sourceCenterId, List<Long> targetCenterIds, BigDecimal amount, Long periodId);

    /**
     * 计算成本差异
     *
     * @param costCenterId 成本中心ID
     * @param periodId 期间ID
     * @return 成本差异信息
     */
    Map<String, Object> calculateCostVariance(Long costCenterId, Long periodId);

    /**
     * 查询成本中心列表
     *
     * @param deptId 部门ID
     * @param status 状态
     * @return 成本中心列表
     */
    List<FinanceCostCenter> queryCostCenters(Long deptId, String status);

    /**
     * 查询成本分析
     *
     * @param fiscalYear 会计年度
     * @param costCenterId 成本中心ID
     * @return 成本分析数据
     */
    Map<String, Object> analyzeCost(Integer fiscalYear, Long costCenterId);

    /**
     * 归集成本
     *
     * @param costCenterId 成本中心ID
     * @param amount 成本金额
     * @param costType 成本类型（可选）
     */
    void collectCost(Long costCenterId, BigDecimal amount, String costType);

    /**
     * 查询成本汇总
     *
     * @param fiscalYear 会计年度
     * @param periodId 期间ID
     * @return 成本汇总
     */
    Map<String, BigDecimal> getCostSummary(Integer fiscalYear, Long periodId);
}
