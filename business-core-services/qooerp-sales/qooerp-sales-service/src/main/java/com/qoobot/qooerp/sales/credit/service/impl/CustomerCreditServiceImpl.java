package com.qoobot.qooerp.sales.credit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.sales.credit.domain.CustomerCredit;
import com.qoobot.qooerp.sales.credit.dto.CreditCheckDTO;
import com.qoobot.qooerp.sales.credit.dto.CustomerCreditDTO;
import com.qoobot.qooerp.sales.credit.mapper.CustomerCreditMapper;
import com.qoobot.qooerp.sales.credit.service.CustomerCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 客户信用服务实现
 */
@Service
@RequiredArgsConstructor
public class CustomerCreditServiceImpl extends ServiceImpl<CustomerCreditMapper, CustomerCredit>
        implements CustomerCreditService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> setCreditLimit(Long customerId, BigDecimal creditLimit) {
        CustomerCredit credit = getCustomerCreditByCustomerId(customerId);

        if (credit == null) {
            // 新建客户信用记录
            credit = new CustomerCredit();
            credit.setCustomerId(customerId);
            credit.setCreditLimit(creditLimit);
            credit.setCreditUsed(BigDecimal.ZERO);
            credit.setCreditLevel(calculateCreditLevel(creditLimit));
            credit.setCreditStatus("ACTIVE");
            save(credit);
        } else {
            // 更新信用额度
            credit.setCreditLimit(creditLimit);
            credit.setCreditLevel(calculateCreditLevel(creditLimit));
            updateById(credit);
        }

        return Result.success();
    }

    @Override
    public Result<CustomerCreditDTO> getCustomerCredit(Long customerId) {
        CustomerCredit credit = getCustomerCreditByCustomerId(customerId);
        if (credit == null) {
            throw new BusinessException("客户信用记录不存在");
        }

        CustomerCreditDTO dto = new CustomerCreditDTO();
        BeanUtils.copyProperties(credit, dto);
        dto.setCreditAvailable(credit.getCreditLimit().subtract(credit.getCreditUsed()));

        return Result.success(dto);
    }

    @Override
    public Result<CreditCheckDTO> checkCredit(Long customerId, BigDecimal amount) {
        CustomerCredit credit = getCustomerCreditByCustomerId(customerId);
        if (credit == null) {
            throw new BusinessException("客户信用记录不存在");
        }

        CreditCheckDTO checkDTO = new CreditCheckDTO();
        checkDTO.setCustomerId(credit.getCustomerId());
        checkDTO.setCustomerName(credit.getCustomerName());
        checkDTO.setCreditLimit(credit.getCreditLimit());
        checkDTO.setCreditUsed(credit.getCreditUsed());
        checkDTO.setCreditAvailable(credit.getCreditLimit().subtract(credit.getCreditUsed()));
        checkDTO.setRequestAmount(amount);

        BigDecimal available = credit.getCreditLimit().subtract(credit.getCreditUsed());
        if (available.compareTo(amount) >= 0) {
            checkDTO.setIsAvailable(true);
            checkDTO.setMessage("信用额度充足");
        } else {
            checkDTO.setIsAvailable(false);
            checkDTO.setMessage("信用额度不足,可用额度: " + available);
        }

        // 检查使用率是否超过80%
        BigDecimal usageRate = credit.getCreditUsed()
                .divide(credit.getCreditLimit(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
        if (usageRate.compareTo(new BigDecimal("80")) >= 0) {
            checkDTO.setMessage(checkDTO.getMessage() + ",信用使用率已达到" + usageRate + "%,请注意");
        }

        return Result.success(checkDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateCreditUsed(Long customerId, BigDecimal amount, Boolean isAdd) {
        CustomerCredit credit = getCustomerCreditByCustomerId(customerId);
        if (credit == null) {
            throw new BusinessException("客户信用记录不存在");
        }

        BigDecimal newUsed;
        if (Boolean.TRUE.equals(isAdd)) {
            // 增加已用额度
            newUsed = credit.getCreditUsed().add(amount);
            // 检查是否超出信用额度
            if (newUsed.compareTo(credit.getCreditLimit()) > 0) {
                throw new BusinessException("超出信用额度");
            }
        } else {
            // 减少已用额度
            newUsed = credit.getCreditUsed().subtract(amount);
            if (newUsed.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("减少额度不能大于已用额度");
            }
        }

        credit.setCreditUsed(newUsed);
        updateById(credit);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateCredit(CustomerCreditDTO dto) {
        CustomerCredit credit = getCustomerCreditByCustomerId(dto.getCustomerId());
        if (credit == null) {
            throw new BusinessException("客户信用记录不存在");
        }

        credit.setCreditLimit(dto.getCreditLimit());
        credit.setCreditLevel(dto.getCreditLevel());
        credit.setCreditStatus(dto.getCreditStatus());
        credit.setRemark(dto.getRemark());

        updateById(credit);

        return Result.success();
    }

    /**
     * 根据客户ID查询信用记录
     */
    private CustomerCredit getCustomerCreditByCustomerId(Long customerId) {
        LambdaQueryWrapper<CustomerCredit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerCredit::getCustomerId, customerId)
                .eq(CustomerCredit::getDeleted, 0);
        return getOne(wrapper);
    }

    /**
     * 计算信用等级
     */
    private String calculateCreditLevel(BigDecimal creditLimit) {
        if (creditLimit.compareTo(new BigDecimal("1000000")) >= 0) {
            return "GOLD";
        } else if (creditLimit.compareTo(new BigDecimal("500000")) >= 0) {
            return "SILVER";
        } else if (creditLimit.compareTo(new BigDecimal("100000")) >= 0) {
            return "BRONZE";
        } else {
            return "NORMAL";
        }
    }
}
