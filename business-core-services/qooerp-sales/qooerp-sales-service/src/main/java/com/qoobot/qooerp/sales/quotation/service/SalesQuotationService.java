package com.qoobot.qooerp.sales.quotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.sales.quotation.domain.SalesQuotation;
import com.qoobot.qooerp.sales.quotation.dto.QuotationDTO;
import com.qoobot.qooerp.sales.quotation.dto.QuotationQueryDTO;

/**
 * 销售报价服务接口
 */
public interface SalesQuotationService extends IService<SalesQuotation> {

    /**
     * 创建报价单
     */
    Result<Long> createQuotation(QuotationDTO quotationDTO);

    /**
     * 审核报价单
     */
    Result<Void> approveQuotation(Long quotationId, Long approverId);

    /**
     * 发送报价单
     */
    Result<Void> sendQuotation(Long quotationId);

    /**
     * 报价转订单
     */
    Result<Long> convertToOrder(Long quotationId);

    /**
     * 查询报价单
     */
    Result<QuotationDTO> getQuotation(Long quotationId);

    /**
     * 分页查询报价单
     */
    Result<PageResult<QuotationDTO>> queryQuotations(QuotationQueryDTO queryDTO);
}
