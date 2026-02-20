package com.qoobot.qooerp.scm.quotation.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.quotation.dto.QuotationComparisonDTO;
import com.qoobot.qooerp.scm.quotation.dto.SupplierQuotationDTO;
import com.qoobot.qooerp.scm.quotation.dto.SupplierQuotationQueryDTO;
import com.qoobot.qooerp.scm.quotation.service.IScmSupplierQuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 供应商报价Controller
 */
@Tag(name = "供应商报价管理", description = "供应商报价管理接口")
@RestController
@RequestMapping("/api/scm/quotations")
@RequiredArgsConstructor
public class ScmSupplierQuotationController {
    
    private final IScmSupplierQuotationService quotationService;
    
    @Operation(summary = "创建报价单")
    @PostMapping
    public Result<Long> createQuotation(@Validated @RequestBody SupplierQuotationDTO dto) {
        return quotationService.createQuotation(dto);
    }
    
    @Operation(summary = "更新报价单")
    @PutMapping("/{id}")
    public Result<Void> updateQuotation(@PathVariable Long id, @Validated @RequestBody SupplierQuotationDTO dto) {
        return quotationService.updateQuotation(id, dto);
    }
    
    @Operation(summary = "删除报价单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteQuotation(@PathVariable Long id) {
        return quotationService.deleteQuotation(id);
    }
    
    @Operation(summary = "获取报价单详情")
    @GetMapping("/{id}")
    public Result<SupplierQuotationDTO> getQuotation(@PathVariable Long id) {
        return quotationService.getQuotation(id);
    }
    
    @Operation(summary = "分页查询报价单")
    @GetMapping
    public Result<PageResult<SupplierQuotationDTO>> queryQuotations(SupplierQuotationQueryDTO queryDTO) {
        return quotationService.queryQuotations(queryDTO);
    }
    
    @Operation(summary = "审核报价单")
    @PostMapping("/{id}/approve")
    public Result<Void> approveQuotation(@PathVariable Long id, 
                                         @RequestParam String auditStatus, 
                                         @RequestParam(required = false) String auditRemark) {
        return quotationService.approveQuotation(id, auditStatus, auditRemark);
    }
    
    @Operation(summary = "报价转采购订单")
    @PostMapping("/{id}/convert")
    public Result<Map<String, Object>> convertToPurchaseOrder(@PathVariable Long id) {
        return quotationService.convertToPurchaseOrder(id);
    }
    
    @Operation(summary = "报价对比")
    @PostMapping("/compare")
    public Result<List<QuotationComparisonDTO>> compareQuotations(
            @RequestBody CompareRequest request) {
        return quotationService.compareQuotations(request.getMaterialCodes(), 
                                                   request.getStartDate(), 
                                                   request.getEndDate());
    }
    
    /**
     * 报价对比请求参数
     */
    public static class CompareRequest {
        private List<String> materialCodes;
        private LocalDate startDate;
        private LocalDate endDate;
        
        public List<String> getMaterialCodes() {
            return materialCodes;
        }
        
        public void setMaterialCodes(List<String> materialCodes) {
            this.materialCodes = materialCodes;
        }
        
        public LocalDate getStartDate() {
            return startDate;
        }
        
        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }
        
        public LocalDate getEndDate() {
            return endDate;
        }
        
        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }
    }
}
