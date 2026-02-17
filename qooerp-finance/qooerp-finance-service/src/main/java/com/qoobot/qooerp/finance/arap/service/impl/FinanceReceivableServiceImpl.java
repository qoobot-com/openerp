package com.qoobot.qooerp.finance.arap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.finance.arap.domain.FinanceReceivable;
import com.qoobot.qooerp.finance.arap.mapper.FinanceReceivableMapper;
import com.qoobot.qooerp.finance.arap.service.IFinanceReceivableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应收账款服务实现
 * 
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceReceivableServiceImpl extends ServiceImpl<FinanceReceivableMapper, FinanceReceivable>
        implements IFinanceReceivableService {

    private final FinanceReceivableMapper receivableMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceReceivable createReceivable(FinanceReceivable receivable) {
        log.info("创建应收账款，客户ID: {}, 金额: {}", receivable.getCustomerId(), receivable.getReceivableAmount());

        // 计算账龄
        calculateAge(receivable);

        // 设置初始状态
        if (receivable.getStatus() == null) {
            receivable.setStatus("UNPAID");
        }

        save(receivable);
        return receivable;
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelReceivable(Long receivableId) {
        log.info("取消应收账款，ID: {}", receivableId);
        
        FinanceReceivable receivable = getById(receivableId);
        if (receivable == null) {
            throw new RuntimeException("应收账款不存在");
        }
        
        if (!"UNPAID".equals(receivable.getStatus()) && !"PART_PAID".equals(receivable.getStatus())) {
            throw new RuntimeException("只能取消未收款或部分收款的应收账款");
        }
        
        receivable.setStatus("CANCELLED");
        updateById(receivable);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePaidAmount(Long receivableId, BigDecimal paidAmount) {
        log.info("更新应收账款收款金额，ID: {}, 已收金额: {}", receivableId, paidAmount);

        FinanceReceivable receivable = getById(receivableId);
        if (receivable == null) {
            throw new RuntimeException("应收账款不存在");
        }

        if ("CANCELLED".equals(receivable.getStatus()) || "PAID".equals(receivable.getStatus())) {
            throw new RuntimeException("该应收账款已取消或已收款完成");
        }

        receivable.setReceivedAmount(paidAmount);
        receivable.setUnpaidAmount(receivable.getReceivableAmount().subtract(paidAmount));

        // 更新状态
        if (receivable.getUnpaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            receivable.setStatus("PAID");
        } else if (receivable.getUnpaidAmount().compareTo(receivable.getReceivableAmount()) < 0) {
            receivable.setStatus("PARTIAL");
        }

        updateById(receivable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeOffReceivable(Long receivableId, String receiptNo, BigDecimal amount) {
        log.info("核销应收账款，ID: {}, 收据号: {}, 核销金额: {}", receivableId, receiptNo, amount);

        FinanceReceivable receivable = getById(receivableId);
        if (receivable == null) {
            throw new RuntimeException("应收账款不存在");
        }

        if ("CANCELLED".equals(receivable.getStatus())) {
            throw new RuntimeException("已取消的应收账款不能核销");
        }

        BigDecimal newPaidAmount = receivable.getReceivedAmount() == null ? amount : receivable.getReceivedAmount().add(amount);
        BigDecimal totalAmount = receivable.getReceivableAmount();

        if (newPaidAmount.compareTo(totalAmount) > 0) {
            throw new RuntimeException("核销金额不能超过应收金额");
        }

        receivable.setReceivedAmount(newPaidAmount);
        receivable.setUnpaidAmount(totalAmount.subtract(newPaidAmount));

        // 更新状态
        if (receivable.getUnpaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            receivable.setStatus("PAID");
        } else {
            receivable.setStatus("PARTIAL");
        }

        updateById(receivable);
    }

    public List<FinanceReceivable> getReceivablesByCustomer(Long customerId) {
        LambdaQueryWrapper<FinanceReceivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceReceivable::getCustomerId, customerId);
        wrapper.orderByDesc(FinanceReceivable::getReceivableDate);
        return list(wrapper);
    }

    public Map<String, Object> analyzeAgingByCustomer(Long customerId) {
        LambdaQueryWrapper<FinanceReceivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceReceivable::getCustomerId, customerId);
        wrapper.in(FinanceReceivable::getStatus, "UNPAID", "PARTIAL");

        List<FinanceReceivable> receivables = list(wrapper);

        Map<String, Object> result = new HashMap<>();
        BigDecimal amount0_30 = BigDecimal.ZERO;
        BigDecimal amount31_60 = BigDecimal.ZERO;
        BigDecimal amount61_90 = BigDecimal.ZERO;
        BigDecimal amountOver90 = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (FinanceReceivable receivable : receivables) {
            BigDecimal unpaidAmount = receivable.getUnpaidAmount();
            totalAmount = totalAmount.add(unpaidAmount);

            Integer age = receivable.getAgingDays() != null ? receivable.getAgingDays() : 0;

            if (age <= 30) {
                amount0_30 = amount0_30.add(unpaidAmount);
            } else if (age <= 60) {
                amount31_60 = amount31_60.add(unpaidAmount);
            } else if (age <= 90) {
                amount61_90 = amount61_90.add(unpaidAmount);
            } else {
                amountOver90 = amountOver90.add(unpaidAmount);
            }
        }

        result.put("0-30天", amount0_30);
        result.put("31-60天", amount31_60);
        result.put("61-90天", amount61_90);
        result.put("90天以上", amountOver90);
        result.put("总计", totalAmount);
        result.put("记录数", receivables.size());

        return result;
    }

    public Map<String, Object> analyzeAgingOverall() {
        LambdaQueryWrapper<FinanceReceivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(FinanceReceivable::getStatus, "UNPAID", "PARTIAL");

        List<FinanceReceivable> receivables = list(wrapper);

        Map<String, Object> result = new HashMap<>();
        BigDecimal amount0_30 = BigDecimal.ZERO;
        BigDecimal amount31_60 = BigDecimal.ZERO;
        BigDecimal amount61_90 = BigDecimal.ZERO;
        BigDecimal amountOver90 = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (FinanceReceivable receivable : receivables) {
            BigDecimal unpaidAmount = receivable.getUnpaidAmount();
            totalAmount = totalAmount.add(unpaidAmount);

            Integer age = receivable.getAgingDays() != null ? receivable.getAgingDays() : 0;

            if (age <= 30) {
                amount0_30 = amount0_30.add(unpaidAmount);
            } else if (age <= 60) {
                amount31_60 = amount31_60.add(unpaidAmount);
            } else if (age <= 90) {
                amount61_90 = amount61_90.add(unpaidAmount);
            } else {
                amountOver90 = amountOver90.add(unpaidAmount);
            }
        }

        result.put("0-30天", amount0_30);
        result.put("31-60天", amount31_60);
        result.put("61-90天", amount61_90);
        result.put("90天以上", amountOver90);
        result.put("总计", totalAmount);
        result.put("记录数", receivables.size());

        return result;
    }

    public List<FinanceReceivable> getOverdueReceivables() {
        LambdaQueryWrapper<FinanceReceivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(FinanceReceivable::getStatus, "UNPAID", "PARTIAL");
        wrapper.isNotNull(FinanceReceivable::getAgingDays);
        wrapper.gt(FinanceReceivable::getAgingDays, 0);
        wrapper.orderByDesc(FinanceReceivable::getAgingDays);

        return list(wrapper);
    }

    public BigDecimal getTotalUnpaidAmountByCustomer(Long customerId) {
        LambdaQueryWrapper<FinanceReceivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceReceivable::getCustomerId, customerId);
        wrapper.in(FinanceReceivable::getStatus, "UNPAID", "PARTIAL");

        List<FinanceReceivable> receivables = list(wrapper);
        return receivables.stream()
                .map(FinanceReceivable::getUnpaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalUnpaidAmountOverall() {
        LambdaQueryWrapper<FinanceReceivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(FinanceReceivable::getStatus, "UNPAID", "PARTIAL");

        List<FinanceReceivable> receivables = list(wrapper);
        return receivables.stream()
                .map(FinanceReceivable::getUnpaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<FinanceReceivable> queryReceivables(String customerName, Integer status) {
        LambdaQueryWrapper<FinanceReceivable> wrapper = new LambdaQueryWrapper<>();
        if (customerName != null && !customerName.isEmpty()) {
            wrapper.like(FinanceReceivable::getCustomerName, customerName);
        }
        if (status != null) {
            wrapper.eq(FinanceReceivable::getStatus, status);
        }
        wrapper.orderByDesc(FinanceReceivable::getReceivableDate);
        return list(wrapper);
    }

    @Override
    public Integer calculateAgingDays(LocalDate receivableDate, LocalDate asOfDate) {
        if (receivableDate == null || asOfDate == null) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(receivableDate, asOfDate);
        return Math.max(0, (int) days);
    }

    @Override
    public Map<String, BigDecimal> analyzeAging(String customerName, LocalDate asOfDate) {
        LambdaQueryWrapper<FinanceReceivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(FinanceReceivable::getStatus, "UNPAID", "PARTIAL");

        if (customerName != null && !customerName.isEmpty()) {
            wrapper.like(FinanceReceivable::getCustomerName, customerName);
        }

        List<FinanceReceivable> receivables = list(wrapper);

        Map<String, BigDecimal> result = new HashMap<>();
        BigDecimal amount0_30 = BigDecimal.ZERO;
        BigDecimal amount31_60 = BigDecimal.ZERO;
        BigDecimal amount61_90 = BigDecimal.ZERO;
        BigDecimal amountOver90 = BigDecimal.ZERO;

        LocalDate finalAsOfDate = asOfDate != null ? asOfDate : LocalDate.now();

        for (FinanceReceivable receivable : receivables) {
            BigDecimal unpaidAmount = receivable.getUnpaidAmount() != null ? receivable.getUnpaidAmount() : BigDecimal.ZERO;

            Integer agingDays = calculateAgingDays(receivable.getReceivableDate(), finalAsOfDate);

            if (agingDays <= 30) {
                amount0_30 = amount0_30.add(unpaidAmount);
            } else if (agingDays <= 60) {
                amount31_60 = amount31_60.add(unpaidAmount);
            } else if (agingDays <= 90) {
                amount61_90 = amount61_90.add(unpaidAmount);
            } else {
                amountOver90 = amountOver90.add(unpaidAmount);
            }
        }

        result.put("0-30天", amount0_30);
        result.put("31-60天", amount31_60);
        result.put("61-90天", amount61_90);
        result.put("90天以上", amountOver90);

        return result;
    }

    /**
     * 计算账龄（天数）
     */
    private void calculateAge(FinanceReceivable receivable) {
        if (receivable.getReceivableDate() != null) {
            LocalDate now = LocalDate.now();
            long days = ChronoUnit.DAYS.between(receivable.getReceivableDate(), now);
            receivable.setAgingDays((int) Math.max(0, days));
        }
    }
}
