package com.qoobot.qooerp.sales.credit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.sales.credit.domain.CustomerCredit;
import com.qoobot.qooerp.sales.credit.dto.CreditCheckDTO;
import com.qoobot.qooerp.sales.credit.dto.CustomerCreditDTO;

import java.math.BigDecimal;

/**
 * 客户信用服务接口
 */
public interface CustomerCreditService extends IService<CustomerCredit> {

    /**
     * 设置信用额度
     */
    Result<Void> setCreditLimit(Long customerId, BigDecimal creditLimit);

    /**
     * 查询客户信用
     */
    Result<CustomerCreditDTO> getCustomerCredit(Long customerId);

    /**
     * 检查信用额度
     */
    Result<CreditCheckDTO> checkCredit(Long customerId, BigDecimal amount);

    /**
     * 更新信用额度使用
     */
    Result<Void> updateCreditUsed(Long customerId, BigDecimal amount, Boolean isAdd);

    /**
     * 更新信用额度
     */
    Result<Void> updateCredit(CustomerCreditDTO dto);
}
