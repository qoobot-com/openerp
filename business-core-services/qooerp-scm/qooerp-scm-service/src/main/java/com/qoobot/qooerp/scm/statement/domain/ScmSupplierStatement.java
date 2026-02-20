package com.qoobot.qooerp.scm.statement.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 供应商对账单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_supplier_statement")
public class ScmSupplierStatement extends BaseEntity {
    
    /** 对账单号 */
    private String statementCode;
    
    /** 供应商ID */
    private Long supplierId;
    
    /** 供应商编码 */
    private String supplierCode;
    
    /** 供应商名称 */
    private String supplierName;
    
    /** 对账类型：MONTHLY-月度,QUARTERLY-季度,YEARLY-年度 */
    private String statementType;
    
    /** 对账起始日期 */
    private LocalDate startDate;
    
    /** 对账结束日期 */
    private LocalDate endDate;
    
    /** 对账日期 */
    private LocalDate statementDate;
    
    /** 采购总金额 */
    private BigDecimal purchaseAmount;
    
    /** 退货总金额 */
    private BigDecimal returnAmount;
    
    /** 运费 */
    private BigDecimal freightAmount;
    
    /** 其他费用 */
    private BigDecimal otherAmount;
    
    /** 应付总额 */
    private BigDecimal totalAmount;
    
    /** 审核状态：PENDING-待审核,APPROVED-已通过,REJECTED-已拒绝 */
    private String auditStatus;
    
    /** 审核人 */
    private String auditor;
    
    /** 审核时间 */
    private java.time.LocalDateTime auditTime;
    
    /** 审核意见 */
    private String auditRemark;
    
    /** 结算状态：PENDING-待结算,SETTLED-已结算 */
    private String settlementStatus;
    
    /** 结算日期 */
    private LocalDate settlementDate;
    
    /** 结算方式 */
    private String settlementMethod;
}
