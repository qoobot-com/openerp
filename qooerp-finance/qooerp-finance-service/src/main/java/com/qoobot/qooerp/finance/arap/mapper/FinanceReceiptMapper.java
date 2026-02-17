package com.qoobot.qooerp.finance.arap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.finance.arap.domain.FinanceReceipt;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收款记录Mapper
 */
@Mapper
public interface FinanceReceiptMapper extends BaseMapper<FinanceReceipt> {
}
