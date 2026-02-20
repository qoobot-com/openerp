package com.qoobot.qooerp.finance.arap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.finance.arap.domain.FinancePayable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 应付账款服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IFinancePayableService extends IService<FinancePayable> {

    /**
     * 创建应付账款
     *
     * @param payable 应付账款信息
     * @return 创建后的应付账款
     */
    FinancePayable createPayable(FinancePayable payable);

    /**
     * 取消应付账款
     *
     * @param payableId 应付账款ID
     */
    void cancelPayable(Long payableId);

    /**
     * 付款核销
     *
     * @param payableId 应付账款ID
     * @param paymentNo 付款单号
     * @param amount 付款金额
     */
    void writeOffPayable(Long payableId, String paymentNo, BigDecimal amount);

    /**
     * 账龄分析
     *
     * @param supplierName 供应商名称
     * @param asOfDate 截止日期
     * @return 账龄分布（30天/60天/90天/90天以上）
     */
    Map<String, BigDecimal> analyzeAging(String supplierName, LocalDate asOfDate);

    /**
     * 查询应付账款
     *
     * @param supplierName 供应商名称
     * @param status 单据状态
     * @return 应付账款列表
     */
    List<FinancePayable> queryPayables(String supplierName, String status);

    /**
     * 计算账龄（天数）
     *
     * @param payableDate 应付日期
     * @param asOfDate 截止日期
     * @return 账龄天数
     */
    Integer calculateAgingDays(LocalDate payableDate, LocalDate asOfDate);

    /**
     * 批量更新账龄
     *
     * @param asOfDate 截止日期
     */
    void batchUpdateAging(LocalDate asOfDate);
}
