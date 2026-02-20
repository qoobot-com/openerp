package com.qoobot.qooerp.sales.quotation.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.sales.quotation.dto.QuotationDTO;
import com.qoobot.qooerp.sales.quotation.dto.QuotationQueryDTO;
import com.qoobot.qooerp.sales.quotation.service.SalesQuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 销售报价控制器
 */
@Tag(name = "销售报价管理", description = "销售报价相关接口")
@RestController
@RequestMapping("/api/sales/quotations")
@RequiredArgsConstructor
public class SalesQuotationController {

    private final SalesQuotationService quotationService;

    @Operation(summary = "创建报价单")
    @PostMapping
    public Result<Long> createQuotation(@RequestBody QuotationDTO quotationDTO) {
        return quotationService.createQuotation(quotationDTO);
    }

    @Operation(summary = "查询报价单")
    @GetMapping("/{id}")
    public Result<QuotationDTO> getQuotation(@PathVariable Long id) {
        return quotationService.getQuotation(id);
    }

    @Operation(summary = "分页查询报价单")
    @GetMapping
    public Result<PageResult<QuotationDTO>> queryQuotations(QuotationQueryDTO queryDTO) {
        return quotationService.queryQuotations(queryDTO);
    }

    @Operation(summary = "审核报价单")
    @PostMapping("/{id}/approve")
    public Result<Void> approveQuotation(@PathVariable Long id, @RequestParam Long approverId) {
        return quotationService.approveQuotation(id, approverId);
    }

    @Operation(summary = "发送报价单")
    @PostMapping("/{id}/send")
    public Result<Void> sendQuotation(@PathVariable Long id) {
        return quotationService.sendQuotation(id);
    }

    @Operation(summary = "报价转订单")
    @PostMapping("/{id}/convert")
    public Result<Long> convertToOrder(@PathVariable Long id) {
        return quotationService.convertToOrder(id);
    }
}
