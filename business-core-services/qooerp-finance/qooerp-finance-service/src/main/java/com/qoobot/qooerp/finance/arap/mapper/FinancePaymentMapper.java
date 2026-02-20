package com.qoobot.qooerp.finance.arap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.finance.arap.domain.FinancePayment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 付款记录Mapper
 */
@Mapper
public interface FinancePaymentMapper extends BaseMapper<FinancePayment> {
}
