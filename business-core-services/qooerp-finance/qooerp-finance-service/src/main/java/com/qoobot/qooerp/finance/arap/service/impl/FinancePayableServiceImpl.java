package com.qoobot.qooerp.finance.arap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.finance.arap.domain.FinancePayable;
import com.qoobot.qooerp.finance.arap.domain.FinancePayment;
import com.qoobot.qooerp.finance.arap.mapper.FinancePayableMapper;
import com.qoobot.qooerp.finance.arap.mapper.FinancePaymentMapper;
import com.qoobot.qooerp.finance.arap.service.IFinancePayableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应付账款服务实现类
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinancePayableServiceImpl extends ServiceImpl<FinancePayableMapper, FinancePayable> implements IFinancePayableService {

    private final FinancePaymentMapper paymentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinancePayable createPayable(FinancePayable payable) {
        // 校验参数
        if (payable.getSupplierId() == null) {
            throw new BusinessException("供应商ID不能为空");
        }
        if (payable.getPayableAmount() == null || payable.getPayableAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("应付金额必须大于0");
        }

        // 初始化字段
        payable.setPaidAmount(BigDecimal.ZERO);
        payable.setUnpaidAmount(payable.getPayableAmount());
        payable.setStatus("UNPAID");
        payable.setAgingDays(0);

        // 计算账龄
        if (payable.getPayableDate() != null) {
            payable.setAgingDays(calculateAgingDays(payable.getPayableDate(), LocalDate.now()));
        }

        save(payable);
        log.info("创建应付账款成功，单号: {}, 供应商: {}, 金额: {}",
                payable.getPayableCode(), payable.getSupplierName(), payable.getPayableAmount());

        return payable;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPayable(Long payableId) {
        FinancePayable payable = getById(payableId);
        if (payable == null) {
            throw new BusinessException("应付账款不存在");
        }

        if (!"UNPAID".equals(payable.getStatus())) {
            throw new BusinessException("只有未付款状态的单据才能取消");
        }

        payable.setStatus("CANCELLED");
        updateById(payable);
        log.info("取消应付账款成功，单号: {}", payable.getPayableCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeOffPayable(Long payableId, String paymentNo, BigDecimal amount) {
        FinancePayable payable = getById(payableId);
        if (payable == null) {
            throw new BusinessException("应付账款不存在");
        }

        if (amount.compareTo(payable.getUnpaidAmount()) > 0) {
            throw new BusinessException("付款金额不能大于未付金额");
        }

        // 创建付款记录
        FinancePayment payment = new FinancePayment();
        payment.setPayableId(payableId);
        payment.setPaymentCode(paymentNo);
        payment.setSupplierId(payable.getSupplierId());
        payment.setSupplierName(payable.getSupplierName());
        payment.setPaymentAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        paymentMapper.insert(payment);

        // 更新应付账款
        BigDecimal newPaidAmount = payable.getPaidAmount().add(amount);
        BigDecimal newUnpaidAmount = payable.getUnpaidAmount().subtract(amount);

        payable.setPaidAmount(newPaidAmount);
        payable.setUnpaidAmount(newUnpaidAmount);

        // 更新状态
        if (newUnpaidAmount.compareTo(BigDecimal.ZERO) == 0) {
            payable.setStatus("PAID");
        } else {
            payable.setStatus("PARTIAL");
        }

        updateById(payable);
        log.info("付款核销成功，应付单号: {}, 付款单号: {}, 付款金额: {}",
                payable.getPayableCode(), paymentNo, amount);
    }

    @Override
    public Map<String, BigDecimal> analyzeAging(String supplierName, LocalDate asOfDate) {
        LambdaQueryWrapper<FinancePayable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinancePayable::getStatus, "UNPAID")
                .ne(FinancePayable::getUnpaidAmount, BigDecimal.ZERO);

        if (supplierName != null && !supplierName.isEmpty()) {
            wrapper.like(FinancePayable::getSupplierName, supplierName);
        }

        List<FinancePayable> payables = list(wrapper);

        Map<String, BigDecimal> agingMap = new HashMap<>();
        agingMap.put("0-30天", BigDecimal.ZERO);
        agingMap.put("31-60天", BigDecimal.ZERO);
        agingMap.put("61-90天", BigDecimal.ZERO);
        agingMap.put("90天以上", BigDecimal.ZERO);
        agingMap.put("合计", BigDecimal.ZERO);

        for (FinancePayable payable : payables) {
            int agingDays = calculateAgingDays(payable.getPayableDate(), asOfDate);
            String agingRange = getAgingRange(agingDays);

            BigDecimal rangeAmount = agingMap.getOrDefault(agingRange, BigDecimal.ZERO);
            agingMap.put(agingRange, rangeAmount.add(payable.getUnpaidAmount()));

            BigDecimal totalAmount = agingMap.get("合计");
            agingMap.put("合计", totalAmount.add(payable.getUnpaidAmount()));
        }

        return agingMap;
    }

    @Override
    public List<FinancePayable> queryPayables(String supplierName, String status) {
        LambdaQueryWrapper<FinancePayable> wrapper = new LambdaQueryWrapper<>();

        if (supplierName != null && !supplierName.isEmpty()) {
            wrapper.like(FinancePayable::getSupplierName, supplierName);
        }

        if (status != null && !status.isEmpty()) {
            wrapper.eq(FinancePayable::getStatus, status);
        }

        wrapper.orderByDesc(FinancePayable::getPayableDate);
        return list(wrapper);
    }

    @Override
    public Integer calculateAgingDays(LocalDate payableDate, LocalDate asOfDate) {
        if (payableDate == null || asOfDate == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(payableDate, asOfDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateAging(LocalDate asOfDate) {
        LambdaQueryWrapper<FinancePayable> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(FinancePayable::getStatus, "CANCELLED");

        List<FinancePayable> payables = list(wrapper);

        for (FinancePayable payable : payables) {
            if (payable.getPayableDate() != null) {
                int agingDays = calculateAgingDays(payable.getPayableDate(), asOfDate);
                payable.setAgingDays(agingDays);
                updateById(payable);
            }
        }

        log.info("批量更新应付账龄完成，更新记录数: {}", payables.size());
    }

    /**
     * 根据账龄天数获取账龄区间
     */
    private String getAgingRange(int agingDays) {
        if (agingDays <= 30) {
            return "0-30天";
        } else if (agingDays <= 60) {
            return "31-60天";
        } else if (agingDays <= 90) {
            return "61-90天";
        } else {
            return "90天以上";
        }
    }
}
