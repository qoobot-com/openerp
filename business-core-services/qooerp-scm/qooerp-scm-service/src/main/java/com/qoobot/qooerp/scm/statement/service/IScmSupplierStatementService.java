package com.qoobot.qooerp.scm.statement.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.statement.dto.StatementStatisticsDTO;
import com.qoobot.qooerp.scm.statement.dto.SupplierStatementDTO;
import com.qoobot.qooerp.scm.statement.dto.SupplierStatementQueryDTO;

import java.util.Map;

/**
 * 供应商对账Service接口
 */
public interface IScmSupplierStatementService {
    
    /**
     * 创建对账单
     */
    Result<Long> createStatement(SupplierStatementDTO dto);
    
    /**
     * 自动生成对账单
     */
    Result<Map<String, Object>> autoGenerateStatement(Long supplierId, String statementType, 
                                                       Integer statementYear, Integer statementMonth);
    
    /**
     * 更新对账单
     */
    Result<Void> updateStatement(Long id, SupplierStatementDTO dto);
    
    /**
     * 删除对账单
     */
    Result<Void> deleteStatement(Long id);
    
    /**
     * 获取对账单详情
     */
    Result<SupplierStatementDTO> getStatement(Long id);
    
    /**
     * 分页查询对账单
     */
    Result<PageResult<SupplierStatementDTO>> queryStatements(SupplierStatementQueryDTO queryDTO);
    
    /**
     * 审核对账单
     */
    Result<Void> approveStatement(Long id, String auditStatus, String auditRemark);
    
    /**
     * 对账结算
     */
    Result<Map<String, Object>> settleStatement(Long id, String settlementMethod, java.time.LocalDate settlementDate);
    
    /**
     * 对账统计
     */
    Result<StatementStatisticsDTO> getStatistics();
}
