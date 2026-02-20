package com.qoobot.qooerp.finance.invoice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.finance.invoice.domain.FinanceInvoice;
import com.qoobot.qooerp.finance.invoice.mapper.FinanceInvoiceMapper;
import com.qoobot.qooerp.finance.invoice.service.IFinanceInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 发票服务实现类
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceInvoiceServiceImpl extends ServiceImpl<FinanceInvoiceMapper, FinanceInvoice> implements IFinanceInvoiceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceInvoice createInvoice(FinanceInvoice invoice) {
        // 生成发票编号
        if (invoice.getInvoiceNo() == null || invoice.getInvoiceNo().isEmpty()) {
            invoice.setInvoiceNo(generateInvoiceNo(invoice.getInvoiceType()));
        }

        // 计算税额和价税合计
        calculateTaxAmounts(invoice);

        // 初始化状态
        if (invoice.getStatus() == null || invoice.getStatus().isEmpty()) {
            invoice.setStatus("DRAFT");
        }

        invoice.setCreateTime(LocalDateTime.now());

        save(invoice);
        log.info("创建发票成功，发票号: {}, 类型: {}, 金额: {}",
                invoice.getInvoiceNo(), invoice.getInvoiceType(), invoice.getTotalAmount());

        return invoice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceInvoice issueSalesInvoice(Long customerId, String buyerName, String buyerTaxNo,
                                             BigDecimal amount, BigDecimal taxRate, String invoiceCategory) {
        FinanceInvoice invoice = new FinanceInvoice();
        invoice.setInvoiceType("SALES");
        invoice.setInvoiceCategory(invoiceCategory);
        invoice.setCustomerId(customerId);
        invoice.setBuyerName(buyerName);
        invoice.setBuyerTaxNo(buyerTaxNo);
        invoice.setAmount(amount);
        invoice.setTaxRate(taxRate);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setSellerName("默认销售方");
        invoice.setSellerTaxNo("默认销售方税号");
        invoice.setStatus("PENDING");

        return createInvoice(invoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceInvoice receivePurchaseInvoice(Long supplierId, String sellerName, String sellerTaxNo,
                                                   BigDecimal amount, BigDecimal taxRate, String invoiceCategory) {
        FinanceInvoice invoice = new FinanceInvoice();
        invoice.setInvoiceType("PURCHASE");
        invoice.setInvoiceCategory(invoiceCategory);
        invoice.setSupplierId(supplierId);
        invoice.setSellerName(sellerName);
        invoice.setSellerTaxNo(sellerTaxNo);
        invoice.setAmount(amount);
        invoice.setTaxRate(taxRate);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setBuyerName("默认购买方");
        invoice.setBuyerTaxNo("默认购买方税号");
        invoice.setStatus("PENDING");

        return createInvoice(invoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verifyInvoice(Long invoiceId) {
        FinanceInvoice invoice = getById(invoiceId);
        if (invoice == null) {
            throw new BusinessException("发票不存在");
        }

        if ("VERIFIED".equals(invoice.getStatus()) || "INVALID".equals(invoice.getStatus())) {
            throw new BusinessException("发票已认证或已作废，无法重复认证");
        }

        invoice.setStatus("VERIFIED");
        invoice.setVerifyDate(LocalDate.now());
        updateById(invoice);

        log.info("认证发票成功，发票号: {}", invoice.getInvoiceNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invalidateInvoice(Long invoiceId) {
        FinanceInvoice invoice = getById(invoiceId);
        if (invoice == null) {
            throw new BusinessException("发票不存在");
        }

        if ("VERIFIED".equals(invoice.getStatus())) {
            throw new BusinessException("已认证的发票无法作废");
        }

        invoice.setStatus("INVALID");
        updateById(invoice);

        log.info("作废发票成功，发票号: {}", invoice.getInvoiceNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void linkVoucher(Long invoiceId, Long voucherId) {
        FinanceInvoice invoice = getById(invoiceId);
        if (invoice == null) {
            throw new BusinessException("发票不存在");
        }

        invoice.setVoucherId(voucherId);
        updateById(invoice);

        log.info("关联凭证成功，发票号: {}, 凭证ID: {}", invoice.getInvoiceNo(), voucherId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void linkArap(Long invoiceId, Long receivableId, Long payableId) {
        FinanceInvoice invoice = getById(invoiceId);
        if (invoice == null) {
            throw new BusinessException("发票不存在");
        }

        invoice.setReceivableId(receivableId);
        invoice.setPayableId(payableId);
        updateById(invoice);

        log.info("关联应收应付成功，发票号: {}, 应收ID: {}, 应付ID: {}",
                invoice.getInvoiceNo(), receivableId, payableId);
    }

    @Override
    public List<FinanceInvoice> queryInvoices(String invoiceType, String status) {
        LambdaQueryWrapper<FinanceInvoice> wrapper = new LambdaQueryWrapper<>();

        if (invoiceType != null && !invoiceType.isEmpty()) {
            wrapper.eq(FinanceInvoice::getInvoiceType, invoiceType);
        }

        if (status != null && !status.isEmpty()) {
            wrapper.eq(FinanceInvoice::getStatus, status);
        }

        wrapper.orderByDesc(FinanceInvoice::getInvoiceDate);
        return list(wrapper);
    }

    @Override
    public FinanceInvoice getByInvoiceNo(String invoiceNo) {
        LambdaQueryWrapper<FinanceInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceInvoice::getInvoiceNo, invoiceNo);
        return getOne(wrapper);
    }

    @Override
    public List<FinanceInvoice> queryInvoicesByDateRange(LocalDate startDate, LocalDate endDate, String invoiceType) {
        LambdaQueryWrapper<FinanceInvoice> wrapper = new LambdaQueryWrapper<>();

        if (invoiceType != null && !invoiceType.isEmpty()) {
            wrapper.eq(FinanceInvoice::getInvoiceType, invoiceType);
        }

        wrapper.ge(FinanceInvoice::getInvoiceDate, startDate)
                .le(FinanceInvoice::getInvoiceDate, endDate)
                .orderByDesc(FinanceInvoice::getInvoiceDate);

        return list(wrapper);
    }

    /**
     * 计算税额和价税合计
     */
    private void calculateTaxAmounts(FinanceInvoice invoice) {
        if (invoice.getAmount() != null && invoice.getTaxRate() != null) {
            // 计算税额
            BigDecimal taxAmount = invoice.getAmount()
                    .multiply(invoice.getTaxRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            invoice.setTaxAmount(taxAmount);

            // 计算价税合计
            BigDecimal totalAmount = invoice.getAmount().add(taxAmount);
            invoice.setTotalAmount(totalAmount);
        }
    }

    @Override
    public List<FinanceInvoice> getInvoicesByCustomer(Long customerId) {
        LambdaQueryWrapper<FinanceInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceInvoice::getCustomerId, customerId);
        wrapper.orderByDesc(FinanceInvoice::getInvoiceDate);
        return list(wrapper);
    }

    @Override
    public List<FinanceInvoice> getInvoicesBySupplier(Long supplierId) {
        LambdaQueryWrapper<FinanceInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceInvoice::getSupplierId, supplierId);
        wrapper.orderByDesc(FinanceInvoice::getInvoiceDate);
        return list(wrapper);
    }

    @Override
    public List<FinanceInvoice> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<FinanceInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(FinanceInvoice::getInvoiceDate, startDate)
                .le(FinanceInvoice::getInvoiceDate, endDate)
                .orderByDesc(FinanceInvoice::getInvoiceDate);
        return list(wrapper);
    }

    /**
     * 生成发票编号
     */
    private String generateInvoiceNo(String invoiceType) {
        String prefix = "SALES".equals(invoiceType) ? "XS" : "JX";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + timestamp + uuid;
    }
}
