package com.qoobot.qooerp.finance.budget.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.finance.budget.domain.FinanceBudget;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 预算服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IFinanceBudgetService extends IService<FinanceBudget> {

    /**
     * 创建预算
     *
     * @param budget 预算信息
     * @return 创建后的预算
     */
    FinanceBudget createBudget(FinanceBudget budget);

    /**
     * 审批预算
     *
     * @param budgetId 预算ID
     * @param approved 是否通过
     * @param comment 审批意见
     */
    void approveBudget(Long budgetId, Boolean approved, String comment);

    /**
     * 执行预算控制
     *
     * @param budgetId 预算ID
     * @param amount 使用金额
     * @return 是否通过控制
     */
    boolean checkBudgetControl(Long budgetId, BigDecimal amount);

    /**
     * 更新预算执行情况
     *
     * @param budgetId 预算ID
     * @param actualAmount 实际金额
     */
    void updateBudgetExecution(Long budgetId, BigDecimal actualAmount);

    /**
     * 查询预算执行情况
     *
     * @param fiscalYear 会计年度
     * @param departmentId 部门ID
     * @return 预算执行情况
     */
    Map<String, Object> getBudgetExecution(Integer fiscalYear, Long departmentId);

    /**
     * 查询预算列表
     *
     * @param fiscalYear 会计年度
     * @param departmentId 部门ID
     * @param status 状态
     * @return 预算列表
     */
    List<FinanceBudget> queryBudgets(Integer fiscalYear, Long departmentId, String status);

    /**
     * 查询预算超支情况
     *
     * @param fiscalYear 会计年度
     * @return 超支预算列表
     */
    List<FinanceBudget> queryOverBudget(Integer fiscalYear);

    /**
     * 调整预算
     *
     * @param budgetId 预算ID
     * @param newBudgetAmount 新预算金额
     */
    void adjustBudget(Long budgetId, BigDecimal newBudgetAmount);
}
