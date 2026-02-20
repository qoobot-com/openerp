package com.qoobot.qooerp.finance.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.finance.cost.domain.FinanceCostCenter;
import com.qoobot.qooerp.finance.cost.mapper.FinanceCostCenterMapper;
import com.qoobot.qooerp.finance.cost.service.IFinanceCostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成本服务实现类
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceCostServiceImpl extends ServiceImpl<FinanceCostCenterMapper, FinanceCostCenter> implements IFinanceCostService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceCostCenter createCostCenter(FinanceCostCenter costCenter) {
        if (costCenter.getName() == null || costCenter.getName().isEmpty()) {
            throw new BusinessException("成本中心名称不能为空");
        }

        if (costCenter.getDeptId() == null) {
            throw new BusinessException("所属部门不能为空");
        }

        costCenter.setStatus(0); // 正常

        save(costCenter);
        log.info("创建成本中心成功，名称: {}, 部门ID: {}", costCenter.getName(), costCenter.getDeptId());

        return costCenter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allocateCost(Long sourceCenterId, List<Long> targetCenterIds, BigDecimal amount, Long periodId) {
        FinanceCostCenter sourceCenter = getById(sourceCenterId);
        if (sourceCenter == null) {
            throw new BusinessException("源成本中心不存在");
        }

        if (targetCenterIds == null || targetCenterIds.isEmpty()) {
            throw new BusinessException("目标成本中心不能为空");
        }

        // 简单平均分摊（实际业务中可能按权重、按比例等规则）
        BigDecimal allocateAmountPerCenter = amount.divide(
                BigDecimal.valueOf(targetCenterIds.size()),
                2,
                RoundingMode.HALF_UP
        );

        for (Long targetCenterId : targetCenterIds) {
            FinanceCostCenter targetCenter = getById(targetCenterId);
            if (targetCenter == null) {
                continue;
            }

            // 更新目标成本中心金额
            BigDecimal currentAmount = targetCenter.getActualCost() != null
                    ? targetCenter.getActualCost() : BigDecimal.ZERO;
            targetCenter.setActualCost(currentAmount.add(allocateAmountPerCenter));
            updateById(targetCenter);

            log.info("成本分摊，源中心: {}, 目标中心: {}, 分摊金额: {}",
                    sourceCenter.getName(), targetCenter.getName(), allocateAmountPerCenter);
        }

        log.info("成本分摊完成，总金额: {}, 分摊中心数: {}", amount, targetCenterIds.size());
    }

    @Override
    public Map<String, Object> calculateCostVariance(Long costCenterId, Long periodId) {
        FinanceCostCenter costCenter = getById(costCenterId);
        if (costCenter == null) {
            throw new BusinessException("成本中心不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("costCenter", costCenter);

        BigDecimal annualBudget = costCenter.getAnnualBudget() != null
                ? costCenter.getAnnualBudget() : BigDecimal.ZERO;
        BigDecimal actualCost = costCenter.getActualCost() != null
                ? costCenter.getActualCost() : BigDecimal.ZERO;

        BigDecimal variance = actualCost.subtract(annualBudget);
        BigDecimal varianceRate = BigDecimal.ZERO;

        if (annualBudget.compareTo(BigDecimal.ZERO) > 0) {
            varianceRate = variance.divide(annualBudget, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        result.put("annualBudget", annualBudget);
        result.put("actualCost", actualCost);
        result.put("variance", variance);
        result.put("varianceRate", varianceRate);

        return result;
    }

    @Override
    public List<FinanceCostCenter> queryCostCenters(Long departmentId, String status) {
        LambdaQueryWrapper<FinanceCostCenter> wrapper = new LambdaQueryWrapper<>();

        if (departmentId != null) {
            wrapper.eq(FinanceCostCenter::getDeptId, departmentId);
        }

        if (status != null && !status.isEmpty()) {
            wrapper.eq(FinanceCostCenter::getStatus, Integer.valueOf(status));
        }

        wrapper.orderByAsc(FinanceCostCenter::getCode);
        return list(wrapper);
    }

    @Override
    public Map<String, Object> analyzeCost(Integer fiscalYear, Long costCenterId) {
        LambdaQueryWrapper<FinanceCostCenter> wrapper = new LambdaQueryWrapper<>();

        if (costCenterId != null) {
            wrapper.eq(FinanceCostCenter::getId, costCenterId).or()
                    .eq(FinanceCostCenter::getDeptId, costCenterId);
        }

        List<FinanceCostCenter> costCenters = list(wrapper);

        Map<String, Object> result = new HashMap<>();
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActualCost = BigDecimal.ZERO;

        for (FinanceCostCenter center : costCenters) {
            BigDecimal budget = center.getAnnualBudget() != null
                    ? center.getAnnualBudget() : BigDecimal.ZERO;
            BigDecimal actualCost = center.getActualCost() != null
                    ? center.getActualCost() : BigDecimal.ZERO;

            totalBudget = totalBudget.add(budget);
            totalActualCost = totalActualCost.add(actualCost);
        }

        BigDecimal totalVariance = totalActualCost.subtract(totalBudget);

        result.put("costCenters", costCenters);
        result.put("totalBudget", totalBudget);
        result.put("totalActualCost", totalActualCost);
        result.put("totalVariance", totalVariance);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectCost(Long costCenterId, BigDecimal amount, String costType) {
        FinanceCostCenter costCenter = getById(costCenterId);
        if (costCenter == null) {
            throw new BusinessException("成本中心不存在");
        }

        BigDecimal currentAmount = costCenter.getActualCost() != null
                ? costCenter.getActualCost() : BigDecimal.ZERO;
        costCenter.setActualCost(currentAmount.add(amount));

        updateById(costCenter);

        log.info("成本归集，成本中心: {}, 归集金额: {}", costCenter.getName(), amount);
    }

    @Override
    public Map<String, BigDecimal> getCostSummary(Integer fiscalYear, Long periodId) {
        LambdaQueryWrapper<FinanceCostCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceCostCenter::getStatus, 0); // 正常

        List<FinanceCostCenter> costCenters = list(wrapper);

        Map<String, BigDecimal> summary = new HashMap<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (FinanceCostCenter center : costCenters) {
            BigDecimal amount = center.getActualCost() != null
                    ? center.getActualCost() : BigDecimal.ZERO;
            totalCost = totalCost.add(amount);
        }

        summary.put("totalCost", totalCost);

        return summary;
    }
}
