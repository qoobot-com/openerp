package com.qoobot.qooerp.scm.statement.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.statement.dto.StatementStatisticsDTO;
import com.qoobot.qooerp.scm.statement.dto.SupplierStatementDTO;
import com.qoobot.qooerp.scm.statement.dto.SupplierStatementQueryDTO;
import com.qoobot.qooerp.scm.statement.service.IScmSupplierStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 供应商对账Controller
 */
@Tag(name = "供应商对账管理", description = "供应商对账管理接口")
@RestController
@RequestMapping("/api/scm/statements")
@RequiredArgsConstructor
public class ScmSupplierStatementController {
    
    private final IScmSupplierStatementService statementService;
    
    @Operation(summary = "创建对账单")
    @PostMapping
    public Result<Long> createStatement(@Validated @RequestBody SupplierStatementDTO dto) {
        return statementService.createStatement(dto);
    }
    
    @Operation(summary = "自动生成对账单")
    @PostMapping("/auto")
    public Result<Map<String, Object>> autoGenerateStatement(
            @RequestParam Long supplierId,
            @RequestParam String statementType,
            @RequestParam Integer statementYear,
            @RequestParam Integer statementMonth) {
        return statementService.autoGenerateStatement(supplierId, statementType, 
                                                      statementYear, statementMonth);
    }
    
    @Operation(summary = "更新对账单")
    @PutMapping("/{id}")
    public Result<Void> updateStatement(@PathVariable Long id, @Validated @RequestBody SupplierStatementDTO dto) {
        return statementService.updateStatement(id, dto);
    }
    
    @Operation(summary = "删除对账单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteStatement(@PathVariable Long id) {
        return statementService.deleteStatement(id);
    }
    
    @Operation(summary = "获取对账单详情")
    @GetMapping("/{id}")
    public Result<SupplierStatementDTO> getStatement(@PathVariable Long id) {
        return statementService.getStatement(id);
    }
    
    @Operation(summary = "分页查询对账单")
    @GetMapping
    public Result<PageResult<SupplierStatementDTO>> queryStatements(SupplierStatementQueryDTO queryDTO) {
        return statementService.queryStatements(queryDTO);
    }
    
    @Operation(summary = "审核对账单")
    @PostMapping("/{id}/approve")
    public Result<Void> approveStatement(@PathVariable Long id, 
                                         @RequestParam String auditStatus, 
                                         @RequestParam(required = false) String auditRemark) {
        return statementService.approveStatement(id, auditStatus, auditRemark);
    }
    
    @Operation(summary = "对账结算")
    @PostMapping("/{id}/settle")
    public Result<Map<String, Object>> settleStatement(@PathVariable Long id, 
                                                       @RequestParam String settlementMethod, 
                                                       @RequestParam LocalDate settlementDate) {
        return statementService.settleStatement(id, settlementMethod, settlementDate);
    }
    
    @Operation(summary = "对账统计")
    @GetMapping("/statistics")
    public Result<StatementStatisticsDTO> getStatistics() {
        return statementService.getStatistics();
    }
}
