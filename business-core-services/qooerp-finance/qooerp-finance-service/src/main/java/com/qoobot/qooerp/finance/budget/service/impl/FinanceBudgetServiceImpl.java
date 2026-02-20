package com.qoobot.qooerp.finance.budget.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.finance.budget.domain.FinanceBudget;
import com.qoobot.qooerp.finance.budget.mapper.FinanceBudgetMapper;
import com.qoobot.qooerp.finance.budget.service.IFinanceBudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预算服务实现类
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceBudgetServiceImpl extends ServiceImpl<FinanceBudgetMapper, FinanceBudget> implements IFinanceBudgetService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceBudget createBudget(FinanceBudget budget) {
        if (budget.getBudgetAmount() == null || budget.getBudgetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("预算金额必须大于0");
        }

        budget.setExecutedAmount(BigDecimal.ZERO);
        budget.setExecutionRate(BigDecimal.ZERO);
        budget.setStatus(0); // 草稿

        save(budget);
        log.info("创建预算成功，年度: {}, 预算金额: {}",
                budget.getBudgetYear(), budget.getBudgetAmount());

        return budget;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveBudget(Long budgetId, Boolean approved, String comment) {
        FinanceBudget budget = getById(budgetId);
        if (budget == null) {
            throw new BusinessException("预算不存在");
        }

        if (budget.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的预算才能审批");
        }

        if (approved) {
            budget.setStatus(2); // 已审批
            log.info("预算审批通过，预算ID: {}", budgetId);
        } else {
            budget.setStatus(3); // 已驳回
            budget.setRejectReason(comment);
            log.info("预算审批拒绝，预算ID: {}, 意见: {}", budgetId, comment);
        }

        updateById(budget);
    }

    @Override
    public boolean checkBudgetControl(Long budgetId, BigDecimal amount) {
        FinanceBudget budget = getById(budgetId);
        if (budget == null) {
            throw new BusinessException("预算不存在");
        }

        if (budget.getStatus() != 2) {
            throw new BusinessException("预算未审批，无法进行预算控制");
        }

        // 计算剩余预算
        BigDecimal remaining = budget.getBudgetAmount().subtract(budget.getExecutedAmount());
        return remaining.compareTo(amount) >= 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBudgetExecution(Long budgetId, BigDecimal actualAmount) {
        FinanceBudget budget = getById(budgetId);
        if (budget == null) {
            throw new BusinessException("预算不存在");
        }

        BigDecimal newExecutedAmount = budget.getExecutedAmount().add(actualAmount);
        budget.setExecutedAmount(newExecutedAmount);

        // 计算执行率
        if (budget.getBudgetAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal executionRate = newExecutedAmount.divide(budget.getBudgetAmount(), 4, BigDecimal.ROUND_HALF_UP);
            budget.setExecutionRate(executionRate);
        }

        updateById(budget);

        log.info("更新预算执行情况，预算ID: {}, 已执行金额: {}, 执行率: {}",
                budgetId, newExecutedAmount, budget.getExecutionRate());
    }

    @Override
    public Map<String, Object> getBudgetExecution(Integer fiscalYear, Long departmentId) {
        LambdaQueryWrapper<FinanceBudget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceBudget::getBudgetYear, fiscalYear);
        wrapper.eq(FinanceBudget::getStatus, 2); // 已审批

        List<FinanceBudget> budgets = list(wrapper);

        Map<String, Object> result = new HashMap<>();
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalExecuted = BigDecimal.ZERO;

        for (FinanceBudget budget : budgets) {
            totalBudget = totalBudget.add(budget.getBudgetAmount());
            totalExecuted = totalExecuted.add(budget.getExecutedAmount());
        }

        result.put("budgets", budgets);
        result.put("totalBudget", totalBudget);
        result.put("totalExecuted", totalExecuted);

        // 计算执行率
        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal executionRate = totalExecuted.divide(totalBudget, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            result.put("executionRate", executionRate);
        }

        return result;
    }

    @Override
    public List<FinanceBudget> queryBudgets(Integer fiscalYear, Long departmentId, String status) {
        LambdaQueryWrapper<FinanceBudget> wrapper = new LambdaQueryWrapper<>();

        if (fiscalYear != null) {
            wrapper.eq(FinanceBudget::getBudgetYear, fiscalYear);
        }

        if (status != null && !status.isEmpty()) {
            wrapper.eq(FinanceBudget::getStatus, Integer.valueOf(status));
        }

        wrapper.orderByDesc(FinanceBudget::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<FinanceBudget> queryOverBudget(Integer fiscalYear) {
        LambdaQueryWrapper<FinanceBudget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceBudget::getBudgetYear, fiscalYear)
                .eq(FinanceBudget::getStatus, 2); // 已审批

        List<FinanceBudget> budgets = list(wrapper);
        return budgets.stream()
                .filter(b -> b.getExecutedAmount() != null && b.getExecutedAmount().compareTo(b.getBudgetAmount()) > 0)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustBudget(Long budgetId, BigDecimal newBudgetAmount) {
        FinanceBudget budget = getById(budgetId);
        if (budget == null) {
            throw new BusinessException("预算不存在");
        }

        if (newBudgetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("预算金额必须大于0");
        }

        BigDecimal oldAmount = budget.getBudgetAmount();
        budget.setBudgetAmount(newBudgetAmount);

        // 重新计算执行率
        if (budget.getExecutedAmount() != null && newBudgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal executionRate = budget.getExecutedAmount().divide(newBudgetAmount, 4, BigDecimal.ROUND_HALF_UP);
            budget.setExecutionRate(executionRate);
        }

        updateById(budget);

        log.info("调整预算金额，预算ID: {}, 原金额: {}, 新金额: {}",
                budgetId, oldAmount, newBudgetAmount);
    }
}
