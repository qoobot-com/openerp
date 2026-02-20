package com.qoobot.qooerp.scm.quotation.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.quotation.dto.QuotationComparisonDTO;
import com.qoobot.qooerp.scm.quotation.dto.SupplierQuotationDTO;
import com.qoobot.qooerp.scm.quotation.dto.SupplierQuotationQueryDTO;

import java.util.List;

/**
 * 供应商报价Service接口
 */
public interface IScmSupplierQuotationService {
    
    /**
     * 创建报价单
     */
    Result<Long> createQuotation(SupplierQuotationDTO dto);
    
    /**
     * 更新报价单
     */
    Result<Void> updateQuotation(Long id, SupplierQuotationDTO dto);
    
    /**
     * 删除报价单
     */
    Result<Void> deleteQuotation(Long id);
    
    /**
     * 获取报价单详情
     */
    Result<SupplierQuotationDTO> getQuotation(Long id);
    
    /**
     * 分页查询报价单
     */
    Result<PageResult<SupplierQuotationDTO>> queryQuotations(SupplierQuotationQueryDTO queryDTO);
    
    /**
     * 审核报价单
     */
    Result<Void> approveQuotation(Long id, String auditStatus, String auditRemark);
    
    /**
     * 报价转采购订单
     */
    Result<java.util.Map<String, Object>> convertToPurchaseOrder(Long id);
    
    /**
     * 报价对比
     */
    Result<List<QuotationComparisonDTO>> compareQuotations(List<String> materialCodes, 
                                                           java.time.LocalDate startDate, 
                                                           java.time.LocalDate endDate);
}
