package com.qoobot.qooerp.sales.quotation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.sales.quotation.domain.SalesQuotationDetail;
import com.qoobot.qooerp.sales.quotation.mapper.SalesQuotationDetailMapper;
import com.qoobot.qooerp.sales.quotation.service.SalesQuotationDetailService;
import org.springframework.stereotype.Service;

/**
 * 销售报价明细服务实现
 */
@Service
public class SalesQuotationDetailServiceImpl extends ServiceImpl<SalesQuotationDetailMapper, SalesQuotationDetail>
        implements SalesQuotationDetailService {
}
