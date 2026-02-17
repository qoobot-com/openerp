package com.qoobot.qooerp.scm.logistics.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.scm.logistics.cost.domain.LogisticsCost;
import com.qoobot.qooerp.scm.logistics.cost.dto.LogisticsCostDTO;
import com.qoobot.qooerp.scm.logistics.cost.mapper.LogisticsCostMapper;
import com.qoobot.qooerp.scm.logistics.cost.service.ILogisticsCostService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流费用Service实现
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Service
public class LogisticsCostServiceImpl extends ServiceImpl<LogisticsCostMapper, LogisticsCost>
        implements ILogisticsCostService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(LogisticsCostDTO dto) {
        if (dto.getTrackingId() == null) {
            throw new BusinessException("物流跟踪ID不能为空");
        }

        // 设置默认币种
        if (!StringUtils.hasText(dto.getCurrency())) {
            dto.setCurrency("CNY");
        }

        // 设置默认付款状态
        if (!StringUtils.hasText(dto.getPaidStatus())) {
            dto.setPaidStatus("UNPAID");
        }

        // 初始化已付金额
        if (dto.getPaidAmount() == null) {
            dto.setPaidAmount(BigDecimal.ZERO);
        }

        // 检查付款状态一致性
        checkPaymentStatus(dto.getPaidStatus(), dto.getPaidAmount(), dto.getAmount());

        LogisticsCost cost = new LogisticsCost();
        BeanUtils.copyProperties(dto, cost);

        save(cost);
        return cost.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(LogisticsCostDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("费用ID不能为空");
        }

        LogisticsCost cost = getById(dto.getId());
        if (cost == null) {
            throw new BusinessException("物流费用不存在");
        }

        // 检查付款状态一致性
        if (dto.getPaidStatus() != null) {
            BigDecimal amount = dto.getAmount() != null ? dto.getAmount() : cost.getAmount();
            BigDecimal paidAmount = dto.getPaidAmount() != null ? dto.getPaidAmount() : cost.getPaidAmount();
            checkPaymentStatus(dto.getPaidStatus(), paidAmount, amount);
        }

        BeanUtils.copyProperties(dto, cost);
        return updateById(cost);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        LogisticsCost cost = getById(id);
        if (cost == null) {
            throw new BusinessException("物流费用不存在");
        }
        return removeById(id);
    }

    @Override
    public LogisticsCostDTO getDetail(Long id) {
        LogisticsCost cost = getById(id);
        if (cost == null) {
            throw new BusinessException("物流费用不存在");
        }

        LogisticsCostDTO dto = new LogisticsCostDTO();
        BeanUtils.copyProperties(cost, dto);
        return dto;
    }

    @Override
    public PageResult<LogisticsCost> queryPage(Long trackingId, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<LogisticsCost> wrapper = new LambdaQueryWrapper<>();

        if (trackingId != null) {
            wrapper.eq(LogisticsCost::getTrackingId, trackingId);
        }

        wrapper.orderByDesc(LogisticsCost::getCreateTime);

        Page<LogisticsCost> page = new Page<>(pageNo, pageSize);
        Page<LogisticsCost> result = page(page, wrapper);

        return new PageResult<>(page.getCurrent(), page.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    public List<LogisticsCost> getCostsByTrackingId(Long trackingId) {
        LambdaQueryWrapper<LogisticsCost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogisticsCost::getTrackingId, trackingId);
        wrapper.orderByDesc(LogisticsCost::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<LogisticsCost> getCostsByOrderId(Long orderId) {
        LambdaQueryWrapper<LogisticsCost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogisticsCost::getOrderId, orderId);
        wrapper.orderByDesc(LogisticsCost::getCreateTime);
        return list(wrapper);
    }

    @Override
    public BigDecimal calculateTotalCost(Long trackingId) {
        List<LogisticsCost> costs = getCostsByTrackingId(trackingId);

        return costs.stream()
                .map(LogisticsCost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePaymentStatus(Long id, String paidStatus, BigDecimal paidAmount, LocalDate paidDate) {
        LogisticsCost cost = getById(id);
        if (cost == null) {
            throw new BusinessException("物流费用不存在");
        }

        // 更新付款状态
        cost.setPaidStatus(paidStatus);

        // 更新已付金额
        if (paidAmount != null) {
            cost.setPaidAmount(paidAmount);
        }

        // 如果是全部付款或部分付款，更新付款日期
        if (("PAID".equals(paidStatus) || "PARTIAL".equals(paidStatus)) && paidDate != null) {
            cost.setPaidDate(paidDate);
        } else if ("PAID".equals(paidStatus) && paidDate == null) {
            cost.setPaidDate(LocalDate.now());
        }

        return updateById(cost);
    }

    @Override
    public Map<String, BigDecimal> getCostStatistics(Long trackingId) {
        List<LogisticsCost> costs = getCostsByTrackingId(trackingId);

        BigDecimal totalAmount = costs.stream()
                .map(LogisticsCost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = costs.stream()
                .map(LogisticsCost::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal unpaid = totalAmount.subtract(totalPaid);

        Map<String, BigDecimal> statistics = new HashMap<>();
        statistics.put("totalAmount", totalAmount);
        statistics.put("totalPaid", totalPaid);
        statistics.put("unpaid", unpaid);

        return statistics;
    }

    /**
     * 检查付款状态一致性
     */
    private void checkPaymentStatus(String paidStatus, BigDecimal paidAmount, BigDecimal amount) {
        if (amount == null || paidAmount == null) {
            return;
        }

        switch (paidStatus) {
            case "UNPAID":
                if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
                    throw new BusinessException("未付状态的已付金额必须为0");
                }
                break;
            case "PAID":
                if (paidAmount.compareTo(amount) != 0) {
                    throw new BusinessException("已付状态的已付金额必须等于总金额");
                }
                break;
            case "PARTIAL":
                if (paidAmount.compareTo(BigDecimal.ZERO) <= 0 || paidAmount.compareTo(amount) >= 0) {
                    throw new BusinessException("部分付款状态的已付金额必须大于0且小于总金额");
                }
                break;
            default:
                throw new BusinessException("无效的付款状态：" + paidStatus);
        }
    }
}
