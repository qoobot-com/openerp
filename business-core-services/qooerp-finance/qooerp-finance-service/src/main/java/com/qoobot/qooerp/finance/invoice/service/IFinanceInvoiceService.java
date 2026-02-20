package com.qoobot.qooerp.finance.invoice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.finance.invoice.domain.FinanceInvoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 发票服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IFinanceInvoiceService extends IService<FinanceInvoice> {

    /**
     * 创建发票
     *
     * @param invoice 发票信息
     * @return 创建后的发票
     */
    FinanceInvoice createInvoice(FinanceInvoice invoice);

    /**
     * 开具销项发票
     *
     * @param customerId 客户ID
     * @param buyerName 购买方名称
     * @param buyerTaxNo 购买方税号
     * @param amount 金额（不含税）
     * @param taxRate 税率
     * @param invoiceCategory 发票种类
     * @return 发票
     */
    FinanceInvoice issueSalesInvoice(Long customerId, String buyerName, String buyerTaxNo,
                                       BigDecimal amount, BigDecimal taxRate, String invoiceCategory);

    /**
     * 接收进项发票
     *
     * @param supplierId 供应商ID
     * @param sellerName 销售方名称
     * @param sellerTaxNo 销售方税号
     * @param amount 金额（不含税）
     * @param taxRate 税率
     * @param invoiceCategory 发票种类
     * @return 发票
     */
    FinanceInvoice receivePurchaseInvoice(Long supplierId, String sellerName, String sellerTaxNo,
                                           BigDecimal amount, BigDecimal taxRate, String invoiceCategory);

    /**
     * 认证发票
     *
     * @param invoiceId 发票ID
     */
    void verifyInvoice(Long invoiceId);

    /**
     * 作废发票
     *
     * @param invoiceId 发票ID
     */
    void invalidateInvoice(Long invoiceId);

    /**
     * 关联凭证
     *
     * @param invoiceId 发票ID
     * @param voucherId 凭证ID
     */
    void linkVoucher(Long invoiceId, Long voucherId);

    /**
     * 关联应收应付
     *
     * @param invoiceId 发票ID
     * @param receivableId 应收账款ID
     * @param payableId 应付账款ID
     */
    void linkArap(Long invoiceId, Long receivableId, Long payableId);

    /**
     * 查询发票
     *
     * @param invoiceType 发票类型
     * @param status 发票状态
     * @return 发票列表
     */
    List<FinanceInvoice> queryInvoices(String invoiceType, String status);

    /**
     * 根据发票编号查询
     *
     * @param invoiceNo 发票编号
     * @return 发票
     */
    FinanceInvoice getByInvoiceNo(String invoiceNo);

    /**
     * 查询指定日期范围的发票
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param invoiceType 发票类型
     * @return 发票列表
     */
    List<FinanceInvoice> queryInvoicesByDateRange(LocalDate startDate, LocalDate endDate, String invoiceType);

    /**
     * 查询客户发票
     *
     * @param customerId 客户ID
     * @return 发票列表
     */
    List<FinanceInvoice> getInvoicesByCustomer(Long customerId);

    /**
     * 查询供应商发票
     *
     * @param supplierId 供应商ID
     * @return 发票列表
     */
    List<FinanceInvoice> getInvoicesBySupplier(Long supplierId);

    /**
     * 查询日期范围内发票
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 发票列表
     */
    List<FinanceInvoice> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate);
}
