package com.qoobot.qooerp.finance.invoice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoobot.qooerp.finance.invoice.domain.FinanceInvoice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 发票Mapper接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Mapper
public interface FinanceInvoiceMapper extends BaseMapper<FinanceInvoice> {
}
