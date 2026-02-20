package com.qoobot.qooerp.finance.arap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.finance.arap.domain.FinanceReceivable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 应收账款服务接口
 */
public interface IFinanceReceivableService extends IService<FinanceReceivable> {

    /**
     * 创建应收账款
     */
    FinanceReceivable createReceivable(FinanceReceivable receivable);

    /**
     * 收款核销
     */
    void writeOffReceivable(Long receivableId, String receiptNo, BigDecimal amount);

    /**
     * 账龄分析
     * @param customerName 客户名称
     * @param asOfDate 截止日期
     * @return 账龄分布（30天/60天/90天/90天以上）
     */
    Map<String, BigDecimal> analyzeAging(String customerName, LocalDate asOfDate);

    /**
     * 查询应收账款
     */
    List<FinanceReceivable> queryReceivables(String customerName, Integer status);

    /**
     * 计算账龄（天数）
     */
    Integer calculateAgingDays(LocalDate receivableDate, LocalDate asOfDate);
}
